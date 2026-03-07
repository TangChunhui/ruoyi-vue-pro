package cn.iocoder.yudao.module.erp.controller.admin.agri;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockRecordDO;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockRecordMapper;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Tag(name = "管理后台 - 农资平账报表")
@RestController
@RequestMapping("/erp/agri-report")
@Validated
public class ErpAgriReportController {

    @Resource
    private ErpStockRecordMapper stockRecordMapper;
    @Resource
    private ErpProductService productService;
    @Resource
    private ErpStockService stockService;

    @GetMapping("/stock-balance")
    @Operation(summary = "获得进销存平账报表")
    public CommonResult<List<ErpAgriStockBalanceRespVO>> getStockBalanceReport(
            @RequestParam("beginTime") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND) LocalDateTime beginTime,
            @RequestParam("endTime") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND) LocalDateTime endTime) {
        
        List<ErpProductDO> products = productService.getProductVOListByStatus(0).stream()
                .map(vo -> productService.getProduct(vo.getId()))
                .toList();
        
        List<ErpAgriStockBalanceRespVO> report = new ArrayList<>();
        
        for (ErpProductDO product : products) {
            ErpAgriStockBalanceRespVO vo = new ErpAgriStockBalanceRespVO();
            vo.setProductId(product.getId());
            vo.setProductName(product.getName());
            vo.setStandard(product.getStandard());
            // 简单处理单位名，实际应关联 unit
            vo.setUnitName(""); 

            // 1. 计算期末库存 (当前库存 - 结束后发生的变动)
            BigDecimal currentStock = stockService.getStockCount(product.getId());
            BigDecimal changesAfterEnd = getStockChangeSum(product.getId(), endTime, LocalDateTime.now());
            BigDecimal closingStock = currentStock.subtract(changesAfterEnd);
            vo.setClosingStock(closingStock);

            // 2. 计算本期变动
            BigDecimal inbound = getStockChangeSum(product.getId(), beginTime, endTime, true);
            BigDecimal outbound = getStockChangeSum(product.getId(), beginTime, endTime, false).abs();
            vo.setInboundStock(inbound);
            vo.setOutboundStock(outbound);

            // 3. 计算期初库存
            vo.setOpeningStock(closingStock.subtract(inbound).add(outbound));
            
            report.add(vo);
        }
        
        return success(report);
    }

    private BigDecimal getStockChangeSum(Long productId, LocalDateTime begin, LocalDateTime end) {
        if (begin.isAfter(end)) return BigDecimal.ZERO;
        List<ErpStockRecordDO> records = stockRecordMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ErpStockRecordDO>()
                        .eq(ErpStockRecordDO::getProductId, productId)
                        .between(ErpStockRecordDO::getCreateTime, begin, end));
        return records.stream().map(ErpStockRecordDO::getCount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getStockChangeSum(Long productId, LocalDateTime begin, LocalDateTime end, boolean positiveOnly) {
        List<ErpStockRecordDO> records = stockRecordMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ErpStockRecordDO>()
                        .eq(ErpStockRecordDO::getProductId, productId)
                        .between(ErpStockRecordDO::getCreateTime, begin, end));
        return records.stream()
                .map(ErpStockRecordDO::getCount)
                .filter(count -> positiveOnly ? count.compareTo(BigDecimal.ZERO) > 0 : count.compareTo(BigDecimal.ZERO) < 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
