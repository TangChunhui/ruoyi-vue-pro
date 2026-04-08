package cn.iocoder.yudao.module.erp.dal.mysql.agri;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFieldPageReqVO;

@Mapper
public interface ErpAgriFieldMapper extends BaseMapperX<ErpAgriFieldDO> {
    default List<ErpAgriFieldDO> selectListByMemberId(Long memberId) {
        return selectList(ErpAgriFieldDO::getMemberId, memberId);
    }
    
    default PageResult<ErpAgriFieldDO> selectPage(ErpAgriFieldPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpAgriFieldDO>()
                .likeIfPresent(ErpAgriFieldDO::getName, reqVO.getName())
                .eqIfPresent(ErpAgriFieldDO::getCrop, reqVO.getCrop())
                .orderByDesc(ErpAgriFieldDO::getId));
    }
}
