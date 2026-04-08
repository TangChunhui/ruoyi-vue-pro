package cn.iocoder.yudao.module.erp.controller.app.agri;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.erp.dal.dataobject.agri.ErpAgriServiceOrderDO;
import cn.iocoder.yudao.module.erp.service.agri.ErpAgriServiceOrderService;
import cn.iocoder.yudao.module.erp.controller.app.agri.vo.AppAgriServiceOrderRespVO;
import cn.iocoder.yudao.module.erp.controller.app.agri.vo.AppAgriServiceOrderCreateReqVO;
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

@Tag(name = "用户 App - 农机服务")
@RestController
@RequestMapping("/erp/agri/service-order")
@Validated
public class AppAgriServiceOrderController {

    @Resource
    private ErpAgriServiceOrderService agriServiceOrderService;

    @GetMapping("/list")
    @Operation(summary = "获得服务订单列表")
    public CommonResult<List<AppAgriServiceOrderRespVO>> getServiceOrderList() {
        Long memberId = SecurityFrameworkUtils.getLoginUserId();
        List<ErpAgriServiceOrderDO> list = agriServiceOrderService.getServiceOrderList(memberId);
        List<AppAgriServiceOrderRespVO> res = list.stream().map(doObj -> {
            AppAgriServiceOrderRespVO vo = new AppAgriServiceOrderRespVO();
            BeanUtils.copyProperties(doObj, vo);
            return vo;
        }).collect(Collectors.toList());
        return success(res);
    }

    @PostMapping("/create")
    @Operation(summary = "创建服务订单")
    public CommonResult<Long> createServiceOrder(@Valid @RequestBody AppAgriServiceOrderCreateReqVO createReqVO) {
        Long memberId = SecurityFrameworkUtils.getLoginUserId();
        ErpAgriServiceOrderDO createReqDO = new ErpAgriServiceOrderDO();
        BeanUtils.copyProperties(createReqVO, createReqDO);
        createReqDO.setMemberId(memberId);
        return success(agriServiceOrderService.createServiceOrder(createReqDO));
    }
}
