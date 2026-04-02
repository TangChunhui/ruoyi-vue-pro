import request from '@/config/axios'

// ERP 采购订单项 VO
export interface PurchaseOrderItemVO {
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
  inCount: number // 入库数量
  returnCount: number // 退货数量
}

// ERP 采购订单 VO
export interface PurchaseOrderVO {
  id: number // 订单号编号
  no: string // 采购订单号
  supplierId: number // 供应商编号
  orderTime: number // 订单时间
  totalCount: number // 合计数量
  totalPrice: number // 合计金额，单位：元
  status: number // 状态
  remark: string // 备注
  items: PurchaseOrderItemVO[] // 订单清单列表
}

// ERP 采购订单 API
export const PurchaseOrderApi = {
  // 查询采购订单分页
  getPurchaseOrderPage: async (params: any) => {
    return await request.get({ url: `/erp/purchase-order/page`, params })
  },

  // 查询采购订单详情
  getPurchaseOrder: async (id: number) => {
    return await request.get({ url: `/erp/purchase-order/get?id=` + id })
  },

  // 新增采购订单
  createPurchaseOrder: async (data: PurchaseOrderVO) => {
    return await request.post({ url: `/erp/purchase-order/create`, data })
  },

  // 修改采购订单
  updatePurchaseOrder: async (data: PurchaseOrderVO) => {
    return await request.put({ url: `/erp/purchase-order/update`, data })
  },

  // 更新采购订单的状态
  updatePurchaseOrderStatus: async (id: number, status: number) => {
    return await request.put({
      url: `/erp/purchase-order/update-status`,
      params: {
        id,
        status
      }
    })
  },

  // 删除采购订单
  deletePurchaseOrder: async (ids: number[]) => {
    return await request.delete({
      url: `/erp/purchase-order/delete`,
      params: {
        ids: ids.join(',')
      }
    })
  },

  // 导出采购订单 Excel
  exportPurchaseOrder: async (params: any) => {
    return await request.download({ url: `/erp/purchase-order/export-excel`, params })
  }
}
