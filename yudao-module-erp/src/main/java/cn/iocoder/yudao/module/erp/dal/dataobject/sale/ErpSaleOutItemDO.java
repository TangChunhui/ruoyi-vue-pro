package cn.iocoder.yudao.module.erp.dal.dataobject.sale;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ERP 销售出库项 DO (农资进阶版)
 *
 * @author 芋道源码
 */
@TableName("erp_sale_out_items")
@KeySequence("erp_sale_out_items_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpSaleOutItemDO extends BaseDO {

    @TableId
    private Long id;
    /**
     * 销售出库编号
     */
    private Long outId;
    /**
     * 销售订单项编号
     */
    private Long orderItemId;
    /**
     * 仓库编号
     */
    private Long warehouseId;
    /**
     * 产品编号
     */
    private Long productId;
    /**
     * 产品单位单位
     */
    private Long productUnitId;

    /**
     * 产品单位单价，单位：元
     */
    private BigDecimal productPrice;
    /**
     * 数量
     */
    private BigDecimal count;
    /**
     * 总价，单位：元
     */
    private BigDecimal totalPrice;
    /**
     * 税率，百分比
     */
    private BigDecimal taxPercent;
    /**
     * 税额，单位：元
     */
    private BigDecimal taxPrice;

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