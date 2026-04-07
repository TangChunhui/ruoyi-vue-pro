package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 农资购进台账 Response VO
 */
@Schema(description = "管理后台 - 农资购进台账 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpAgriPurchaseLedgerRespVO {

    @Schema(description = "采购单号")
    @ExcelProperty("采购单号")
    private String orderNo;

    @Schema(description = "采购时间")
    @ExcelProperty("采购时间")
    private LocalDateTime orderTime;

    @Schema(description = "供货商名称")
    @ExcelProperty("供货商")
    private String supplierName;

    @Schema(description = "产品名称")
    @ExcelProperty("产品名称")
    private String productName;

    @Schema(description = "农药登记证号")
    @ExcelProperty("登记证号")
    private String registrationNo;

    @Schema(description = "产品规格")
    @ExcelProperty("规格")
    private String standard;

    @Schema(description = "农资类型（1-农药，2-化肥，3-种子）")
    private Integer agriType;

    @Schema(description = "是否高毒限用")
    private Integer isRestricted;

    @Schema(description = "数量")
    @ExcelProperty("数量")
    private BigDecimal count;

    @Schema(description = "单位")
    @ExcelProperty("单位")
    private String unitName;

    @Schema(description = "批次号")
    @ExcelProperty("批次号")
    private String batchNo;

    @Schema(description = "生产日期")
    @ExcelProperty("生产日期")
    private LocalDateTime productionDate;

    @Schema(description = "有效期至")
    @ExcelProperty("有效期至")
    private LocalDateTime expiryDate;

    @Schema(description = "采购单价")
    @ExcelProperty("单价(元)")
    private BigDecimal productPrice;

    @Schema(description = "采购金额")
    @ExcelProperty("金额(元)")
    private BigDecimal totalPrice;

}
