package cn.iocoder.yudao.module.erp.controller.admin.agri;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriBatchTraceRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriPurchaseLedgerRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriRestrictedSaleRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriWarningOverviewRespVO;
import cn.iocoder.yudao.module.erp.service.agri.ErpAgriReportService;
import cn.iocoder.yudao.module.erp.framework.seetong.core.SeetongClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 农资报表 Controller
 */
@Tag(name = "管理后台 - 农资报表")
@RestController
@RequestMapping("/erp/agri-report")
@Validated
public class ErpAgriReportController {

    @Resource
    private ErpAgriReportService agriReportService;

    @GetMapping("/stock-balance")
    @Operation(summary = "获取农资收发存台账报表")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<List<ErpAgriStockBalanceRespVO>> getStockBalanceReport(@Valid ErpAgriStockBalanceReqVO reqVO) {
        return success(agriReportService.getStockBalanceReport(reqVO));
    }

    @GetMapping("/get-warning-overview")
    @Operation(summary = "获取农资运营合规预警概览")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<ErpAgriWarningOverviewRespVO> getAgriWarningOverview() {
        return success(agriReportService.getAgriWarningOverview());
    }

    @GetMapping("/restricted-sale-list")
    @Operation(summary = "获得高毒限用农资销售详细单据（电子台账）")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<List<ErpAgriRestrictedSaleRespVO>> getRestrictedSaleList(@Valid ErpAgriStockBalanceReqVO reqVO) {
        return success(agriReportService.getRestrictedSaleList(reqVO));
    }

    @GetMapping("/sales-detail-list")
    @Operation(summary = "获得全量销售明细列表（当日对账/全量追溯）")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<List<ErpAgriRestrictedSaleRespVO>> getSalesDetailList(@Valid ErpAgriStockBalanceReqVO reqVO) {
        return success(agriReportService.getSalesDetailList(reqVO));
    }

    @GetMapping("/expiring-stock-list")
    @Operation(summary = "获得临期库存清单（基于批次效期）")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<List<ErpAgriStockBalanceRespVO>> getExpiringStockList(Integer days) {
        return success(agriReportService.getExpiringStockList(days));
    }

    @GetMapping("/finance-summary")
    @Operation(summary = "获取农资财务概览（应收、今日收支流水、近15日趋势）")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFinanceSummaryRespVO> getAgriFinanceSummary() {
        return success(agriReportService.getAgriFinanceSummary());
    }

    @GetMapping("/restricted-sale-leaderboard")
    @Operation(summary = "获取高毒限用农资采购排行（合规排查）")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<List<cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriRestrictedSaleRespVO>> getRestrictedSaleLeaderboard() {
        return success(agriReportService.getRestrictedSaleLeaderboard());
    }

    @GetMapping("/supplier-license-countdown")
    @Operation(summary = "获得供货商资质到期预警列表")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<List<cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierRespVO>> getSupplierLicenseCountdown() {
        return success(agriReportService.getSupplierLicenseCountdown());
    }

    @GetMapping("/get-playback-url")
    @Operation(summary = "获得监控回放地址")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<String> getPlaybackUrl(Long bizId, String bizType, Integer preMinutes, Integer postMinutes) {
        return success(agriReportService.getPlaybackUrl(bizId, bizType, preMinutes, postMinutes));
    }

    @GetMapping("/seetong-devices")
    @Operation(summary = "获得 Seetong 摄像头设备列表")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<List<SeetongClient.DeviceVO>> getSeetongDeviceList() {
        return success(agriReportService.getSeetongDeviceList());
    }

    @GetMapping("/purchase-ledger-list")
    @Operation(summary = "获得农资购进台账列表")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<List<ErpAgriPurchaseLedgerRespVO>> getPurchaseLedgerList(@Valid ErpAgriStockBalanceReqVO reqVO) {
        return success(agriReportService.getPurchaseLedgerList(reqVO));
    }

    @GetMapping("/export-restricted-sale")
    @Operation(summary = "导出高毒限用农资销售台账 Excel")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:export')")
    public void exportRestrictedSaleExcel(@Valid ErpAgriStockBalanceReqVO reqVO,
                                          HttpServletResponse response) throws IOException {
        List<ErpAgriRestrictedSaleRespVO> list = agriReportService.getRestrictedSaleList(reqVO);
        ExcelUtils.write(response, "高毒限用农资销售台账.xls", "数据", ErpAgriRestrictedSaleRespVO.class, list);
    }

    @GetMapping("/export-sales-detail")
    @Operation(summary = "导出全量销售明细 Excel")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:export')")
    public void exportSalesDetailExcel(@Valid ErpAgriStockBalanceReqVO reqVO,
                                       HttpServletResponse response) throws IOException {
        List<ErpAgriRestrictedSaleRespVO> list = agriReportService.getSalesDetailList(reqVO);
        ExcelUtils.write(response, "农资销售明细.xls", "数据", ErpAgriRestrictedSaleRespVO.class, list);
    }

    @GetMapping("/export-purchase-ledger")
    @Operation(summary = "导出农资购进台账 Excel")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:export')")
    public void exportPurchaseLedgerExcel(@Valid ErpAgriStockBalanceReqVO reqVO,
                                          HttpServletResponse response) throws IOException {
        List<ErpAgriPurchaseLedgerRespVO> list = agriReportService.getPurchaseLedgerList(reqVO);
        ExcelUtils.write(response, "农资购进台账.xls", "数据", ErpAgriPurchaseLedgerRespVO.class, list);
    }

    @GetMapping("/batch-trace-detail")
    @Operation(summary = "获得农资批次全生命周期溯源详情")
    @PreAuthorize("@ss.hasPermission('erp:agri-report:query')")
    public CommonResult<ErpAgriBatchTraceRespVO> getBatchTraceDetail(
            @RequestParam @Parameter(description = "产品编号") Long productId,
            @RequestParam(required = false) @Parameter(description = "批次号") String batchNo) {
        return success(agriReportService.getBatchTraceDetail(productId, batchNo));
    }

}
