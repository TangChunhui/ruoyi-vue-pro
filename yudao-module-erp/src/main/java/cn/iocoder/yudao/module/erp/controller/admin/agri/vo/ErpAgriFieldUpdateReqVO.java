package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ErpAgriFieldUpdateReqVO {
    @NotNull(message = "编号不能为空")
    private Long id;
    private String name;
    private BigDecimal area;
    private String location;
    private String crop;
    private String soilType;
    private LocalDateTime sowDate;
    private String growthStage;
    private String remark;
}
