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
    },
    // 获取高毒限用农资采购排行
    getRestrictedSaleLeaderboard: async () => {
        return await request.get({ url: `/erp/agri-report/restricted-sale-leaderboard` })
    },
    // 获得供货商资质到期预警列表
    getSupplierLicenseCountdown: async () => {
        return await request.get({ url: `/erp/agri-report/supplier-license-countdown` })
    },
    // 获得监控回放地址
    getPlaybackUrl: async (bizId: number, bizType: string, preMinutes?: number, postMinutes?: number) => {
        return await request.get({
            url: `/erp/agri-report/get-playback-url`,
            params: { bizId, bizType, preMinutes, postMinutes }
        })
    },
    // 获得 Seetong 摄像头设备列表
    getSeetongDevices: async () => {
        return await request.get({ url: `/erp/agri-report/seetong-devices` })
    },
    // 获得农资购进台账列表
    getPurchaseLedgerList: async (params: any) => {
        return await request.get({ url: `/erp/agri-report/purchase-ledger-list`, params })
    }
}
