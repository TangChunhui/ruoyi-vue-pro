package cn.iocoder.yudao.module.erp.service.agri;

import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceRespVO;

import java.util.List;

/**
 * 农资报表 Service 接口
 */
public interface ErpAgriReportService {

    /**
     * 获取农资收发存台账报表
     *
     * @param reqVO 查询条件（时间范围、仓库、产品）
     * @return 收发存台账列表
     */
    List<ErpAgriStockBalanceRespVO> getStockBalanceReport(ErpAgriStockBalanceReqVO reqVO);

}
