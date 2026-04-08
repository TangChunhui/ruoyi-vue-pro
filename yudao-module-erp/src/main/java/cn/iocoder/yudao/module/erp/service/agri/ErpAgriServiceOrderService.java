package cn.iocoder.yudao.module.erp.service.agri;

import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriServiceOrderDO;

import java.util.List;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriServiceOrderPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriServiceOrderUpdateReqVO;

public interface ErpAgriServiceOrderService {

    List<ErpAgriServiceOrderDO> getServiceOrderList(Long memberId);

    Long createServiceOrder(ErpAgriServiceOrderDO createReqDO);

    // Admin APIs
    PageResult<ErpAgriServiceOrderDO> getServiceOrderPage(ErpAgriServiceOrderPageReqVO pageReqVO);

    void updateServiceOrder(ErpAgriServiceOrderUpdateReqVO updateReqVO);

    void deleteServiceOrder(Long id);
}
