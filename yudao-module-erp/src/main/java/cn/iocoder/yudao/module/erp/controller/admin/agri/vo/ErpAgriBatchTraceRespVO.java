package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 农资批次全生命周期溯源数据 Response VO
 */
@Schema(description = "管理后台 - 农资批次溯源 Response VO")
@Data
public class ErpAgriBatchTraceRespVO {

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "批次号")
    private String batchNo;

    @Schema(description = "单位")
    private String unitName;

    @Schema(description = "是否高毒限用")
    private Boolean isRestricted;

    @Schema(description = "生产日期")
    private LocalDateTime productionDate;

    @Schema(description = "有效期至")
    private LocalDateTime expiryDate;

    // —— 进货来源 ——
    @Schema(description = "最早一笔采购时间")
    private LocalDateTime firstInTime;

    @Schema(description = "最早一笔采购单号")
    private String firstInNo;

    @Schema(description = "供货商名称")
    private String supplierName;

    @Schema(description = "批次累计入库数量")
    private BigDecimal totalInCount;

    // —— 仓储信息 ——
    @Schema(description = "所在仓库名称")
    private String warehouseName;

    @Schema(description = "当前库存数量")
    private BigDecimal currentStock;

    // —— 销售流向 ——
    @Schema(description = "批次累计销售数量")
    private BigDecimal totalOutCount;

    @Schema(description = "销售单数量")
    private Long salesCount;

    @Schema(description = "主要购买客户（最多3个名称，逗号分隔）")
    private String mainCustomerNames;

    @Schema(description = "是否存在视频存证")
    private Boolean hasVideo;

}
