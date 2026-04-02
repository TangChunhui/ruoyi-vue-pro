import request from '@/config/axios'

// 农业报表台账 API
export const AgriReportApi = {
    // 获取农资库存及收发存报表
    getStockBalanceReport: async (params: any) => {
        return await request.get({ url: `/erp/agri-report/stock-balance`, params })
    },
    // 获取农资运营合规预警概览
    getAgriWarningOverview: async () => {
        return await request.get({ url: `/erp/agri-report/get-warning-overview` })
    },
    // 获取高毒限用农药销售电子台账
    getRestrictedSaleList: async (params: any) => {
        return await request.get({ url: `/erp/agri-report/restricted-sale-list`, params })
    },
    getSalesDetailList: async (params: any) => {
        return await request.get({ url: `/erp/agri-report/sales-detail-list`, params })
    },
    getExpiringStockList: async (days: number) => {
        return await request.get({ url: `/erp/agri-report/expiring-stock-list`, params: { days } })
    },
    // 获取农资财务概览（应收、今日收支流水）
    getAgriFinanceSummary: async () => {
        return await request.get({ url: `/erp/agri-report/finance-summary` })
    }
}
