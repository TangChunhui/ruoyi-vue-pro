package cn.iocoder.yudao.module.erp.dal.dataobject.agri;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("erp_agri_service_order")
@KeySequence("erp_agri_service_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpAgriServiceOrderDO extends BaseDO {
    @TableId
    private Long id;
    private Long memberId;
    private String serviceType;
    private Integer status;
    private Long fieldId;
    private BigDecimal serviceArea;
    private LocalDateTime expectDate;
    private String contactName;
    private String contactMobile;
    private String address;
    private BigDecimal price;
    private String remark;
}
