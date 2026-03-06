package cn.iocoder.yudao.module.erp.dal.mysql.agri;

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

}
