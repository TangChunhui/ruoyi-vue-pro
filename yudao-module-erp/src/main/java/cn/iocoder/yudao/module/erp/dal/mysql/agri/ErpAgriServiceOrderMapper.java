package cn.iocoder.yudao.module.erp.dal.mysql.agri;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriServiceOrderDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriServiceOrderPageReqVO;

@Mapper
public interface ErpAgriServiceOrderMapper extends BaseMapperX<ErpAgriServiceOrderDO> {
    default List<ErpAgriServiceOrderDO> selectListByMemberId(Long memberId) {
        return selectList(ErpAgriServiceOrderDO::getMemberId, memberId);
    }

    default PageResult<ErpAgriServiceOrderDO> selectPage(ErpAgriServiceOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpAgriServiceOrderDO>()
                .eqIfPresent(ErpAgriServiceOrderDO::getServiceType, reqVO.getServiceType())
                .eqIfPresent(ErpAgriServiceOrderDO::getStatus, reqVO.getStatus())
                .likeIfPresent(ErpAgriServiceOrderDO::getContactName, reqVO.getContactName())
                .likeIfPresent(ErpAgriServiceOrderDO::getContactMobile, reqVO.getContactMobile())
                .orderByDesc(ErpAgriServiceOrderDO::getId));
    }
}
