<template>
  <div class="agri-dashboard">
    <!-- 1. 顶部指标概览 (Premium Cards) -->
    <el-row :gutter="20">
      <el-col :lg="6" :md="12" :sm="12" :xs="24" v-for="(item, index) in statCards" :key="index">
        <el-card shadow="hover" :class="['stat-card', item.type]">
          <div class="flex justify-between items-center">
            <div class="card-info">
              <div class="stat-title text-14px text-gray-400">{{ item.title }}</div>
              <div class="stat-value text-28px font-bold mt-8px">
                {{ item.prefix }}{{ erpPriceInputFormatter(item.value) }}<small class="text-14px font-normal ml-4px">{{ item.unit }}</small>
              </div>
            </div>
            <div class="card-icon flex justify-center items-center w-48px h-48px rounded-12px" :style="{ background: item.iconBg }">
              <Icon :icon="item.icon" :color="item.iconColor" :size="24" />
            </div>
          </div>
          <div class="card-footer mt-15px pt-15px border-t border-gray-100 flex items-center text-12px">
            <span :class="item.trend > 0 ? 'text-green-500' : 'text-red-500'" class="font-bold flex items-center">
              <Icon :icon="item.trend > 0 ? 'ep:caret-top' : 'ep:caret-bottom'" />
              {{ Math.abs(item.trend) }}%
            </span>
            <span class="text-gray-400 ml-8px">{{ item.footerText }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 2. 快捷操作 & 图表 -->
    <el-row :gutter="20" class="mt-20px">
      <el-col :lg="8" :md="24">
        <el-card shadow="never" class="h-full">
          <template #header>
            <div class="flex justify-between items-center">
              <span class="font-bold text-16px">农资快捷工作台</span>
              <Icon icon="ep:menu" />
            </div>
          </template>
          <div class="quick-actions grid grid-cols-2 gap-15px">
            <div
              v-for="action in fastActions"
              :key="action.name"
              class="action-item flex flex-col items-center justify-center p-15px rounded-12px cursor-pointer hover:bg-blue-50 transition-all border border-transparent hover:border-blue-200"
              @click="handleAction(action.route)"
            >
              <div class="icon-box p-12px rounded-full mb-10px" :style="{ background: action.bg }">
                <Icon :icon="action.icon" :color="action.color" :size="20" />
              </div>
              <span class="text-14px font-medium text-gray-700">{{ action.name }}</span>
              <span class="text-12px text-gray-400 mt-4px">{{ action.desc }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :lg="16" :md="24">
        <el-card shadow="never">
          <template #header>
            <div class="flex justify-between items-center">
              <span class="font-bold text-16px">近15日营收趋势 (合规交易监控)</span>
              <el-radio-group v-model="trendMode" size="small">
                <el-radio-button label="营收" />
                <el-radio-button label="笔数" />
              </el-radio-group>
            </div>
          </template>
          <div ref="chartRef" style="height: 300px; width: 100%">
            <Echart :options="chartOptions" height="300px" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 3. 临期提醒雷达 -->
    <el-row :gutter="20" class="mt-20px">
      <el-col :span="24">
        <el-card shadow="hover" class="warning-card">
          <template #header>
            <div class="card-header flex justify-between items-center">
              <div class="flex items-center">
                <Icon icon="ep:warning" color="#F56C6C" class="mr-8px" />
                <span class="font-bold">临期库存自动预警 - 优先去仓 (FIFO)</span>
              </div>
              <div>
                <el-button type="danger" plain size="small" @click="exportExpiring">一键导出预警清单</el-button>
                <el-button type="primary" link @click="router.push({ name: 'ErpAgriStockBalance' })">查看完整台账</el-button>
              </div>
            </div>
          </template>
          <el-table :data="expiringList" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="productName" label="农药/农资名称" min-width="200">
              <template #default="scope">
                <div class="font-medium text-blue-600">{{ scope.row.productName }}</div>
                <div class="text-12px text-gray-400">{{ scope.row.productBarCode }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="batchNo" label="批次" width="120" />
            <el-table-column prop="closingStock" label="现有结余" width="120">
              <template #default="scope">
                <span class="font-bold text-orange-600">{{ scope.row.closingStock }}</span> {{ scope.row.unitName }}
              </template>
            </el-table-column>
            <el-table-column label="到期日期" width="130">
              <template #default="scope">
                {{ dateFormatter(null, null, scope.row.expiryDate) }}
              </template>
            </el-table-column>
            <el-table-column label="风险等级" width="120">
              <template #default="scope">
                <el-tag :type="getExpiryTag(scope.row.expiryDate)" effect="dark">
                  {{ getExpiryDesc(scope.row.expiryDate) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button type="primary" link @click="goSale(scope.row)">快速开单</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { AgriReportApi } from '@/api/erp/agri/report'
import { erpPriceInputFormatter } from '@/utils'
import dayjs from 'dayjs'
import { Echart } from '@/components/Echart'

/** 农资经营驾驶舱 - Premium Edition */
defineOptions({ name: 'AgriDashboard' })

const router = useRouter()
const warningStats = ref<any>({})
const financeSummary = ref<any>({})
const expiringList = ref([])
const loading = ref(true)
const trendMode = ref('营收')

// 指标卡片配置
const statCards = computed(() => [
  {
    title: '今日本店营收',
    value: warningStats.value.todaySalesAmount || 0,
    prefix: '¥ ',
    unit: '',
    type: 'revenue',
    icon: 'ep:money',
    iconBg: '#E1F5FE',
    iconColor: '#03A9F4',
    trend: 8.5,
    footerText: '较昨日同时段'
  },
  {
    title: '今日现金实收',
    value: financeSummary.value.todayReceiptAmount || 0,
    prefix: '¥ ',
    unit: '',
    type: 'income',
    icon: 'ep:bottom-right',
    iconBg: '#E8F5E9',
    iconColor: '#4CAF50',
    trend: 12.3,
    footerText: '实缴现金流'
  },
  {
    title: '当前总应收',
    value: financeSummary.value.totalReceivableAmount || 0,
    prefix: '¥ ',
    unit: '',
    type: 'restricted',
    icon: 'ep:reading-lamp',
    iconBg: '#FFF3E0',
    iconColor: '#FF9800',
    trend: -1.2,
    footerText: '农户挂账总额'
  },
  {
    title: '临期库存预警',
    value: expiringList.value.length || 0,
    prefix: '',
    unit: '批',
    type: 'warning',
    icon: 'ep:timer',
    iconBg: '#FFEBEE',
    iconColor: '#E91E63',
    trend: 12.0,
    footerText: '高风险 (15天内)'
  }
])

// 快捷操作配置
const fastActions = [
  { name: '开方销售', desc: '合规电子开单', icon: 'ep:shopping-cart', color: '#409EFF', bg: '#ecf5ff', route: 'ErpSaleOrder' },
  { name: '财务日报', desc: '今日收支结存', icon: 'ep:set-up', color: '#67C23A', bg: '#f0f9eb', route: 'ErpAgriFinance' },
  { name: '资产台账', desc: '实时收发存', icon: 'ep:data-analysis', color: '#E6A23C', bg: '#fdf6ec', route: 'ErpAgriSalesDetail' },
  { name: '效期盘点', desc: '先到先出指引', icon: 'ep:memo', color: '#F56C6C', bg: '#fef0f0', route: 'ErpAgriStockBalance' }
]

// 图表配置 (Mock Trend)
const chartOptions = computed(() => ({
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['3-13', '3-14', '3-15', '3-16', '3-17', '3-18', '3-19', '3-20', '3-21', '3-22', '3-23', '3-24', '3-25', '3-26', '今日'],
    axisLine: { lineStyle: { color: '#eee' } },
    axisLabel: { color: '#999' }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { type: 'dashed', color: '#eee' } },
    axisLabel: { color: '#999' }
  },
  tooltip: { trigger: 'axis' },
  series: [
    {
      name: trendMode.value,
      type: 'line',
      smooth: true,
      data: [1200, 1500, 800, 2100, 1800, 2400, 3200, 2800, 1900, 2600, 3100, 4200, 3800, 4500, warningStats.value.todaySalesAmount || 0],
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: 'rgba(64, 158, 255, 0.4)' }, { offset: 1, color: 'rgba(64, 158, 255, 0)' }]
        }
      },
      itemStyle: { color: '#409EFF' },
      lineStyle: { width: 3 }
    }
  ]
}))

/** 获取统计数据 */
const fetchStats = async () => {
  loading.value = true
  try {
    const [overview, expiring, finance] = await Promise.all([
      AgriReportApi.getAgriWarningOverview(),
      AgriReportApi.getExpiringStockList(180),
      AgriReportApi.getAgriFinanceSummary()
    ])
    warningStats.value = overview
    expiringList.value = expiring.slice(0, 10)
    financeSummary.value = finance
  } finally {
    loading.value = false
  }
}

const getExpiryTag = (date) => {
  const days = dayjs(date).diff(dayjs(), 'day')
  if (days < 30) return 'danger'
  if (days < 90) return 'warning'
  return 'info'
}

const getExpiryDesc = (date) => {
  const days = dayjs(date).diff(dayjs(), 'day')
  if (days <= 0) return '已过期'
  if (days < 10) return `急需出库 (${days}天)`
  return `${days}天后到期`
}

const handleAction = (routeName) => {
  router.push({ name: routeName })
}

const goSale = (row) => {
  router.push({ name: 'ErpSaleOrder', query: { productId: row.productId } })
}

const exportExpiring = () => {
  // TODO: 后续对接导出
}

onMounted(fetchStats)
</script>

<style scoped lang="scss">
.agri-dashboard {
  padding: 20px;
  @media (max-width: 768px) {
    padding: 10px;
  }
}

.stat-card {
  border-radius: 12px;
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 20px;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.05) !important;
  }
  
  @media (max-width: 768px) {
    .stat-value {
      font-size: 20px;
    }
  }
}
.revenue { border-bottom: 4px solid #03A9F4; }
.restricted { border-bottom: 4px solid #FF9800; }
.warning { border-bottom: 4px solid #E91E63; }
.asset { border-bottom: 4px solid #4CAF50; }

.quick-actions {
  .action-item {
    background: #fcfcfc;
    padding: 10px;
    &:active {
      transform: scale(0.95);
    }
    @media (max-width: 768px) {
      .icon-box {
        padding: 8px;
        margin-bottom: 5px;
      }
      span.text-14px {
        font-size: 13px;
      }
    }
  }
}

.warning-card {
  border-radius: 12px;
  :deep(.el-card__header) {
    background: #fffafa;
    @media (max-width: 768px) {
      padding: 10px 15px;
      .card-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 10px;
      }
    }
  }
}
</style>
