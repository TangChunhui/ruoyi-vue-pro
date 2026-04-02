import request from '@/config/axios'

// ERP 产品库存明细 VO
export interface StockRecordVO {
  id: number
  productId: number
  warehouseId: number
  batchNo: string
  productionDate: number
  expiryDate: number
  count: number
  totalCount: number
  bizType: number
  bizId: number
  bizItemId: number
  bizNo: string
}

// ERP 产品库存明细 API
export const StockRecordApi = {
  // 查询产品库存明细分页
  getStockRecordPage: async (params: any) => {
    return await request.get({ url: `/erp/stock-record/page`, params })
  },

  // 查询产品库存明细详情
  getStockRecord: async (id: number) => {
    return await request.get({ url: `/erp/stock-record/get?id=` + id })
  },

  // 导出产品库存明细 Excel
  exportStockRecord: async (params) => {
    return await request.download({ url: `/erp/stock-record/export-excel`, params })
  }
}
