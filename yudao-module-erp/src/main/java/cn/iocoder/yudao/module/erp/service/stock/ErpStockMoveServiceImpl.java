package cn.iocoder.yudao.module.erp.service.stock;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.move.ErpStockMovePageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.move.ErpStockMoveSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockMoveDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockMoveItemDO;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockMoveItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockMoveMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

/**
 * ERP 库存调拨 Service 实现类 (农资进阶版)
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ErpStockMoveServiceImpl implements ErpStockMoveService {

    @Resource
    private ErpStockMoveMapper stockMoveMapper;
    @Resource
    private ErpStockMoveItemMapper stockMoveItemMapper;

    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Resource
    private ErpStockRecordService stockRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockMove(ErpStockMoveSaveReqVO createReqVO) {
        // 1. 生成单号
        String no = noRedisDAO.generate(ErpNoRedisDAO.STOCK_MOVE_NO_PREFIX);
        if (stockMoveMapper.selectByNo(no) != null) {
            throw exception(STOCK_MOVE_NO_EXISTS);
        }

        // 2. 插入调拨单
        ErpStockMoveDO stockMove = BeanUtils.toBean(createReqVO, ErpStockMoveDO.class)
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus());
        stockMoveMapper.insert(stockMove);

        // 3. 插入调拨项
        List<ErpStockMoveItemDO> stockMoveItems = BeanUtils.toBean(createReqVO.getItems(), ErpStockMoveItemDO.class);
        stockMoveItems.forEach(o -> o.setMoveId(stockMove.getId()));
        stockMoveItemMapper.insertBatch(stockMoveItems);
        return stockMove.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockMove(ErpStockMoveSaveReqVO updateReqVO) {
        // 1. 校验存在
        ErpStockMoveDO stockMove = validateStockMoveExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(stockMove.getStatus())) {
            throw exception(STOCK_MOVE_UPDATE_FAIL_APPROVE, stockMove.getNo());
        }

        // 2. 更新调拨单
        ErpStockMoveDO updateObj = BeanUtils.toBean(updateReqVO, ErpStockMoveDO.class);
        stockMoveMapper.updateById(updateObj);

        // 3. 更新调拨项
        updateStockMoveItemList(updateReqVO.getId(), BeanUtils.toBean(updateReqVO.getItems(), ErpStockMoveItemDO.class));
    }

    private void updateStockMoveItemList(Long moveId, List<ErpStockMoveItemDO> newList) {
        List<ErpStockMoveItemDO> oldList = stockMoveItemMapper.selectListByMoveId(moveId);
        List<List<ErpStockMoveItemDO>> diffList = diffList(oldList, newList, (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setMoveId(moveId));
            stockMoveItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            stockMoveItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            stockMoveItemMapper.deleteByIds(convertList(diffList.get(2), ErpStockMoveItemDO::getId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockMoveStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1. 校验存在与状态
        ErpStockMoveDO stockMove = validateStockMoveExists(id);
        if (stockMove.getStatus().equals(status)) {
            throw exception(approve ? STOCK_MOVE_APPROVE_FAIL : STOCK_MOVE_PROCESS_FAIL);
        }

        // 2. 更新状态
        int updateCount = stockMoveMapper.updateByIdAndStatus(id, stockMove.getStatus(), new ErpStockMoveDO().setStatus(status));
        if (updateCount == 0) {
            throw exception(approve ? STOCK_MOVE_APPROVE_FAIL : STOCK_MOVE_PROCESS_FAIL);
        }

        // 3. 变更库存 (农资进阶：批次平移)
        List<ErpStockMoveItemDO> stockMoveItems = stockMoveItemMapper.selectListByMoveId(id);
        
        stockMoveItems.forEach(stockMoveItem -> {
            // 3.1 动态确定业务类型 (出库/入库 分别判定)
            Integer bizTypeOut = approve ? ErpStockRecordBizTypeEnum.MOVE_OUT.getType() : ErpStockRecordBizTypeEnum.MOVE_OUT_CANCEL.getType();
            Integer bizTypeIn = approve ? ErpStockRecordBizTypeEnum.MOVE_IN.getType() : ErpStockRecordBizTypeEnum.MOVE_IN_CANCEL.getType();

            // 3.2 调出仓库 (负增量)
            stockRecordService.createStockRecord(ErpStockRecordCreateReqBO.builder()
                    .productId(stockMoveItem.getProductId())
                    .warehouseId(stockMoveItem.getFromWarehouseId())
                    .count(approve ? stockMoveItem.getCount().negate() : stockMoveItem.getCount())
                    .bizType(bizTypeOut)
                    .bizId(stockMoveItem.getMoveId())
                    .bizItemId(stockMoveItem.getId())
                    .bizNo(stockMove.getNo())
                    .batchNo(stockMoveItem.getBatchNo())
                    .productionDate(stockMoveItem.getProductionDate())
                    .expiryDate(stockMoveItem.getExpiryDate())
                    .build());
            // 3.3 调入仓库 (正增量)
            stockRecordService.createStockRecord(ErpStockRecordCreateReqBO.builder()
                    .productId(stockMoveItem.getProductId())
                    .warehouseId(stockMoveItem.getToWarehouseId())
                    .count(approve ? stockMoveItem.getCount() : stockMoveItem.getCount().negate())
                    .bizType(bizTypeIn)
                    .bizId(stockMoveItem.getMoveId())
                    .bizItemId(stockMoveItem.getId())
                    .bizNo(stockMove.getNo())
                    .batchNo(stockMoveItem.getBatchNo())
                    .productionDate(stockMoveItem.getProductionDate())
                    .expiryDate(stockMoveItem.getExpiryDate())
                    .build());
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockMove(List<Long> ids) {
        List<ErpStockMoveDO> stockMoves = stockMoveMapper.selectByIds(ids);
        if (CollUtil.isEmpty(stockMoves)) {
            return;
        }
        stockMoves.forEach(stockMove -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(stockMove.getStatus())) {
                throw exception(STOCK_MOVE_DELETE_FAIL_APPROVE, stockMove.getNo());
            }
            stockMoveMapper.deleteById(stockMove.getId());
            stockMoveItemMapper.deleteByMoveId(stockMove.getId());
        });
    }

    private ErpStockMoveDO validateStockMoveExists(Long id) {
        ErpStockMoveDO stockMove = stockMoveMapper.selectById(id);
        if (stockMove == null) {
            throw exception(STOCK_MOVE_NOT_EXISTS);
        }
        return stockMove;
    }

    @Override
    public ErpStockMoveDO getStockMove(Long id) {
        return stockMoveMapper.selectById(id);
    }

    @Override
    public PageResult<ErpStockMoveDO> getStockMovePage(ErpStockMovePageReqVO pageReqVO) {
        return stockMoveMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ErpStockMoveItemDO> getStockMoveItemListByMoveId(Long moveId) {
        return stockMoveItemMapper.selectListByMoveId(moveId);
    }

    @Override
    public List<ErpStockMoveItemDO> getStockMoveItemListByMoveIds(Collection<Long> moveIds) {
        return stockMoveItemMapper.selectListByMoveIds(moveIds);
    }
}
