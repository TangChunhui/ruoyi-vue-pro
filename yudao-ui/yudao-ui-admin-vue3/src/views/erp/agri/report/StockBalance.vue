<template>
  <doc-alert title="【农业】进销存收发台账" url="https://doc.iocoder.cn/erp/" />

  <ContentWrap>
    <!-- 搜索栏 -->
    <el-form class="-mb-15px" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="单据时间" prop="timeRange">
        <el-date-picker
          v-model="queryParams.timeRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD HH:mm:ss"
          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
        />
      </el-form-item>
      <el-form-item label="生产批次" prop="batchNo">
        <el-input
          v-model="queryParams.batchNo"
          placeholder="请输入生产批次"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 数据报表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :show-overflow-tooltip="true"
      border
      show-summary
    >
      <el-table-column label="产品编号" align="center" prop="productId" width="80" />
      <el-table-column label="产品名称" align="center" prop="productName">
        <template #default="{ row }">
          <span>{{ row.productName }}</span>
          <el-tag v-if="row.isRestricted" type="danger" size="small" class="ml-1">高毒限用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="规格型号" align="center" prop="standard" />
      <el-table-column label="登记证有效期" align="center" width="160">
        <template #default="{ row }">
          <span :class="{ 'text-red-500 font-bold': isExpired(row.registrationExpiryDate) }">
            {{ formatDateLabel(row.registrationExpiryDate) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="单位" align="center" prop="unitName" width="80" />
      <el-table-column label="生产批次" align="center" prop="batchNo" width="120" />
      <el-table-column label="生产日期" align="center" width="120">
        <template #default="{ row }">
          {{ formatDateLabel(row.productionDate) }}
        </template>
      </el-table-column>
      <el-table-column label="有效期至" align="center" width="120">
        <template #default="{ row }">
          <span :class="{ 'text-red-500 font-bold': isExpired(row.expiryDate) }">
            {{ formatDateLabel(row.expiryDate) }}
          </span>
        </template>
      </el-table-column>
      
      <el-table-column label="期初库存" align="center">
        <el-table-column label="数量" align="center" prop="openingStock" />
      </el-table-column>
      
      <el-table-column label="本期入库" align="center">
        <el-table-column label="数量" align="center" prop="inboundStock" />
      </el-table-column>

      <el-table-column label="本期出库" align="center">
        <el-table-column label="数量" align="center" prop="outboundStock" />
      </el-table-column>

      <el-table-column label="期末库存" align="center">
        <el-table-column label="数量" align="center" prop="closingStock" />
      </el-table-column>

      <el-table-column label="操作" align="center" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handlePrint(row)">
            <Icon icon="ep:printer" class="mr-2px" /> 标签
          </el-button>
          <el-button type="success" link @click="handleTrace(row)">
            <Icon icon="ep:link" class="mr-2px" /> 溯源
          </el-button>
        </template>
      </el-table-column>

    </el-table>
  </ContentWrap>

  <!-- 弹窗：打印标签 -->
  <BatchPrint ref="printRef" />
  <!-- 抽屉：溯源图 -->
  <TraceabilityGraph ref="traceRef" />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { AgriReportApi } from '@/api/erp/agri/report'
import { useMessage } from '@/hooks/web/useMessage'
import dayjs from 'dayjs'
import BatchPrint from './components/BatchPrint.vue'
import TraceabilityGraph from './components/TraceabilityGraph.vue'

defineOptions({ name: 'AgriStockBalanceReport' })

const message = useMessage()

const loading = ref(false)
const list = ref([])
const queryParams = reactive({
  timeRange: [] as string[],
  batchNo: undefined
})

const printRef = ref()
const traceRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const params: any = {
      batchNo: queryParams.batchNo
    }
    if (queryParams.timeRange && queryParams.timeRange.length === 2) {
      params.beginTime = queryParams.timeRange[0]
      params.endTime = queryParams.timeRange[1]
    }
    const data = await AgriReportApi.getStockBalanceReport(params)
    list.value = data || []
  } catch (error) {
    console.error(error)
    message.error('获取报表数据失败')
  } finally {
    loading.value = false
  }
}

/** 打印标签 */
const handlePrint = (row: any) => {
  printRef.value.open(row)
}

/** 查看溯源 */
const handleTrace = (row: any) => {
  traceRef.value.open(row)
}

/** 格式化日期显示 */
const formatDateLabel = (val) => {
  return val ? dayjs(val).format('YYYY-MM-DD') : '--'
}

/** 判断是否过期 */
const isExpired = (val) => {
  if (!val) return false
  return dayjs(val).isBefore(dayjs())
}

/** 搜索按钮操作 */
const handleQuery = () => {
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryParams.timeRange = []
  queryParams.batchNo = undefined
  handleQuery()
}

/** 初始化 **/
onMounted(() => {
  // 默认查询当月
  const date = new Date()
  const firstDay = new Date(date.getFullYear(), date.getMonth(), 1)
  const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0, 23, 59, 59)
  
  const formatDateStr = (d: Date) => {
    const yyyy = d.getFullYear()
    const mm = String(d.getMonth() + 1).padStart(2, '0')
    const dd = String(d.getDate()).padStart(2, '0')
    const hh = String(d.getHours()).padStart(2, '0')
    const min = String(d.getMinutes()).padStart(2, '0')
    const ss = String(d.getSeconds()).padStart(2, '0')
    return `${yyyy}-${mm}-${dd} ${hh}:${min}:${ss}`
  }

  queryParams.timeRange = [formatDateStr(firstDay), formatDateStr(lastDay)]
  
  getList()
})
</script>
