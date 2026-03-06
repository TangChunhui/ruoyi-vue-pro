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
      <el-table-column label="产品名称" align="center" prop="productName" />
      <el-table-column label="规格型号" align="center" prop="standard" />
      <el-table-column label="单位" align="center" prop="unitName" width="80" />
      
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

    </el-table>
  </ContentWrap>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { AgriReportApi } from '@/api/erp/agri/report'

defineOptions({ name: 'AgriStockBalanceReport' })

const message = useMessage()

const loading = ref(false)
const list = ref([])
const queryParams = reactive({
  timeRange: [] as string[]
})

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    let params = {}
    if (queryParams.timeRange && queryParams.timeRange.length === 2) {
      params = {
        beginTime: queryParams.timeRange[0],
        endTime: queryParams.timeRange[1]
      }
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

/** 搜索按钮操作 */
const handleQuery = () => {
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryParams.timeRange = []
  handleQuery()
}

/** 初始化 **/
onMounted(() => {
  // 默认查询当月
  const date = new Date()
  const firstDay = new Date(date.getFullYear(), date.getMonth(), 1)
  const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0, 23, 59, 59)
  
  const formatDate = (d: Date) => {
    const yyyy = d.getFullYear()
    const mm = String(d.getMonth() + 1).padStart(2, '0')
    const dd = String(d.getDate()).padStart(2, '0')
    const hh = String(d.getHours()).padStart(2, '0')
    const min = String(d.getMinutes()).padStart(2, '0')
    const ss = String(d.getSeconds()).padStart(2, '0')
    return `${yyyy}-${mm}-${dd} ${hh}:${min}:${ss}`
  }

  queryParams.timeRange = [formatDate(firstDay), formatDate(lastDay)]
  
  getList()
})
</script>
