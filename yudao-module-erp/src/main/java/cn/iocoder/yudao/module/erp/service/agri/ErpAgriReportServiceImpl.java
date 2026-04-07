package cn.iocoder.yudao.module.erp.service.agri;

import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriPurchaseLedgerRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriRestrictedSaleRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriWarningOverviewRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpSaleOrderDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpSaleOutDO;
import cn.iocoder.yudao.module.erp.dal.mysql.agri.ErpAgriReportMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOrderMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOutMapper;
import cn.iocoder.yudao.module.erp.framework.seetong.core.SeetongClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 农资报表 Service 实现类
 */
@Service
public class ErpAgriReportServiceImpl implements ErpAgriReportService {

    @Resource
    private ErpAgriReportMapper agriReportMapper;
    @Resource
    private ErpSaleOrderMapper saleOrderMapper;
    @Resource
    private ErpSaleOutMapper saleOutMapper;
    @Resource
    private SeetongClient seetongClient;
    @Resource
    private cn.iocoder.yudao.module.erp.framework.seetong.config.SeetongProperties seetongProperties;

    @Override
    public List<ErpAgriStockBalanceRespVO> getStockBalanceReport(ErpAgriStockBalanceReqVO reqVO) {
        return agriReportMapper.selectStockBalance(reqVO);
    }

    @Override
    public ErpAgriWarningOverviewRespVO getAgriWarningOverview() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime monthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        ErpAgriWarningOverviewRespVO vo = new ErpAgriWarningOverviewRespVO();
        // 核心监控指标聚合
        vo.setExpiringProductCount(agriReportMapper.selectExpiringProductCount(now, now.plusDays(30)));
        vo.setExpiredSupplierCount(agriReportMapper.selectExpiredSupplierCount(now));
        vo.setWarningCustomerCount(agriReportMapper.selectWarningCustomerCount());
        vo.setMonthlyRestrictedSaleCount(agriReportMapper.selectMonthlyRestrictedSaleCount(monthStart));

        // 商业指标注入
        vo.setTotalStockValue(agriReportMapper.selectTotalStockValue());
        vo.setTodaySalesAmount(agriReportMapper.selectSalesAmount(todayStart, now));
        vo.setMonthSalesAmount(agriReportMapper.selectSalesAmount(monthStart, now));

        return vo;
    }

    @Override
    public List<ErpAgriRestrictedSaleRespVO> getRestrictedSaleList(ErpAgriStockBalanceReqVO req) {
        return agriReportMapper.selectRestrictedSaleList(req);
    }

    @Override
    public List<ErpAgriRestrictedSaleRespVO> getSalesDetailList(ErpAgriStockBalanceReqVO req) {
        return agriReportMapper.selectSalesDetailList(req);
    }

    @Override
    public List<ErpAgriStockBalanceRespVO> getExpiringStockList(Integer days) {
        return agriReportMapper.selectExpiringStockList(days != null ? days : 180);
    }

    @Override
    public cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFinanceSummaryRespVO getAgriFinanceSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beginTime = now.toLocalDate().atStartOfDay();
        LocalDateTime endTime = now.toLocalDate().atTime(LocalTime.MAX);

        cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFinanceSummaryRespVO vo = new cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFinanceSummaryRespVO();
        
        // 1. 基础金额统计
        vo.setTotalReceivableAmount(agriReportMapper.selectTotalReceivableAmount());
        vo.setTodayReceiptAmount(agriReportMapper.selectReceiptAmount(beginTime, endTime));
        vo.setTodayPaymentAmount(agriReportMapper.selectPaymentAmount(beginTime, endTime));
        vo.setTodaySalesAmount(agriReportMapper.selectSalesAmount(beginTime, endTime));
        
        // 2. 净现金流计算
        java.math.BigDecimal receipt = vo.getTodayReceiptAmount() != null ? vo.getTodayReceiptAmount() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal payment = vo.getTodayPaymentAmount() != null ? vo.getTodayPaymentAmount() : java.math.BigDecimal.ZERO;
        vo.setTodayNetCashFlow(receipt.subtract(payment));

        // 3. 流水列表
        vo.setFlowList(agriReportMapper.selectTodayFinanceFlowList(beginTime, endTime));
        
        return vo;
    }

    @Override
    public List<ErpAgriRestrictedSaleRespVO> getRestrictedSaleLeaderboard() {
        return agriReportMapper.selectRestrictedSaleLeaderboard();
    }

    @Override
    public List<cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierRespVO> getSupplierLicenseCountdown() {
        return agriReportMapper.selectSupplierLicenseCountdown();
    }

    @Override
    public String getPlaybackUrl(Long bizId, String bizType, Integer preMinutes, Integer postMinutes) {
        String cameraId = null;
        LocalDateTime videoTime = null;

        if ("sale_order".equals(bizType)) {
            ErpSaleOrderDO order = saleOrderMapper.selectById(bizId);
            if (order != null) {
                cameraId = order.getCameraId();
                videoTime = order.getVideoTime() != null ? order.getVideoTime() : order.getOrderTime();
            }
        } else if ("sale_out".equals(bizType)) {
            ErpSaleOutDO outbound = saleOutMapper.selectById(bizId);
            if (outbound != null) {
                cameraId = outbound.getCameraId();
                videoTime = outbound.getVideoTime() != null ? outbound.getVideoTime() : outbound.getOutTime();
            }
        }

        if (cameraId == null || videoTime == null) {
            return null;
        }

        int pre  = preMinutes  != null ? preMinutes  : seetongProperties.getPreMinutes();
        int post = postMinutes != null ? postMinutes : seetongProperties.getPostMinutes();
        return seetongClient.getPlaybackUrl(cameraId, videoTime.minusMinutes(pre), videoTime.plusMinutes(post));
    }

    @Override
    public List<cn.iocoder.yudao.module.erp.framework.seetong.core.SeetongClient.DeviceVO> getSeetongDeviceList() {
        if (!seetongProperties.isEnabled()) {
            return new java.util.ArrayList<>();
        }
        return seetongClient.getDeviceList();
    }

    @Override
    public List<ErpAgriPurchaseLedgerRespVO> getPurchaseLedgerList(ErpAgriStockBalanceReqVO req) {
        return agriReportMapper.selectPurchaseLedgerList(req);
    }
}
