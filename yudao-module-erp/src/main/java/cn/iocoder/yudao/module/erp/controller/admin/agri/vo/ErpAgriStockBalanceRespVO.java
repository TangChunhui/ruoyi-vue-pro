package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 农资收发存台账 Response VO
 */
@Schema(description = "管理后台 - 农资收发存台账 Response VO")
@Data
public class ErpAgriStockBalanceRespVO {

    @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long productId;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "多菌灵")
    private String productName;

    @Schema(description = "规格型号", example = "500g/瓶")
    private String standard;

    @Schema(description = "单位", example = "瓶")
    private String unitName;

    @Schema(description = "期初库存数量", example = "100.00")
    private BigDecimal openingStock;

    @Schema(description = "本期入库数量", example = "200.00")
    private BigDecimal inboundStock;

    @Schema(description = "本期出库数量", example = "150.00")
    private BigDecimal outboundStock;

    @Schema(description = "期末库存数量", example = "150.00")
    private BigDecimal closingStock;

}
