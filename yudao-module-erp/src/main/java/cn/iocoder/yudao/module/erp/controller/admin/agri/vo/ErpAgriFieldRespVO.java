package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ErpAgriFieldRespVO {
    private Long id;
    private Long memberId;
    private String name;
    private BigDecimal area;
    private String location;
    private String crop;
    private String soilType;
    private LocalDateTime sowDate;
    private String growthStage;
    private String remark;
    private LocalDateTime createTime;
}
