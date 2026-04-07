package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 农资限用农药销售台账 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpAgriRestrictedSaleRespVO {

    @Schema(description = "销售单号", example = "XS20240315001")
    @ExcelProperty("销售单号")
    private String orderNo;

    @Schema(description = "销售时间")
    @ExcelProperty("销售时间")
    private LocalDateTime orderTime;

    @Schema(description = "产品名称", example = "毒死蜱")
    @ExcelProperty("产品名称")
    private String productName;

    @Schema(description = "农药登记证号", example = "PD20150001")
    @ExcelProperty("登记证号")
    private String registrationNo;

    @Schema(description = "产品规格", example = "500ml/瓶")
    @ExcelProperty("规格")
    private String standard;

    @Schema(description = "销售数量", example = "10.00")
    @ExcelProperty("数量")
    private BigDecimal count;

    @Schema(description = "单位", example = "瓶")
    @ExcelProperty("单位")
    private String unitName;

    @Schema(description = "批次号", example = "B2403151")
    @ExcelProperty("批次号")
    private String batchNo;

    @Schema(description = "生产日期")
    @ExcelProperty("生产日期")
    private LocalDateTime productionDate;

    @Schema(description = "有效期至")
    @ExcelProperty("有效期至")
    private LocalDateTime expiryDate;

    @Schema(description = "客户名称", example = "张三")
    @ExcelProperty("购买人")
    private String customerName;

    @Schema(description = "购买人身份证", example = "350102199001010011")
    @ExcelProperty("身份证号")
    private String buyerIdCard;

    @Schema(description = "用途/对象", example = "防治稻飞虱")
    @ExcelProperty("防治对象")
    private String usageIntent;

    @Schema(description = "建议用量", example = "每亩50ml")
    @ExcelProperty("建议用量")
    private String dosageAdvice;

    @Schema(description = "产品单价")
    @ExcelProperty("单价(元)")
    private java.math.BigDecimal productPrice;

    @Schema(description = "销售金额")
    @ExcelProperty("金额(元)")
    private java.math.BigDecimal totalPrice;

    @Schema(description = "农资类型（1-农药，2-化肥，3-种子）")
    private Integer agriType;

    @Schema(description = "是否高毒限用")
    private Integer isRestricted;

}
