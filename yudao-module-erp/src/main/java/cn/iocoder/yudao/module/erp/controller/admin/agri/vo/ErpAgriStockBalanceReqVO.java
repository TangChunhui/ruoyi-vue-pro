package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 农资收发存台账 Request VO
 */
@Schema(description = "管理后台 - 农资收发存台账 Request VO")
@Data
public class ErpAgriStockBalanceReqVO {

    @Schema(description = "统计开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime beginTime;

    @Schema(description = "统计结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime endTime;

    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

    @Schema(description = "产品编号", example = "1")
    private Long productId;

}
