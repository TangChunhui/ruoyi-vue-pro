package cn.iocoder.yudao.module.erp.dal.dataobject.stock;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ERP 其它入库单项 DO (农资进阶版)
 *
 * @author 芋道源码
 */
@TableName("erp_stock_in_item")
@KeySequence("erp_stock_in_item_seq") 
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpStockInItemDO extends BaseDO {

    @TableId
    private Long id;
    /**
     * 入库编号
     */
    private Long inId;
    /**
     * 仓库编号
     */
    private Long warehouseId;
    /**
     * 产品编号
     */
    private Long productId;
    /**
     * 产品单位编号
     */
    private Long productUnitId;
    /**
     * 产品单价
     */
    private BigDecimal productPrice;
    /**
     * 产品数量
     */
    private BigDecimal count;
    /**
     * 合计金额，单位：元
     */
    private BigDecimal totalPrice;

    /**
     * 生产批次号 (进阶版：溯源追溯)
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

    /**
     * 备注
     */
    private String remark;

}