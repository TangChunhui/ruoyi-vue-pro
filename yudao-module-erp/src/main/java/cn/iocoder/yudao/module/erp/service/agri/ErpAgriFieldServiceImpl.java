package cn.iocoder.yudao.module.erp.service.agri;

import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldRecordDO;
import cn.iocoder.yudao.module.erp.dal.mysql.agri.ErpAgriFieldMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.agri.ErpAgriFieldRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

@Service
@Validated
public class ErpAgriFieldServiceImpl implements ErpAgriFieldService {

    @Resource
    private ErpAgriFieldMapper agriFieldMapper;

    @Resource
    private ErpAgriFieldRecordMapper agriFieldRecordMapper;

    @Override
    public List<ErpAgriFieldDO> getFieldList(Long memberId) {
        return agriFieldMapper.selectListByMemberId(memberId);
    }

    @Override
    public Long createField(ErpAgriFieldDO createReqDO) {
        agriFieldMapper.insert(createReqDO);
        return createReqDO.getId();
    }

    @Override
    public List<ErpAgriFieldRecordDO> getFieldRecordList(Long fieldId) {
        return agriFieldRecordMapper.selectListByFieldId(fieldId);
    }

    @Override
    public Long createFieldRecord(ErpAgriFieldRecordDO createReqDO) {
        agriFieldRecordMapper.insert(createReqDO);
        return createReqDO.getId();
    }

    @Override
    public cn.iocoder.yudao.framework.common.pojo.PageResult<ErpAgriFieldDO> getFieldPage(cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFieldPageReqVO pageReqVO) {
        return agriFieldMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateField(cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFieldUpdateReqVO updateReqVO) {
        ErpAgriFieldDO updateObj = new ErpAgriFieldDO();
        org.springframework.beans.BeanUtils.copyProperties(updateReqVO, updateObj);
        agriFieldMapper.updateById(updateObj);
    }

    @Override
    public void deleteField(Long id) {
        agriFieldMapper.deleteById(id);
    }

    @Override
    public cn.iocoder.yudao.framework.common.pojo.PageResult<ErpAgriFieldRecordDO> getFieldRecordPage(cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFieldRecordPageReqVO pageReqVO) {
        return agriFieldRecordMapper.selectPage(pageReqVO);
    }
}
