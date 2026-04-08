package cn.iocoder.yudao.module.erp.dal.mysql.agri;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldRecordDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFieldRecordPageReqVO;

@Mapper
public interface ErpAgriFieldRecordMapper extends BaseMapperX<ErpAgriFieldRecordDO> {
    default List<ErpAgriFieldRecordDO> selectListByFieldId(Long fieldId) {
        return selectList(ErpAgriFieldRecordDO::getFieldId, fieldId);
    }

    default PageResult<ErpAgriFieldRecordDO> selectPage(ErpAgriFieldRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpAgriFieldRecordDO>()
                .eqIfPresent(ErpAgriFieldRecordDO::getFieldId, reqVO.getFieldId())
                .eqIfPresent(ErpAgriFieldRecordDO::getType, reqVO.getType())
                .orderByDesc(ErpAgriFieldRecordDO::getId));
    }
}
