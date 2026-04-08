package cn.iocoder.yudao.module.erp.dal.dataobject.agri;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("erp_agri_field_record")
@KeySequence("erp_agri_field_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpAgriFieldRecordDO extends BaseDO {
    @TableId
    private Long id;
    private Long fieldId;
    private Long memberId;
    private String type;
    private String product;
    private String dosage;
    private LocalDateTime operateDate;
    private String remark;
}
