package cn.iocoder.yudao.module.erp.service.stock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.stock.ErpStockPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ERP 产品库存 Service 接口 (农资进阶版)
 *
 * @author 芋道源码
 */
public interface ErpStockService {

    /**
     * 获得产品库存
     */
    ErpStockDO getStock(Long id);

    /**
     * 基于产品 + 仓库，获得产品库存
     */
    ErpStockDO getStock(Long productId, Long warehouseId);

    /**
     * 基于产品 + 仓库，获得产品库存列表 (多批次支持)
     */
    List<ErpStockDO> getStockList(Long productId, Long warehouseId);

    /**
     * 基于产品 + 仓库 + 批次号，获得产品库存 (溯源进阶)
     */
    ErpStockDO getStock(Long productId, Long warehouseId, String batchNo);

    /**
     * 获得产品库存数量
     */
    BigDecimal getStockCount(Long productId);

    /**
     * 获得产品库存分页
     */
    PageResult<ErpStockDO> getStockPage(ErpStockPageReqVO pageReqVO);

    /**
     * 进阶版：增量更新产品库存数量 (带批次追溯)
     *
     * @param productId 产品编号
     * @param warehouseId 仓库编号
     * @param count 增量数量
     * @param batchNo 生产批次号
     * @param productionDate 生产日期
     * @param expiryDate 有效截止日期
     * @return 更新后的库存
     */
    BigDecimal updateStockCountIncrement(Long productId, Long warehouseId, BigDecimal count,
                                         String batchNo, LocalDateTime productionDate, LocalDateTime expiryDate);

    /**
     * 基础版：增量更新产品库存数量 (兼容旧逻辑)
     */
    BigDecimal updateStockCountIncrement(Long productId, Long warehouseId, BigDecimal count);

}