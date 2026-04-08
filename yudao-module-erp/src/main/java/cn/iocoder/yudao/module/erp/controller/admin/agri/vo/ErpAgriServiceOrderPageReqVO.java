package cn.iocoder.yudao.module.erp.controller.admin.agri.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpAgriServiceOrderPageReqVO extends PageParam {
    private String serviceType;
    private Integer status;
    private String contactName;
    private String contactMobile;
}
