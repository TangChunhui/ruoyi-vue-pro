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
          <Icon icon="ep:download" class="mr-5px" /> 导出全部
        </el-button>
        <el-button
          type="warning"
          plain
          @click="handleAgriExport"
          :loading="exportLoading"
          v-hasPermi="['erp:sale-order:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出【农委监管台账】
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
            <span class="ml-4px">x{{ erpCountTableColumnFormatter(null, null, item.count, 0) }}</span>
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
            @click="handlePrint(scope.row)"
            v-hasPermi="['erp:sale-order:query']"
          >
            打印
          </el-button>
          <el-button
            link
            type="success"
            @click="handlePlayVideo(scope.row)"
            v-if="scope.row.cameraId"
            v-hasPermi="['erp:agri-report:query']"
          >
            视频
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

  <!-- 打印弹窗 (农资版) -->
  <SaleCompliancePrint ref="printRef" />

  <!-- 视频回放弹窗 -->
  <Dialog v-model="videoVisible" title="监控视频回放 (Seetong)" width="800px">
    <div class="flex justify-center flex-col items-center">
      <video
        v-if="videoUrl"
        ref="videoPlayer"
        :src="videoUrl"
        controls
        autoplay
        class="w-full max-h-450px bg-black"
      >
        您的浏览器不支持视频播放。
      </video>
      <div v-else class="h-300px flex items-center justify-center text-gray-400">
        <Icon icon="ep:video-play" :size="40" class="mb-10px" />
        <span>正在获取视频流...</span>
      </div>
      <div class="mt-15px text-14px text-gray-500">
        设备 ID: {{ currentCameraId }} | 业务时间: {{ currentOrderTime }}
      </div>
    </div>
  </Dialog>
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
import SaleCompliancePrint from './components/SaleCompliancePrint.vue'
import { AgriReportApi } from '@/api/erp/agri/report'
import { dateFormatter } from '@/utils/formatTime'

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

/** 导出全部订单按钮操作 */
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

/** 导出农资电子台账 (R35) */
const handleAgriExport = async () => {
  try {
    await message.exportConfirm('确认导出《农业农村部农药等限用物资流向台账》？')
    exportLoading.value = true
    const data = await SaleOrderApi.exportAgriLedgerExcel(queryParams)
    download.excel(data, '农资销售监管台账(备查).xls')
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
const printRef = ref()
const handlePrint = async (row: any) => {
  // 确保有 items 数据 (如果列表没带 items，则额外查询一次)
  if (!row.items || row.items.length === 0) {
    const data = await SaleOrderApi.getSaleOrder(row.id)
    printRef.value.open(data)
  } else {
    printRef.value.open(row)
  }
}

/** 视频回放相关 */
const videoVisible = ref(false)
const videoUrl = ref('')
const currentCameraId = ref('')
const currentOrderTime = ref('')
const handlePlayVideo = async (row: SaleOrderVO) => {
  currentCameraId.value = row.cameraId
  currentOrderTime.value = dateFormatter(null as any, null as any, row.orderTime)
  videoUrl.value = ''
  videoVisible.value = true
  try {
    // 优先使用已抓取的本地/云端视频
    if (row.videoUrl) {
      videoUrl.value = row.videoUrl
      return
    }
    // 否则动态获取回放地址
    // 默认使用后端配置的分钟数
    videoUrl.value = await AgriReportApi.getPlaybackUrl(row.id, 'sale_order')
    if (!videoUrl.value) {
      message.error('无法获取该时段的监控回放，请检查设备连通性或云存储状态')
      videoVisible.value = false
    }
  } catch (e) {
    console.error(e)
    videoVisible.value = false
  }
}
</script>
