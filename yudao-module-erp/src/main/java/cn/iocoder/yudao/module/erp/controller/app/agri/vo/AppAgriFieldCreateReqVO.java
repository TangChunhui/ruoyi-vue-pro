package cn.iocoder.yudao.module.erp.controller.app.agri.vo;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppAgriFieldCreateReqVO {
    @NotNull(message = "地块名称不能为空")
    private String name;
    @NotNull(message = "面积不能为空")
    private BigDecimal area;
    private String location;
    private String crop;
    private String soilType;
    private LocalDateTime sowDate;
    private String growthStage;
    private String remark;
}
