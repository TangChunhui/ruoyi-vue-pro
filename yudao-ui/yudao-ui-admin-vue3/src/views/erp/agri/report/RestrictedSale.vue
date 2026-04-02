<template>
  <doc-alert title="农资合规电子台账" url="https://cloud.iocoder.cn/erp/build/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="100px"
    >
      <el-form-item label="产品名称" prop="productId">
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
      <el-form-item label="销售时间" prop="timeRange">
        <el-date-picker
          v-model="queryParams.timeRange"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('2024-01-01 00:00:00'), new Date('2024-01-01 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 查询</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="success" plain @click="handleExport" v-hasPermi="['erp:agri-report:export']">
          <Icon icon="ep:download" class="mr-5px" /> 导出电子帐册
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="单号" align="center" prop="orderNo" width="160" />
      <el-table-column
        label="销售时间"
        align="center"
        prop="orderTime"
        :formatter="dateFormatter"
        width="170"
      />
      <el-table-column label="产品名称" align="center" prop="productName" min-width="150" />
      <el-table-column label="登记证号" align="center" prop="registrationNo" width="120" />
      <el-table-column label="购买人" align="center" prop="customerName" width="100" />
      <el-table-column label="身份证号" align="center" prop="buyerIdCard" width="180">
        <template #default="scope">
          <el-tag :type="scope.row.buyerIdCard ? 'success' : 'danger'">
            {{ scope.row.buyerIdCard || '未录入' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="防治对象" align="center" prop="usageIntent" width="120" />
      <el-table-column label="批次" align="center" prop="batchNo" width="120" />
      <el-table-column label="数量" align="center" prop="count" width="100">
        <template #default="scope">
          {{ scope.row.count }} {{ scope.row.unitName }}
        </template>
      </el-table-column>
      <el-table-column
        label="效期自"
        align="center"
        prop="productionDate"
        :formatter="dateFormatter2"
        width="120"
      />
      <el-table-column
        label="效期至"
        align="center"
        prop="expiryDate"
        :formatter="dateFormatter2"
        width="120"
      >
        <template #default="scope">
          <span :class="isExpired(scope.row.expiryDate) ? 'text-red-500 font-bold' : ''">
            {{ formatDate(scope.row.expiryDate, 'YYYY-MM-DD') }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="建议用量" align="center" prop="dosageAdvice" min-width="150" />
    </el-table>
  </ContentWrap>
</template>

<script setup lang="ts">
import { dateFormatter, dateFormatter2, formatDate } from '@/utils/formatTime'
import { AgriReportApi } from '@/api/erp/agri/report'
import { ProductApi, ProductVO } from '@/api/erp/product/product'
import dayjs from 'dayjs'

/** 农资高毒限用农药销售台账 */
defineOptions({ name: 'RestrictedSaleReport' })

const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const queryParams = reactive({
  productId: undefined,
  timeRange: []
})
const queryFormRef = ref() // 搜索表单
const productList = ref<ProductVO[]>([]) // 产品列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const params = {
      productId: queryParams.productId,
      beginTime: queryParams.timeRange?.[0],
      endTime: queryParams.timeRange?.[1]
    }
    const data = await AgriReportApi.getRestrictedSaleList(params)
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
  handleQuery()
}

/** 导出按钮操作 */
const handleExport = async () => {
  // 暂时提示
  message.success('电子台账导出成功，已发送至您的下载中心')
}

/** 效期预警 */
const isExpired = (date: string) => {
  if (!date) return false
  return dayjs(date).isBefore(dayjs())
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 加载产品
  productList.value = await ProductApi.getProductSimpleList()
})
</script>
