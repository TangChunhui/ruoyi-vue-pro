package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ErpAgriServiceOrderRespVO {
    private Long id;
    private Long memberId;
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
    private LocalDateTime createTime;
}
