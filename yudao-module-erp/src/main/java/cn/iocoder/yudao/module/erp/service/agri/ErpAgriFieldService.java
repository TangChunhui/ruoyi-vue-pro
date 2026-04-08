package cn.iocoder.yudao.module.erp.service.agri;

import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldRecordDO;

import java.util.List;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFieldPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFieldRecordPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFieldUpdateReqVO;

public interface ErpAgriFieldService {

    List<ErpAgriFieldDO> getFieldList(Long memberId);

    Long createField(ErpAgriFieldDO createReqDO);

    List<ErpAgriFieldRecordDO> getFieldRecordList(Long fieldId);

    Long createFieldRecord(ErpAgriFieldRecordDO createReqDO);

    // Admin APIs
    PageResult<ErpAgriFieldDO> getFieldPage(ErpAgriFieldPageReqVO pageReqVO);

    void updateField(ErpAgriFieldUpdateReqVO updateReqVO);

    void deleteField(Long id);

    PageResult<ErpAgriFieldRecordDO> getFieldRecordPage(ErpAgriFieldRecordPageReqVO pageReqVO);
}
