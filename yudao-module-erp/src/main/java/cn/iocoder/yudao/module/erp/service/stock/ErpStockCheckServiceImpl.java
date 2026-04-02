package cn.iocoder.yudao.module.erp.service.stock;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.check.ErpStockCheckPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.check.ErpStockCheckSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockCheckDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockCheckItemDO;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockCheckItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockCheckMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

/**
 * ERP 库存盘点 Service 实现类 (农资进阶版)
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ErpStockCheckServiceImpl implements ErpStockCheckService {

    @Resource
    private ErpStockCheckMapper stockCheckMapper;
    @Resource
    private ErpStockCheckItemMapper stockCheckItemMapper;

    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Resource
    private ErpProductService productService;
    @Resource
    private ErpStockRecordService stockRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockCheck(ErpStockCheckSaveReqVO createReqVO) {
        // 1. 生成单号
        String no = noRedisDAO.generate(ErpNoRedisDAO.STOCK_CHECK_NO_PREFIX);
        if (stockCheckMapper.selectByNo(no) != null) {
            throw exception(STOCK_CHECK_NO_EXISTS);
        }

        // 2. 插入盘点单
        ErpStockCheckDO stockCheck = BeanUtils.toBean(createReqVO, ErpStockCheckDO.class)
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus());
        calculateTotalPrice(stockCheck, createReqVO.getItems());
        stockCheckMapper.insert(stockCheck);

        // 3. 插入盘点项
        List<ErpStockCheckItemDO> stockCheckItems = validateStockCheckItems(createReqVO.getItems());
        stockCheckItems.forEach(o -> o.setCheckId(stockCheck.getId()));
        stockCheckItemMapper.insertBatch(stockCheckItems);
        return stockCheck.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockCheck(ErpStockCheckSaveReqVO updateReqVO) {
        // 1. 校验存在
        ErpStockCheckDO stockCheck = validateStockCheckExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(stockCheck.getStatus())) {
            throw exception(STOCK_CHECK_UPDATE_FAIL_APPROVE, stockCheck.getNo());
        }

        // 2. 更新盘点单
        ErpStockCheckDO updateObj = BeanUtils.toBean(updateReqVO, ErpStockCheckDO.class);
        calculateTotalPrice(updateObj, updateReqVO.getItems());
        stockCheckMapper.updateById(updateObj);

        // 3. 更新盘点项
        updateStockCheckItemList(updateReqVO.getId(), updateReqVO.getItems());
    }

    private void calculateTotalPrice(ErpStockCheckDO stockCheck, List<ErpStockCheckSaveReqVO.Item> items) {
        stockCheck.setTotalCount(getSumValue(items, ErpStockCheckSaveReqVO.Item::getCount, BigDecimal::add, BigDecimal.ZERO));
        stockCheck.setTotalPrice(getSumValue(items, ErpStockCheckSaveReqVO.Item::getTotalPrice, BigDecimal::add, BigDecimal.ZERO));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockCheckStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1. 校验存在与状态
        ErpStockCheckDO stockCheck = validateStockCheckExists(id);
        if (stockCheck.getStatus().equals(status)) {
            throw exception(approve ? STOCK_CHECK_APPROVE_FAIL : STOCK_CHECK_PROCESS_FAIL);
        }

        // 3. 变更库存 (农资进阶：多批次盈亏核销)
        List<ErpStockCheckItemDO> stockCheckItems = stockCheckItemMapper.selectListByCheckId(id);
        stockCheckItems.forEach(stockCheckItem -> {
            if (stockCheckItem.getCount().compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            // 3.1 动态计算业务类型
            Integer bizType;
            if (approve) {
                bizType = stockCheckItem.getCount().compareTo(BigDecimal.ZERO) > 0 
                        ? ErpStockRecordBizTypeEnum.CHECK_MORE_IN.getType() 
                        : ErpStockRecordBizTypeEnum.CHECK_LESS_OUT.getType();
            } else {
                bizType = stockCheckItem.getCount().compareTo(BigDecimal.ZERO) > 0 
                        ? ErpStockRecordBizTypeEnum.CHECK_MORE_IN_CANCEL.getType() 
                        : ErpStockRecordBizTypeEnum.CHECK_LESS_OUT_CANCEL.getType();
            }

            // 3.2 记录流水
            stockRecordService.createStockRecord(ErpStockRecordCreateReqBO.builder()
                    .productId(stockCheckItem.getProductId())
                    .warehouseId(stockCheckItem.getWarehouseId())
                    .count(approve ? stockCheckItem.getCount() : stockCheckItem.getCount().negate())
                    .bizType(bizType)
                    .bizId(stockCheckItem.getCheckId())
                    .bizItemId(stockCheckItem.getId())
                    .bizNo(stockCheck.getNo())
                    .batchNo(stockCheckItem.getBatchNo())
                    .productionDate(stockCheckItem.getProductionDate())
                    .expiryDate(stockCheckItem.getExpiryDate())
                    .build());
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockCheck(List<Long> ids) {
        List<ErpStockCheckDO> stockChecks = stockCheckMapper.selectByIds(ids);
        if (CollUtil.isEmpty(stockChecks)) {
            return;
        }
        stockChecks.forEach(stockCheck -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(stockCheck.getStatus())) {
                throw exception(STOCK_CHECK_DELETE_FAIL_APPROVE, stockCheck.getNo());
            }
            stockCheckMapper.deleteById(stockCheck.getId());
            stockCheckItemMapper.deleteByCheckId(stockCheck.getId());
        });
    }

    private List<ErpStockCheckItemDO> validateStockCheckItems(List<ErpStockCheckSaveReqVO.Item> list) {
        List<ErpProductDO> productList = productService.validProductList(convertSet(list, ErpStockCheckSaveReqVO.Item::getProductId));
        Map<Long, ErpProductDO> productMap = convertMap(productList, ErpProductDO::getId);
        return convertList(list, o -> BeanUtils.toBean(o, ErpStockCheckItemDO.class, item -> item.setProductUnitId(productMap.get(item.getProductId()).getUnitId())));
    }

    private void updateStockCheckItemList(Long id, List<ErpStockCheckSaveReqVO.Item> list) {
        List<ErpStockCheckItemDO> oldList = stockCheckItemMapper.selectListByCheckId(id);
        List<ErpStockCheckItemDO> newList = validateStockCheckItems(list);
        List<List<ErpStockCheckItemDO>> diffList = diffList(oldList, newList, (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setCheckId(id));
            stockCheckItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            stockCheckItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            stockCheckItemMapper.deleteByIds(convertList(diffList.get(2), ErpStockCheckItemDO::getId));
        }
    }

    private ErpStockCheckDO validateStockCheckExists(Long id) {
        ErpStockCheckDO stockCheck = stockCheckMapper.selectById(id);
        if (stockCheck == null) {
            throw exception(STOCK_CHECK_NOT_EXISTS);
        }
        return stockCheck;
    }

    @Override
    public ErpStockCheckDO getStockCheck(Long id) {
        return stockCheckMapper.selectById(id);
    }

    @Override
    public PageResult<ErpStockCheckDO> getStockCheckPage(ErpStockCheckPageReqVO pageReqVO) {
        return stockCheckMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ErpStockCheckItemDO> getStockCheckItemListByCheckId(Long checkId) {
        return stockCheckItemMapper.selectListByCheckId(checkId);
    }

    @Override
    public List<ErpStockCheckItemDO> getStockCheckItemListByCheckIds(Collection<Long> checkIds) {
        return stockCheckItemMapper.selectListByCheckIds(checkIds);
    }
}
