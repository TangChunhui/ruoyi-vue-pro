package cn.iocoder.yudao.module.erp.controller.admin.agri;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceRespVO;
import cn.iocoder.yudao.module.erp.service.agri.ErpAgriReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
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

}
