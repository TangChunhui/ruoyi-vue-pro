package cn.iocoder.yudao.module.erp.dal.mysql.agri;

import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriRestrictedSaleRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceRespVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 农资报表 Mapper
 */
@Mapper
public interface ErpAgriReportMapper {

    /**
     * 查询收发存台账（按产品聚合）
     * 逻辑：
     *   期初库存 = 统计开始时间之前的累计库存量（取最近一条 totalCount）
     *   本期入库 = 统计区间内 count > 0 的汇总
     *   本期出库 = 统计区间内 count < 0 的绝对值汇总
     *   期末库存 = 统计结束时间前的最新 totalCount
     */
    List<ErpAgriStockBalanceRespVO> selectStockBalance(@Param("req") ErpAgriStockBalanceReqVO req);

    /**
     * 查询 30 天内登记证过期的产品数量
     */
    Long selectExpiringProductCount(@Param("now") java.time.LocalDateTime now, @Param("future") java.time.LocalDateTime future);

    /**
     * 查询已过期的供应商数量
     */
    Long selectExpiredSupplierCount(@Param("now") java.time.LocalDateTime now);

    /**
     * 查询欠款预警客户数量 (超过 80% 授信)
     */
    Long selectWarningCustomerCount();

    /**
     * 查询本月高毒限用农药销售笔数
     */
    Long selectMonthlyRestrictedSaleCount(@Param("startOfMonth") java.time.LocalDateTime startOfMonth);

    /**
     * 分页查询高毒限用农资销售详细单据（电子台账）
     */
    List<ErpAgriRestrictedSaleRespVO> selectRestrictedSaleList(@Param("req") ErpAgriStockBalanceReqVO req);

    /**
     * 分页查询所有农资销售明细单据（当日对账/全量追溯）
     */
    List<ErpAgriRestrictedSaleRespVO> selectSalesDetailList(@Param("req") ErpAgriStockBalanceReqVO req);

    /**
     * 查询临期库存清单（基于批次效期）
     */
    List<ErpAgriStockBalanceRespVO> selectExpiringStockList(@Param("days") Integer days);

    /**
     * 合计当前库存总市值 (资产估值)
     */
    java.math.BigDecimal selectTotalStockValue();

    /**
     * 查询指定日期区间的销售总额 (业绩透视)
     */
    java.math.BigDecimal selectSalesAmount(@Param("beginTime") java.time.LocalDateTime beginTime,
                                         @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 今日收入总额（收款单已审）
     */
    java.math.BigDecimal selectReceiptAmount(@Param("beginTime") java.time.LocalDateTime beginTime,
                                           @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 今日支出总额（付款单已审）
     */
    java.math.BigDecimal selectPaymentAmount(@Param("beginTime") java.time.LocalDateTime beginTime,
                                           @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 总应收账款（当前所有客户欠款合计）
     */
    java.math.BigDecimal selectTotalReceivableAmount();

    /**
     * 今日流水记录
     */
    List<cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriFinanceSummaryRespVO.FlowItem> selectTodayFinanceFlowList(
            @Param("beginTime") java.time.LocalDateTime beginTime,
            @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 获取高毒限用农资采购排行（合规排查）
     */
    List<ErpAgriRestrictedSaleRespVO> selectRestrictedSaleLeaderboard();

    /**
     * 获得供货商资质到期预警列表
     */
    List<cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierRespVO> selectSupplierLicenseCountdown();

}
