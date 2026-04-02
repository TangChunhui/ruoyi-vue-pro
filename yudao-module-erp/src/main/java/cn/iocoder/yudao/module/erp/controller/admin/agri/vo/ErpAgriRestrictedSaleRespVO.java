package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 农资限用农药销售台账 Response VO")
@Data
public class ErpAgriRestrictedSaleRespVO {

    @Schema(description = "销售单号", example = "XS20240315001")
    private String orderNo;

    @Schema(description = "销售时间")
    private LocalDateTime orderTime;

    @Schema(description = "产品名称", example = "毒死蜱")
    private String productName;

    @Schema(description = "农药登记证号", example = "PD20150001")
    private String registrationNo;

    @Schema(description = "产品规格", example = "500ml/瓶")
    private String standard;

    @Schema(description = "销售数量", example = "10.00")
    private BigDecimal count;

    @Schema(description = "单位", example = "瓶")
    private String unitName;

    @Schema(description = "批次号", example = "B2403151")
    private String batchNo;

    @Schema(description = "生产日期")
    private LocalDateTime productionDate;

    @Schema(description = "有效期至")
    private LocalDateTime expiryDate;

    @Schema(description = "客户名称", example = "张三")
    private String customerName;

    @Schema(description = "购买人身份证", example = "350102199001010011")
    private String buyerIdCard;

    @Schema(description = "用途/对象", example = "防治稻飞虱")
    private String usageIntent;

    @Schema(description = "建议用量", example = "每亩50ml")
    private String dosageAdvice;

}
