package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 农资预警概览 Response VO")
@Data
public class ErpAgriWarningOverviewRespVO {

    @Schema(description = "登记证 30天内到期的产品数量")
    private Long expiringProductCount;

    @Schema(description = "许可证已失效供应商数量")
    private Long expiredSupplierCount;

    @Schema(description = "欠款预警客户数量 (超额或额度 > 80%)")
    private Long warningCustomerCount;

    @Schema(description = "本月受控农药销售次数")
    private Long monthlyRestrictedSaleCount;

    @Schema(description = "库存总市值 (当前仓库全部资产)")
    private java.math.BigDecimal totalStockValue;

    @Schema(description = "今日本店销售总额")
    private java.math.BigDecimal todaySalesAmount;

    @Schema(description = "本月本店销售总额")
    private java.math.BigDecimal monthSalesAmount;

}
