package cn.iocoder.yudao.module.erp.dal.mysql.stock;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.stock.ErpStockPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ERP 产品库存 Mapper (农资进阶版)
 *
 * @author 芋道源码
 */
@Mapper
public interface ErpStockMapper extends BaseMapperX<ErpStockDO> {

    default PageResult<ErpStockDO> selectPage(ErpStockPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpStockDO>()
                .eqIfPresent(ErpStockDO::getProductId, reqVO.getProductId())
                .eqIfPresent(ErpStockDO::getWarehouseId, reqVO.getWarehouseId())
                .orderByDesc(ErpStockDO::getId));
    }

    /**
     * 按产品 + 仓库查询 (兼容旧逻辑，优先返回即将过期的批次)
     */
    default ErpStockDO selectByProductIdAndWarehouseId(Long productId, Long warehouseId) {
        return selectOne(new LambdaQueryWrapperX<ErpStockDO>()
                .eq(ErpStockDO::getProductId, productId)
                .eq(ErpStockDO::getWarehouseId, warehouseId)
                .orderByAsc(ErpStockDO::getExpiryDate)
                .last("LIMIT 1"));
    }

    /**
     * 按产品 + 仓库查询全部批次列表 (溯源核心：先进先出排序)
     */
    default List<ErpStockDO> selectListByProductIdAndWarehouseId(Long productId, Long warehouseId) {
        return selectList(new LambdaQueryWrapperX<ErpStockDO>()
                .eq(ErpStockDO::getProductId, productId)
                .eq(ErpStockDO::getWarehouseId, warehouseId)
                .gt(ErpStockDO::getCount, 0)
                .orderByAsc(ErpStockDO::getExpiryDate)
                .orderByAsc(ErpStockDO::getBatchNo));
    }

    /**
     * 精准按 产品 + 仓库 + 批次 查询 (溯源核心)
     */
    default ErpStockDO selectByProductIdAndWarehouseIdAndBatchNo(Long productId, Long warehouseId, String batchNo) {
        return selectOne(new LambdaQueryWrapperX<ErpStockDO>()
                .eq(ErpStockDO::getProductId, productId)
                .eq(ErpStockDO::getWarehouseId, warehouseId)
                .eq(ErpStockDO::getBatchNo, batchNo));
    }

    default int updateCountIncrement(Long id, BigDecimal count, boolean negativeEnable) {
        LambdaUpdateWrapper<ErpStockDO> updateWrapper = new LambdaUpdateWrapper<ErpStockDO>()
                .eq(ErpStockDO::getId, id);
        if (count.compareTo(BigDecimal.ZERO) > 0) {
            updateWrapper.setSql("count = count + " + count);
        } else if (count.compareTo(BigDecimal.ZERO) < 0) {
            if (!negativeEnable) {
                updateWrapper.ge(ErpStockDO::getCount, count.abs());
            }
            updateWrapper.setSql("count = count - " + count.abs());
        }
        return update(null, updateWrapper);
    }

    default BigDecimal selectSumByProductId(Long productId) {
        List<Map<String, Object>> result = selectMaps(new QueryWrapper<ErpStockDO>()
                .select("SUM(count) AS sum_count")
                .eq("product_id", productId));
        if (CollUtil.isEmpty(result)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(MapUtil.getDouble(result.get(0), "sum_count", 0D));
    }

}