<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="100px"
    >
      <el-form-item label="销售日期" prop="orderTime">
        <el-date-picker
          v-model="queryParams.orderTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('2024-01-01 00:00:00'), new Date('2024-01-01 23:59:59')]"
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
      <el-form-item label="客户/农户" prop="customerId">
        <el-select
          v-model="queryParams.customerId"
          clearable
          filterable
          placeholder="请选择客户"
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
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="success" plain @click="handleExport" :loading="exportLoading">
          <Icon icon="ep:download" class="mr-5px" /> 导出当日清单
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="销售日期" align="center" prop="orderTime" :formatter="dateFormatter" width="120px" />
      <el-table-column label="销售单号" align="center" prop="orderNo" min-width="150" />
      <el-table-column label="农资名称" align="center" prop="productName" min-width="150" />
      <el-table-column label="规格型号" align="center" prop="standard" width="100px" />
      <el-table-column label="销售数量" align="center" prop="count" width="100px">
        <template #default="scope">
          {{ scope.row.count }} {{ scope.row.unitName }}
        </template>
      </el-table-column>
      <el-table-column label="生产批次" align="center" prop="batchNo" width="120px" />
      <el-table-column label="有效期至" align="center" prop="expiryDate" :formatter="dateFormatter" width="120px" />
      <el-table-column label="购买农户" align="center" prop="customerName" width="120px" />
      <el-table-column label="农户证件号" align="center" prop="buyerIdCard" min-width="180px">
        <template #default="scope">
          <el-tag v-if="scope.row.buyerIdCard" type="info">{{ scope.row.buyerIdCard }}</el-tag>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="建议用量/用途" align="center" prop="usageIntent" min-width="200px">
        <template #default="scope">
          <div v-if="scope.row.usageIntent">用途: {{ scope.row.usageIntent }}</div>
          <div v-if="scope.row.dosageAdvice">用法: {{ scope.row.dosageAdvice }}</div>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { AgriReportApi } from '@/api/erp/agri/report'
import { ProductApi, ProductVO } from '@/api/erp/product/product'
import { CustomerApi, CustomerVO } from '@/api/erp/sale/customer'
import dayjs from 'dayjs'

/** 农资当日销售明细 */
defineOptions({ name: 'AgriSalesDetail' })

const loading = ref(true)
const exportLoading = ref(false)
const list = ref([])
const productList = ref<ProductVO[]>([])
const customerList = ref<CustomerVO[]>([])

const queryParams = reactive({
  orderTime: [
    dayjs().startOf('day').format('YYYY-MM-DD HH:mm:ss'),
    dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss')
  ],
  productId: undefined,
  customerId: undefined
})
const queryFormRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const params = {
      beginTime: queryParams.orderTime?.[0],
      endTime: queryParams.orderTime?.[1],
      productId: queryParams.productId,
      customerId: queryParams.customerId
    }
    const data = await AgriReportApi.getSalesDetailList(params)
    list.value = data
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  queryParams.orderTime = [
    dayjs().startOf('day').format('YYYY-MM-DD HH:mm:ss'),
    dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss')
  ]
  handleQuery()
}

/** 导出操作 */
const handleExport = async () => {
  // 简易模拟导出逻辑
  console.log('导出销售明细', queryParams)
  const message = useMessage()
  message.success('导出数据生成中，请稍后在下载中心查看')
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  productList.value = await ProductApi.getProductSimpleList()
  customerList.value = await CustomerApi.getCustomerSimpleList()
})
</script>
