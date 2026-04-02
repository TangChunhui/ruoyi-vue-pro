<template>
  <doc-alert title="【销售】销售订单、出库、退货" url="https://doc.iocoder.cn/erp/sale/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="订单单号" prop="no">
        <el-input
          v-model="queryParams.no"
          placeholder="请输入订单单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="产品" prop="productId">
        <el-select
          v-model="queryParams.productId"
          clearable
          filterable
          placeholder="请选择产品"
          class="!w-240px"
        >
          <el-option
            v-for="item in productList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单时间" prop="orderTime">
        <el-date-picker
          v-model="queryParams.orderTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
          :shortcuts="[
            { text: '今日', value: [new Date().setHours(0, 0, 0, 0), new Date().setHours(23, 59, 59, 999)] },
            { text: '昨日', value: [new Date(Date.now() - 86400000).setHours(0, 0, 0, 0), new Date(Date.now() - 86400000).setHours(23, 59, 59, 999)] },
            { text: '最近7日', value: [new Date(Date.now() - 604800000).setHours(0, 0, 0, 0), new Date(Date.now() - 604800000).setHours(23, 59, 59, 999)] }
          ]"
        />
      </el-form-item>
      <el-form-item label="客户" prop="customerId">
        <el-select
          v-model="queryParams.customerId"
          clearable
          filterable
          placeholder="请选择供客户"
          class="!w-240px"
        >
          <el-option
            v-for="item in customerList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建人" prop="creator">
        <el-select
          v-model="queryParams.creator"
          clearable
          filterable
          placeholder="请选择创建人"
          class="!w-240px"
        >
          <el-option
            v-for="item in userList"
            :key="item.id"
            :label="item.nickname"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.ERP_AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="queryParams.remark"
          placeholder="请输入备注"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="出库数量" prop="outStatus">
        <el-select
          v-model="queryParams.outStatus"
          placeholder="请选择出库数量"
          clearable
          class="!w-240px"
        >
          <el-option label="未出库" value="0" />
          <el-option label="部分出库" value="1" />
          <el-option label="全部出库" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="退货数量" prop="returnStatus">
        <el-select
          v-model="queryParams.returnStatus"
          placeholder="请选择退货数量"
          clearable
          class="!w-240px"
        >
          <el-option label="未退货" value="0" />
          <el-option label="部分退货" value="1" />
          <el-option label="全部退货" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['erp:sale-order:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['erp:sale-order:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
          type="danger"
          plain
          @click="handleDelete(selectionList.map((item) => item.id))"
          v-hasPermi="['erp:sale-order:delete']"
          :disabled="selectionList.length === 0"
        >
          <Icon icon="ep:delete" class="mr-5px" /> 删除
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
      @selection-change="handleSelectionChange"
    >
      <el-table-column width="30" label="选择" type="selection" />
      <el-table-column min-width="180" label="订单单号" align="center" prop="no" />
      <el-table-column label="合规" align="center" width="80">
        <template #default="scope">
          <el-tooltip v-if="scope.row.buyerIdCard" :content="'购药人: ' + scope.row.buyerIdCard">
            <el-tag type="success" size="small"><Icon icon="ep:reading" /> 已登记</el-tag>
          </el-tooltip>
          <el-tag v-else type="info" size="small">免登记</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="产品信息" align="left" prop="productNames" min-width="250">
        <template #default="scope">
          <div v-for="item in scope.row.items" :key="item.id" class="text-12px mb-2px">
            <el-tag size="small" type="info" class="mr-4px">{{ item.productName }}</el-tag>
            <span v-if="item.batchNo" class="text-gray-400">批次: {{ item.batchNo }}</span>
            <span class="ml-4px">x{{ erpCountTableColumnFormatter(null, null, item.count) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="客户" align="center" prop="customerName" />
      <el-table-column
        label="订单时间"
        align="center"
        prop="orderTime"
        :formatter="dateFormatter2"
        width="120px"
      />
      <el-table-column label="创建人" align="center" prop="creatorName" />
      <el-table-column
        label="总数量"
        align="center"
        prop="totalCount"
        :formatter="erpCountTableColumnFormatter"
      />
      <el-table-column
        label="出库数量"
        align="center"
        prop="outCount"
        :formatter="erpCountTableColumnFormatter"
      />
      <el-table-column
        label="退货数量"
        align="center"
        prop="returnCount"
        :formatter="erpCountTableColumnFormatter"
      />
      <el-table-column
        label="金额合计"
        align="center"
        prop="totalProductPrice"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        label="含税金额"
        align="center"
        prop="totalPrice"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        label="收取订金"
        align="center"
        prop="depositPrice"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column label="状态" align="center" fixed="right" width="90" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.ERP_AUDIT_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" width="220">
        <template #default="scope">
          <el-button
            link
            @click="openForm('detail', scope.row.id)"
            v-hasPermi="['erp:sale-order:query']"
          >
            详情
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['erp:sale-order:update']"
            :disabled="scope.row.status === 20"
          >
            编辑
          </el-button>
          <el-button
            link
            type="primary"
            @click="handleUpdateStatus(scope.row.id, 20)"
            v-hasPermi="['erp:sale-order:update-status']"
            v-if="scope.row.status === 10"
          >
            审批
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleUpdateStatus(scope.row.id, 10)"
            v-hasPermi="['erp:sale-order:update-status']"
            v-else
          >
            反审批
          </el-button>
          <el-button
            link
            type="primary"
            @click="handlePrint(scope.row.id)"
            v-hasPermi="['erp:sale-order:query']"
          >
            打印
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete([scope.row.id])"
            v-hasPermi="['erp:sale-order:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <SaleOrderForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter2 } from '@/utils/formatTime'
import download from '@/utils/download'
import { SaleOrderApi, SaleOrderVO } from '@/api/erp/sale/order'
import SaleOrderForm from './SaleOrderForm.vue'
import { ProductApi, ProductVO } from '@/api/erp/product/product'
import { UserVO } from '@/api/system/user'
import * as UserApi from '@/api/system/user'
import { erpCountTableColumnFormatter, erpPriceTableColumnFormatter } from '@/utils'
import { CustomerApi, CustomerVO } from '@/api/erp/sale/customer'

/** ERP 销售订单列表 */
defineOptions({ name: 'ErpSaleOrder' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<SaleOrderVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: undefined,
  customerId: undefined,
  productId: undefined,
  orderTime: [],
  status: undefined,
  remark: undefined,
  creator: undefined,
  outStatus: undefined,
  returnStatus: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const productList = ref<ProductVO[]>([]) // 产品列表
const customerList = ref<CustomerVO[]>([]) // 客户列表
const userList = ref<UserVO[]>([]) // 用户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await SaleOrderApi.getSaleOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (ids: number[]) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await SaleOrderApi.deleteSaleOrder(ids)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
    selectionList.value = selectionList.value.filter((item) => !ids.includes(item.id))
  } catch {}
}

/** 审批/反审批操作 */
const handleUpdateStatus = async (id: number, status: number) => {
  try {
    // 审批的二次确认
    await message.confirm(`确定${status === 20 ? '审批' : '反审批'}该订单吗？`)
    // 发起审批
    await SaleOrderApi.updateSaleOrderStatus(id, status)
    message.success(`${status === 20 ? '审批' : '反审批'}成功`)
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await SaleOrderApi.exportSaleOrder(queryParams)
    download.excel(data, '销售订单.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 选中操作 */
const selectionList = ref<SaleOrderVO[]>([])
const handleSelectionChange = (rows: SaleOrderVO[]) => {
  selectionList.value = rows
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 加载产品、仓库列表、客户
  productList.value = await ProductApi.getProductSimpleList()
  customerList.value = await CustomerApi.getCustomerSimpleList()
  userList.value = await UserApi.getSimpleUserList()
})

/** 打印销售单 (农资合规版) */
const handlePrint = async (id: number) => {
  const data = await SaleOrderApi.getSaleOrder(id)
  const printWindow = window.open('', '_blank')
  if (!printWindow) return

  // 1. 构建打印内容
  let itemsHtml = ''
  data.items.forEach((item, index) => {
    itemsHtml += `
      <tr>
        <td>${index + 1}</td>
        <td>${item.productName}</td>
        <td>${item.productUnitName || ''}</td>
        <td>${item.count}</td>
        <td>¥${item.productPrice.toFixed(2)}</td>
        <td>¥${item.totalPrice.toFixed(2)}</td>
      </tr>
    `
  })

  const htmlContent = `
    <html>
      <head>
        <title>农资产品销售送货单 - ${data.no}</title>
        <style>
          body { font-family: sans-serif; padding: 20px; }
          .header { text-align: center; border-bottom: 2px solid #333; margin-bottom: 20px; padding-bottom: 10px; }
          .info-grid { display: grid; grid-template-columns: 1fr 1fr; margin-bottom: 20px; font-size: 14px; }
          table { width: 100%; border-collapse: collapse; margin-top: 10px; }
          th, td { border: 1px solid #333; padding: 8px; text-align: left; }
          th { background: #f5f5f5; }
          .footer { margin-top: 30px; border-top: 1px dashed #333; padding-top: 15px; font-size: 12px; }
          .highlight { font-weight: bold; }
        </style>
      </head>
      <body>
        <div class="header">
          <h2>农业生产资料销售送货单 (合规凭证)</h2>
          <div>单号: ${data.no} | 日期: ${dateFormatter2(null, null, data.orderTime)}</div>
        </div>
        <div class="info-grid">
          <div>客户名称: <span class="highlight">${data.customerName || '散客'}</span></div>
          <div>购药人证件: <span class="highlight">${data.buyerIdCard || '非受控药/未登记'}</span></div>
          <div>用途建议: ${data.usageIntent || '--'}</div>
          <div>用法建议: ${data.usageMethod || '--'}</div>
        </div>
        <table>
          <thead>
            <tr>
              <th>序号</th><th>产品名称</th><th>单位</th><th>数量</th><th>单价</th><th>金额</th>
            </tr>
          </thead>
          <tbody>${itemsHtml}</tbody>
          <tfoot>
            <tr>
              <td colspan="4" style="text-align:right">合计:</td>
              <td colspan="2" class="highlight">¥${data.totalPrice.toFixed(2)}</td>
            </tr>
          </tfoot>
        </table>
        <div class="footer">
          注：请严格按照使用说明书或农技人员指导使用。农药登记证有效期内销售。
          <br/>
          打印来源: 农资ERP管理系统 | 打印时间: ${new Date().toLocaleString()}
        </div>
      </body>
    </html>
  `
  // 2. 写入并打印
  printWindow.document.write(htmlContent)
  printWindow.document.close()
  setTimeout(() => {
    printWindow.print()
    printWindow.close()
  }, 500)
}
</script>
