package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ErpAgriServiceOrderUpdateReqVO {
    @NotNull(message = "编号不能为空")
    private Long id;
    private Integer status;
    private BigDecimal price;
    private String remark;
}
