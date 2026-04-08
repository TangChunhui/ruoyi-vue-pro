package cn.iocoder.yudao.module.erp.controller.app.agri.vo;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AppAgriFieldRecordCreateReqVO {
    @NotNull(message = "地块编号不能为空")
    private Long fieldId;
    @NotNull(message = "农事类型不能为空")
    private String type;
    private String product;
    private String dosage;
    @NotNull(message = "作业日期不能为空")
    private LocalDateTime operateDate;
    private String remark;
}
