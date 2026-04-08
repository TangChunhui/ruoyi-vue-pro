package cn.iocoder.yudao.module.erp.controller.app.agri;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldRecordDO;
import cn.iocoder.yudao.module.erp.service.agri.ErpAgriFieldService;
import cn.iocoder.yudao.module.erp.controller.app.agri.vo.AppAgriFieldRespVO;
import cn.iocoder.yudao.module.erp.controller.app.agri.vo.AppAgriFieldCreateReqVO;
import cn.iocoder.yudao.module.erp.controller.app.agri.vo.AppAgriFieldRecordRespVO;
import cn.iocoder.yudao.module.erp.controller.app.agri.vo.AppAgriFieldRecordCreateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 我的农田")
@RestController
@RequestMapping("/erp/agri/field")
@Validated
public class AppAgriFieldController {

    @Resource
    private ErpAgriFieldService agriFieldService;

    @GetMapping("/list")
    @Operation(summary = "获得地块列表")
    public CommonResult<List<AppAgriFieldRespVO>> getFieldList() {
        Long memberId = SecurityFrameworkUtils.getLoginUserId();
        List<ErpAgriFieldDO> list = agriFieldService.getFieldList(memberId);
        List<AppAgriFieldRespVO> res = list.stream().map(doObj -> {
            AppAgriFieldRespVO vo = new AppAgriFieldRespVO();
            BeanUtils.copyProperties(doObj, vo);
            return vo;
        }).collect(Collectors.toList());
        return success(res);
    }

    @PostMapping("/create")
    @Operation(summary = "创建地块")
    public CommonResult<Long> createField(@Valid @RequestBody AppAgriFieldCreateReqVO createReqVO) {
        Long memberId = SecurityFrameworkUtils.getLoginUserId();
        ErpAgriFieldDO createReqDO = new ErpAgriFieldDO();
        BeanUtils.copyProperties(createReqVO, createReqDO);
        createReqDO.setMemberId(memberId);
        return success(agriFieldService.createField(createReqDO));
    }

    @GetMapping("/record/list")
    @Operation(summary = "获得农事记录列表")
    public CommonResult<List<AppAgriFieldRecordRespVO>> getFieldRecordList(@RequestParam("fieldId") Long fieldId) {
        List<ErpAgriFieldRecordDO> list = agriFieldService.getFieldRecordList(fieldId);
        List<AppAgriFieldRecordRespVO> res = list.stream().map(doObj -> {
            AppAgriFieldRecordRespVO vo = new AppAgriFieldRecordRespVO();
            BeanUtils.copyProperties(doObj, vo);
            return vo;
        }).collect(Collectors.toList());
        return success(res);
    }

    @PostMapping("/record/create")
    @Operation(summary = "创建农事记录")
    public CommonResult<Long> createFieldRecord(@Valid @RequestBody AppAgriFieldRecordCreateReqVO createReqVO) {
        Long memberId = SecurityFrameworkUtils.getLoginUserId();
        ErpAgriFieldRecordDO createReqDO = new ErpAgriFieldRecordDO();
        BeanUtils.copyProperties(createReqVO, createReqDO);
        createReqDO.setMemberId(memberId);
        return success(agriFieldService.createFieldRecord(createReqDO));
    }
}
