package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 农资财务概览 Response VO")
@Data
public class ErpAgriFinanceSummaryRespVO {

    @Schema(description = "总应收账款（客户欠款总计）", example = "100000.00")
    private BigDecimal totalReceivableAmount;

    @Schema(description = "今日收入金额（收款单已审合计）", example = "5000.00")
    private BigDecimal todayReceiptAmount;

    @Schema(description = "今日支出金额（付款单已审合计）", example = "2000.00")
    private BigDecimal todayPaymentAmount;

    @Schema(description = "今日净现金流", example = "3000.00")
    private BigDecimal todayNetCashFlow;

    @Schema(description = "今日销售额（销售单已审合计）", example = "8000.00")
    private BigDecimal todaySalesAmount;

    @Schema(description = "今日流水记录")
    private List<FlowItem> flowList;

    @Schema(description = "近15日每日销售统计（用于趋势图）")
    private List<DailyStat> dailyStats;

    @Data
    public static class DailyStat {
        @Schema(description = "日期，格式 MM-dd", example = "03-27")
        private String date;
        @Schema(description = "当日销售额", example = "3200.00")
        private BigDecimal revenue;
        @Schema(description = "当日销售笔数", example = "15")
        private Long count;
    }

    @Data
    public static class FlowItem {
        @Schema(description = "类型（1-收入，2-支出）", example = "1")
        private Integer type;
        @Schema(description = "单号", example = "SK20240327001")
        private String no;
        @Schema(description = "往来单位名称", example = "张三/供应商A")
        private String businessName;
        @Schema(description = "金额", example = "1000.00")
        private BigDecimal amount;
        @Schema(description = "支付账户", example = "微信支付")
        private String accountName;
        @Schema(description = "时间")
        private LocalDateTime time;
    }
}
