package cn.iocoder.yudao.module.erp.service.agri;

import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.agri.vo.ErpAgriStockBalanceRespVO;
import cn.iocoder.yudao.module.erp.dal.mysql.agri.ErpAgriReportMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 农资报表 Service 实现类
 */
@Service
public class ErpAgriReportServiceImpl implements ErpAgriReportService {

    @Resource
    private ErpAgriReportMapper agriReportMapper;

    @Override
    public List<ErpAgriStockBalanceRespVO> getStockBalanceReport(ErpAgriStockBalanceReqVO reqVO) {
        return agriReportMapper.selectStockBalance(reqVO);
    }

}
