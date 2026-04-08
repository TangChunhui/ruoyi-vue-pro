package cn.iocoder.yudao.module.erp.controller.app.agri.vo;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppAgriServiceOrderCreateReqVO {
    @NotNull(message = "服务类型不能为空")
    private String serviceType;
    private Long fieldId;
    private BigDecimal serviceArea;
    @NotNull(message = "期望作业日期不能为空")
    private LocalDateTime expectDate;
    @NotNull(message = "联系人不能为空")
    private String contactName;
    @NotNull(message = "联系电话不能为空")
    private String contactMobile;
    private String address;
    private String remark;
}
