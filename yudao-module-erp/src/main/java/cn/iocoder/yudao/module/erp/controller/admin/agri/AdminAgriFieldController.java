package cn.iocoder.yudao.module.erp.controller.admin.agri;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriFieldRecordDO;
import cn.iocoder.yudao.module.erp.service.agri.ErpAgriFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 农田管理")
@RestController
@RequestMapping("/erp/agri/field")
@Validated
public class AdminAgriFieldController {

    @Resource
    private ErpAgriFieldService agriFieldService;

    @GetMapping("/page")
    @Operation(summary = "获得地块分页")
    @PreAuthorize("@ss.hasPermission('erp:agri-field:query')")
    public CommonResult<PageResult<ErpAgriFieldRespVO>> getFieldPage(@Valid ErpAgriFieldPageReqVO pageReqVO) {
        PageResult<ErpAgriFieldDO> pageResult = agriFieldService.getFieldPage(pageReqVO);
        List<ErpAgriFieldRespVO> list = pageResult.getList().stream().map(doObj -> {
            ErpAgriFieldRespVO vo = new ErpAgriFieldRespVO();
            BeanUtils.copyProperties(doObj, vo);
            return vo;
        }).collect(Collectors.toList());
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新地块")
    @PreAuthorize("@ss.hasPermission('erp:agri-field:update')")
    public CommonResult<Boolean> updateField(@Valid @RequestBody ErpAgriFieldUpdateReqVO updateReqVO) {
        agriFieldService.updateField(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除地块")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:agri-field:delete')")
    public CommonResult<Boolean> deleteField(@RequestParam("id") Long id) {
        agriFieldService.deleteField(id);
        return success(true);
    }

    @GetMapping("/record/page")
    @Operation(summary = "获得农田流水记录分页")
    @PreAuthorize("@ss.hasPermission('erp:agri-field-record:query')")
    public CommonResult<PageResult<ErpAgriFieldRecordRespVO>> getFieldRecordPage(@Valid ErpAgriFieldRecordPageReqVO pageReqVO) {
        PageResult<ErpAgriFieldRecordDO> pageResult = agriFieldService.getFieldRecordPage(pageReqVO);
        List<ErpAgriFieldRecordRespVO> list = pageResult.getList().stream().map(doObj -> {
            ErpAgriFieldRecordRespVO vo = new ErpAgriFieldRecordRespVO();
            BeanUtils.copyProperties(doObj, vo);
            return vo;
        }).collect(Collectors.toList());
        return success(new PageResult<>(list, pageResult.getTotal()));
    }
}
