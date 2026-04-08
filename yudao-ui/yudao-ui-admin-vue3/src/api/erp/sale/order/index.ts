import request from '@/config/axios'

// ERP 销售订单项 VO
export interface SaleOrderItemVO {
  id: number // 订单项编号
  productId: number // 产品编号
  productUnitId: number // 产品单位编号
  productPrice: number // 产品单价
  count: number // 产品数量
  totalPrice: number // 总价
  taxPercent: number // 税率
  taxPrice: number // 税额
  batchNo: string // 生产批次号
  productionDate: number // 生产日期
  expiryDate: number // 有效期至
  remark: string // 备注
  outCount: number // 出库数量
  returnCount: number // 退货数量
}

// ERP 销售订单 VO
export interface SaleOrderVO {
  id: number // 订单号编号
  no: string // 销售订单号
  customerId: number // 客户编号
  orderTime: number // 订单时间
  totalCount: number // 合计数量
  totalPrice: number // 合计金额，单位：元
  status: number // 状态
  remark: string // 备注
  outCount: number // 销售出库数量
  returnCount: number // 销售退货数量
  usageIntent: string // 用途/防治对象
  usageMethod: string // 施用方法
  dosageAdvice: string // 建议用量
  buyerIdCard: string // 购买人身份证（高毒限用必填）
  cameraId: string // 监控设备 ID
  videoTime: number // 视频关联时间
  videoUrl: string // 监控回放视频地址
  items: SaleOrderItemVO[] // 订单清单列表
}

// ERP 销售订单 API
export const SaleOrderApi = {
  // 查询销售订单分页
  getSaleOrderPage: async (params: any) => {
    return await request.get({ url: `/erp/sale-order/page`, params })
  },

  // 查询销售订单详情
  getSaleOrder: async (id: number) => {
    return await request.get({ url: `/erp/sale-order/get?id=` + id })
  },

  // 新增销售订单
  createSaleOrder: async (data: SaleOrderVO) => {
    return await request.post({ url: `/erp/sale-order/create`, data })
  },

  // 修改销售订单
  updateSaleOrder: async (data: SaleOrderVO) => {
    return await request.put({ url: `/erp/sale-order/update`, data })
  },

  // 更新销售订单的状态
  updateSaleOrderStatus: async (id: number, status: number) => {
    return await request.put({
      url: `/erp/sale-order/update-status`,
      params: {
        id,
        status
      }
    })
  },

  // 删除销售订单
  deleteSaleOrder: async (ids: number[]) => {
    return await request.delete({
      url: `/erp/sale-order/delete`,
      params: {
        ids: ids.join(',')
      }
    })
  },

  // 导出销售订单 Excel
  exportSaleOrder: async (params: any) => {
    return await request.download({ url: `/erp/sale-order/export-excel`, params })
  },

  // 导出农业部监管合规台账 (限购农药等) Excel (R35)
  exportAgriLedgerExcel: async (params: any) => {
    return await request.download({ url: `/erp/sale-order/export-agri-ledger-excel`, params })
  }
}
