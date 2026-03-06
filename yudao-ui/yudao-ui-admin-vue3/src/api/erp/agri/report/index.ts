import request from '@/config/axios'

// 农业报表台账 API
export const AgriReportApi = {
    // 获取农资库存及收发存报表
    getStockBalanceReport: async (params: any) => {
        return await request.get({ url: `/erp/agri-report/stock-balance`, params })
    }
}
