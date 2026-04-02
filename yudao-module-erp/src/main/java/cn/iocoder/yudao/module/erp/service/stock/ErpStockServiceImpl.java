package cn.iocoder.yudao.module.erp.service.stock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.stock.ErpStockPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockMapper;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.STOCK_COUNT_NEGATIVE;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.STOCK_COUNT_NEGATIVE2;

/**
 * ERP 产品库存 Service 实现类 (农资进阶版)
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ErpStockServiceImpl implements ErpStockService {

    private static final Boolean NEGATIVE_STOCK_COUNT_ENABLE = false;

    @Resource
    private ErpProductService productService;
    @Resource
    private ErpWarehouseService warehouseService;
    @Resource
    private ErpStockMapper stockMapper;

    @Override
    public ErpStockDO getStock(Long id) {
        return stockMapper.selectById(id);
    }

    @Override
    public ErpStockDO getStock(Long productId, Long warehouseId) {
        return stockMapper.selectByProductIdAndWarehouseId(productId, warehouseId);
    }

    @Override
    public List<ErpStockDO> getStockList(Long productId, Long warehouseId) {
        return stockMapper.selectListByProductIdAndWarehouseId(productId, warehouseId);
    }

    @Override
    public ErpStockDO getStock(Long productId, Long warehouseId, String batchNo) {
        return stockMapper.selectByProductIdAndWarehouseIdAndBatchNo(productId, warehouseId, batchNo);
    }

    @Override
    public BigDecimal getStockCount(Long productId) {
        BigDecimal count = stockMapper.selectSumByProductId(productId);
        return count != null ? count : BigDecimal.ZERO;
    }

    @Override
    public PageResult<ErpStockDO> getStockPage(ErpStockPageReqVO pageReqVO) {
        return stockMapper.selectPage(pageReqVO);
    }

    @Override
    public BigDecimal updateStockCountIncrement(Long productId, Long warehouseId, BigDecimal count,
                                                 String batchNo, LocalDateTime productionDate, LocalDateTime expiryDate) {
        // 1.定位或创建匹配批次的库存主档
        ErpStockDO stock = stockMapper.selectByProductIdAndWarehouseIdAndBatchNo(productId, warehouseId, batchNo);
        if (stock == null) {
            stock = ErpStockDO.builder()
                    .productId(productId)
                    .warehouseId(warehouseId)
                    .count(BigDecimal.ZERO)
                    .batchNo(batchNo)
                    .productionDate(productionDate)
                    .expiryDate(expiryDate)
                    .build();
            stockMapper.insert(stock);
        }

        // 2. 校验库存充足性 (如果是负增量，即出库)
        if (!NEGATIVE_STOCK_COUNT_ENABLE && stock.getCount().add(count).compareTo(BigDecimal.ZERO) < 0) {
            throw exception(STOCK_COUNT_NEGATIVE, productService.getProduct(productId).getName(),
                    warehouseService.getWarehouse(warehouseId).getName(), stock.getCount(), count);
        }

        // 3. 执行原子变更
        int updateCount = stockMapper.updateCountIncrement(stock.getId(), count, NEGATIVE_STOCK_COUNT_ENABLE);
        if (updateCount == 0) {
            throw exception(STOCK_COUNT_NEGATIVE2, productService.getProduct(productId).getName(),
                    warehouseService.getWarehouse(warehouseId).getName());
        }

        // 4. 返回叠加后的结果
        return stock.getCount().add(count);
    }

    @Override
    public BigDecimal updateStockCountIncrement(Long productId, Long warehouseId, BigDecimal count) {
        // 调用带批次的逻辑，默认为空批次
        return updateStockCountIncrement(productId, warehouseId, count, null, null, null);
    }

}