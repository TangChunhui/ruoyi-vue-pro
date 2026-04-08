package cn.iocoder.yudao.module.erp.controller.admin.agri;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriServiceOrderDO;
import cn.iocoder.yudao.module.erp.service.agri.ErpAgriServiceOrderService;
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

@Tag(name = "管理后台 - 农业服务订单")
@RestController
@RequestMapping("/erp/agri/service-order")
@Validated
public class AdminAgriServiceOrderController {

    @Resource
    private ErpAgriServiceOrderService agriServiceOrderService;

    @GetMapping("/page")
    @Operation(summary = "获得服务订单分页")
    @PreAuthorize("@ss.hasPermission('erp:agri-service-order:query')")
    public CommonResult<PageResult<ErpAgriServiceOrderRespVO>> getServiceOrderPage(@Valid ErpAgriServiceOrderPageReqVO pageReqVO) {
        PageResult<ErpAgriServiceOrderDO> pageResult = agriServiceOrderService.getServiceOrderPage(pageReqVO);
        List<ErpAgriServiceOrderRespVO> list = pageResult.getList().stream().map(doObj -> {
            ErpAgriServiceOrderRespVO vo = new ErpAgriServiceOrderRespVO();
            BeanUtils.copyProperties(doObj, vo);
            return vo;
        }).collect(Collectors.toList());
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新/派发服务订单")
    @PreAuthorize("@ss.hasPermission('erp:agri-service-order:update')")
    public CommonResult<Boolean> updateServiceOrder(@Valid @RequestBody ErpAgriServiceOrderUpdateReqVO updateReqVO) {
        agriServiceOrderService.updateServiceOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除服务订单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:agri-service-order:delete')")
    public CommonResult<Boolean> deleteServiceOrder(@RequestParam("id") Long id) {
        agriServiceOrderService.deleteServiceOrder(id);
        return success(true);
    }
}
