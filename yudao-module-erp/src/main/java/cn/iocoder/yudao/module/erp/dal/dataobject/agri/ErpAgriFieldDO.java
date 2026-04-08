package cn.iocoder.yudao.module.erp.dal.dataobject.agri;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("erp_agri_field")
@KeySequence("erp_agri_field_seq") 
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpAgriFieldDO extends BaseDO {
    @TableId
    private Long id;
    private Long memberId;
    private String name;
    private BigDecimal area;
    private String location;
    private String crop;
    private String soilType;
    private LocalDateTime sowDate;
    private String growthStage;
    private String remark;
}
