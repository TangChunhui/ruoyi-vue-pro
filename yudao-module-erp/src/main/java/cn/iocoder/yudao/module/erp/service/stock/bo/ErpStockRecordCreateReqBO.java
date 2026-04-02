package cn.iocoder.yudao.module.erp.service.stock.bo;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存明细的创建 Request BO (进阶版：支持批次溯源)
 *
 * @author 芋道源码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErpStockRecordCreateReqBO {

    /**
     * 产品编号
     */
    @NotNull(message = "产品编号不能为空")
    private Long productId;
    /**
     * 仓库编号
     */
    @NotNull(message = "仓库编号不能为空")
    private Long warehouseId;
    /**
     * 出入库数量
     *
     * 正数，表示入库；负数，表示出库
     */
    @NotNull(message = "出入库数量不能为空")
    private BigDecimal count;

    /**
     * 业务类型
     */
    @NotNull(message = "业务类型不能为空")
    private Integer bizType;
    /**
     * 业务编号
     */
    @NotNull(message = "业务编号不能为空")
    private Long bizId;
    /**
     * 业务项编号
     */
    @NotNull(message = "业务项编号不能为空")
    private Long bizItemId;
    /**
     * 业务单号
     */
    @NotNull(message = "业务单号不能为空")
    private String bizNo;

    /**
     * 生产批次号 (农资溯源标识)
     */
    private String batchNo;
    /**
     * 生产日期
     */
    private LocalDateTime productionDate;
    /**
     * 有效截止日期
     */
    private LocalDateTime expiryDate;

}
