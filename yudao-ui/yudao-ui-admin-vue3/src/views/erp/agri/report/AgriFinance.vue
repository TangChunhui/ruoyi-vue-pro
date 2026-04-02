<template>
  <div class="agri-finance">
    <!-- 1. 核心财务指标 (Premium Layout) -->
    <el-row :gutter="20">
      <el-col :lg="6" :md="12" :sm="12" :xs="24">
        <el-card shadow="hover" class="finance-card receivable">
          <div class="flex justify-between items-center">
            <div>
              <div class="text-14px text-gray-400">当前总应收 (农户欠款)</div>
              <div class="text-24px font-bold mt-8px text-orange-600">
                ¥ {{ erpPriceInputFormatter(summary.totalReceivableAmount || 0) }}
              </div>
            </div>
            <div class="icon-circle bg-orange-50 text-orange-500">
              <Icon icon="ep:reading-lamp" :size="20" />
            </div>
          </div>
          <div class="mt-15px text-12px text-gray-400">全量农户/客户实时挂账总计</div>
        </el-card>
      </el-col>
      <el-col :lg="6" :md="12" :sm="12" :xs="24">
        <el-card shadow="hover" class="finance-card income">
          <div class="flex justify-between items-center">
            <div>
              <div class="text-14px text-gray-400">今日实收金额</div>
              <div class="text-24px font-bold mt-8px text-green-600">
                ¥ {{ erpPriceInputFormatter(summary.todayReceiptAmount || 0) }}
              </div>
            </div>
            <div class="icon-circle bg-green-50 text-green-500">
              <Icon icon="ep:bottom-right" :size="20" />
            </div>
          </div>
          <div class="mt-15px text-12px text-gray-400">今日已审核收款单据汇总</div>
        </el-card>
      </el-col>
      <el-col :lg="6" :md="12" :sm="12" :xs="24">
        <el-card shadow="hover" class="finance-card expense">
          <div class="flex justify-between items-center">
            <div>
              <div class="text-14px text-gray-400">今日实付金额</div>
              <div class="text-24px font-bold mt-8px text-red-600">
                ¥ {{ erpPriceInputFormatter(summary.todayPaymentAmount || 0) }}
              </div>
            </div>
            <div class="icon-circle bg-red-50 text-red-500">
              <Icon icon="ep:top-right" :size="20" />
            </div>
          </div>
          <div class="mt-15px text-12px text-gray-400">今日已审核付款单据汇总</div>
        </el-card>
      </el-col>
      <el-col :lg="6" :md="12" :sm="12" :xs="24">
        <el-card shadow="hover" class="finance-card net">
          <div class="flex justify-between items-center">
            <div>
              <div class="text-14px text-gray-400">今日净现金流</div>
              <div class="text-24px font-bold mt-8px" :class="summary.todayNetCashFlow >= 0 ? 'text-blue-600' : 'text-red-500'">
                ¥ {{ erpPriceInputFormatter(summary.todayNetCashFlow || 0) }}
              </div>
            </div>
            <div class="icon-circle bg-blue-50 text-blue-500">
              <Icon icon="ep:coin" :size="20" />
            </div>
          </div>
          <div class="mt-15px text-12px text-gray-400">本日资金盈余/亏缺状态</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 2. 当日收支流水 -->
    <el-card shadow="never" class="mt-20px">
      <template #header>
        <div class="flex justify-between items-center">
          <div class="flex items-center">
            <Icon icon="ep:list" class="mr-8px" />
            <span class="font-bold">农资当日收支流水 (对账日报)</span>
          </div>
          <div>
            <el-button type="primary" plain size="small" @click="handleExport">导出对账单</el-button>
            <el-button @click="fetchSummary" circle><Icon icon="ep:refresh" /></el-button>
          </div>
        </div>
      </template>

      <el-table :data="summary.flowList" v-loading="loading" stripe style="width: 100%">
        <el-table-column label="交易时间" prop="time" width="180">
          <template #default="scope">
            {{ dateFormatter(null, null, scope.row.time) }}
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : 'danger'" effect="light">
              {{ scope.row.type === 1 ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="交易单号" prop="no" min-width="150" />
        <el-table-column label="往来单位" prop="businessName" min-width="150" />
        <el-table-column label="结算账户" prop="accountName" width="150" />
        <el-table-column label="金额" prop="amount" width="150">
          <template #default="scope">
            <span :class="scope.row.type === 1 ? 'text-green-600' : 'text-red-600'" class="font-bold">
              {{ scope.row.type === 1 ? '+' : '-' }} ¥{{ erpPriceInputFormatter(scope.row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="viewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { AgriReportApi } from '@/api/erp/agri/report'
import { erpPriceInputFormatter } from '@/utils'
import { dateFormatter } from '@/utils/formatTime'

/** 农资财务概览 */
defineOptions({ name: 'AgriFinance' })

const router = useRouter()
const message = useMessage()
const loading = ref(false)
const summary = ref<any>({
  totalReceivableAmount: 0,
  todayReceiptAmount: 0,
  todayPaymentAmount: 0,
  todayNetCashFlow: 0,
  todaySalesAmount: 0,
  flowList: []
})

const fetchSummary = async () => {
  loading.value = true
  try {
    summary.value = await AgriReportApi.getAgriFinanceSummary()
  } finally {
    loading.value = false
  }
}

const handleExport = () => {
  message.success('财务流水对账单导出中...')
}

const viewDetail = (row) => {
  if (row.type === 1) {
    // 跳转到收款单
    router.push({ name: 'ErpFinanceReceipt' })
  } else {
    // 跳转到付款单
    router.push({ name: 'ErpFinancePayment' })
  }
}

onMounted(fetchSummary)
</script>

<style scoped lang="scss">
.agri-finance {
  padding: 20px;
  @media (max-width: 768px) {
    padding: 10px;
  }
}

.finance-card {
  border-radius: 12px;
  border: none;
  margin-bottom: 20px;
  
  .icon-circle {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  @media (max-width: 768px) {
    .text-24px {
      font-size: 20px;
    }
    .icon-circle {
      width: 32px;
      height: 32px;
      :deep(.el-icon) {
        font-size: 16px !important;
      }
    }
  }
  
  &.receivable { border-top: 4px solid #ff9800; }
  &.income { border-top: 4px solid #67c23a; }
  &.expense { border-top: 4px solid #f56c6c; }
  &.net { border-top: 4px solid #409eff; }
}
</style>
