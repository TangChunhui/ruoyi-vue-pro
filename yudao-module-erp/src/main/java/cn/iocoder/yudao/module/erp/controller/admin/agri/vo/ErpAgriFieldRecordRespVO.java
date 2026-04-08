package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErpAgriFieldRecordRespVO {
    private Long id;
    private Long fieldId;
    private Long memberId;
    private String type;
    private String product;
    private String dosage;
    private LocalDateTime operateDate;
    private String remark;
    private LocalDateTime createTime;
}
