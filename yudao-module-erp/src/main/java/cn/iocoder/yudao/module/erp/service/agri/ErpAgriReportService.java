package cn.iocoder.yudao.module.erp.service.agri;

import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriPurchaseLedgerRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriRestrictedSaleRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriWarningOverviewRespVO;

import cn.iocoder.yudao.module.erp.framework.seetong.core.SeetongClient;
import java.util.List;

/**
 * 农资报表 Service 接口
 */
public interface ErpAgriReportService {

    /**
     * 获取农资收发存台账报表
     */
    List<ErpAgriStockBalanceRespVO> getStockBalanceReport(ErpAgriStockBalanceReqVO reqVO);

    /**
     * 获取农资运营合规预警概览
     */
    /**
     * 获取农资运营合规预警概览
     */
    ErpAgriWarningOverviewRespVO getAgriWarningOverview();

    /**
     * 获取高毒限用农药销售电子台账
     */
    List<ErpAgriRestrictedSaleRespVO> getRestrictedSaleList(ErpAgriStockBalanceReqVO reqVO);

    /**
     * 获得全量销售明细列表（当日对账）
     */
    List<ErpAgriRestrictedSaleRespVO> getSalesDetailList(ErpAgriStockBalanceReqVO req);

    /**
     * 获得临期库存清单（基于批次效期）
     */
    List<ErpAgriStockBalanceRespVO> getExpiringStockList(Integer days);

    /**
     * 获取农资财务概览（应收、今日收支流水）
     */
    cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFinanceSummaryRespVO getAgriFinanceSummary();

    /**
     * 获取高毒限用农资采购排行（合规排查）
     */
    List<ErpAgriRestrictedSaleRespVO> getRestrictedSaleLeaderboard();

    /**
     * 获得供货商资质到期预警列表
     */
    List<cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierRespVO> getSupplierLicenseCountdown();

    /**
     * 获得监控回放地址
     */
    String getPlaybackUrl(Long bizId, String bizType, Integer preMinutes, Integer postMinutes);

    /**
     * 获得 Seetong 摄像头设备列表
     */
    List<SeetongClient.DeviceVO> getSeetongDeviceList();

    /**
     * 获得农资购进台账列表
     */
    List<ErpAgriPurchaseLedgerRespVO> getPurchaseLedgerList(ErpAgriStockBalanceReqVO req);

}
