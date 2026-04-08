package cn.iocoder.yudao.module.erp.controller.app.agri.vo;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppAgriFieldRecordRespVO {
    private Long id;
    private Long fieldId;
    private String type;
    private String product;
    private String dosage;
    private LocalDateTime operateDate;
    private String remark;
}
