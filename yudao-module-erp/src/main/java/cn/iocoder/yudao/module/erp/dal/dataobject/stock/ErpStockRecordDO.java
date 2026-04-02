package cn.iocoder.yudao.module.erp.dal.dataobject.stock;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ERP 产品库存明细 DO (进阶版：全生命周期溯源)
 *
 * @author 芋道源码
 */
@TableName("erp_stock_record")
@KeySequence("erp_stock_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpStockRecordDO extends BaseDO {

    @TableId
    private Long id;
    /**
     * 产品编号
     */
    private Long productId;
    /**
     * 仓库编号
     */
    private Long warehouseId;
    /**
     * 出入库数量
     */
    private BigDecimal count;
    /**
     * 对应批次的总库存量 (快照)
     */
    private BigDecimal totalCount;
    /**
     * 业务类型
     */
    private Integer bizType;
    /**
     * 业务编号
     */
    private Long bizId;
    /**
     * 业务项编号
     */
    private Long bizItemId;
    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 生产批次号 (溯源链条核心)
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