package cn.iocoder.yudao.module.erp.controller.app.agri.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppAgriServiceOrderRespVO {
    private Long id;
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
