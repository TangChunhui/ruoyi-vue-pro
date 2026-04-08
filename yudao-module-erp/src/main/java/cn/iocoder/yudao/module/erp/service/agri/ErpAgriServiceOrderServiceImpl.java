package cn.iocoder.yudao.module.erp.service.agri;

import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriServiceOrderDO;
import cn.iocoder.yudao.module.erp.dal.mysql.agri.ErpAgriServiceOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

@Service
@Validated
public class ErpAgriServiceOrderServiceImpl implements ErpAgriServiceOrderService {

    @Resource
    private ErpAgriServiceOrderMapper agriServiceOrderMapper;

    @Override
    public List<ErpAgriServiceOrderDO> getServiceOrderList(Long memberId) {
        return agriServiceOrderMapper.selectListByMemberId(memberId);
    }

    @Override
    public Long createServiceOrder(ErpAgriServiceOrderDO createReqDO) {
        // init status
        createReqDO.setStatus(0); // 待接单
        agriServiceOrderMapper.insert(createReqDO);
        return createReqDO.getId();
    }

    @Override
    public cn.iocoder.yudao.framework.common.pojo.PageResult<ErpAgriServiceOrderDO> getServiceOrderPage(cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriServiceOrderPageReqVO pageReqVO) {
        return agriServiceOrderMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateServiceOrder(cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriServiceOrderUpdateReqVO updateReqVO) {
        ErpAgriServiceOrderDO updateObj = new ErpAgriServiceOrderDO();
        org.springframework.beans.BeanUtils.copyProperties(updateReqVO, updateObj);
        agriServiceOrderMapper.updateById(updateObj);
    }

    @Override
    public void deleteServiceOrder(Long id) {
        agriServiceOrderMapper.deleteById(id);
    }
}
