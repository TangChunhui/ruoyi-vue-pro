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

    <!-- 3. 临期库存 & 资质预警 -->
    <el-row :gutter="20" class="mt-20px">
      <el-col :lg="16" :md="24">
        <el-card shadow="hover" class="warning-card">
          <template #header>
            <div class="card-header flex justify-between items-center">
              <div class="flex items-center">
                <Icon icon="ep:warning" color="#F56C6C" class="mr-8px" />
                <span class="font-bold">临期库存自动预警 - 优先去仓 (FIFO)</span>
              </div>
              <el-button type="primary" link @click="router.push({ name: 'ErpAgriStockBalance' })">查看完整台账</el-button>
            </div>
          </template>
          <el-table :data="expiringList" stripe style="width: 100%" v-loading="loading" height="300">
            <el-table-column prop="productName" label="产品名称" min-width="150" />
            <el-table-column prop="batchNo" label="批次" width="120" />
            <el-table-column prop="closingStock" label="库存" width="100">
              <template #default="scope">
                <span class="text-orange-600 font-bold">{{ scope.row.closingStock }}</span> {{ scope.row.unitName }}
              </template>
            </el-table-column>
            <el-table-column label="风险等级" width="120">
              <template #default="scope">
                <el-tag :type="getExpiryTag(scope.row.expiryDate)" effect="dark" size="small">
                  {{ getExpiryDesc(scope.row.expiryDate) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :lg="8" :md="24">
        <el-card shadow="hover" class="h-full">
          <template #header>
            <div class="flex items-center">
              <Icon icon="ep:medal" color="#E6A23C" class="mr-8px" />
              <span class="font-bold">限用农药采购大户 (合规监控)</span>
            </div>
          </template>
          <el-table :data="leaderboard" stripe size="small" height="300">
            <el-table-column type="index" width="50" label="排名" />
            <el-table-column prop="customerName" label="购药人" />
            <el-table-column prop="count" label="数量" width="80" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 4. 供应商资质到期提醒 -->
    <el-row :gutter="20" class="mt-20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="flex items-center text-red-600">
              <Icon icon="ep:clock" class="mr-8px" />
              <span class="font-bold">供应商经营资质过期预警 (90天内)</span>
            </div>
          </template>
          <el-table :data="supplierWarnings" stripe style="width: 100%">
            <el-table-column prop="name" label="供应商名称" />
            <el-table-column prop="businessLicenseNo" label="经营许可证号" />
            <el-table-column prop="contact" label="联系人" />
            <el-table-column prop="mobile" label="手机号" />
            <el-table-column label="有效期至" width="150">
              <template #default="scope">
                <span :class="{'text-red-500 font-bold': isExpired(scope.row.licenseExpiryDate)}">
                  {{ dateFormatter(null, null, scope.row.licenseExpiryDate) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="isExpired(scope.row.licenseExpiryDate) ? 'danger' : 'warning'">
                  {{ isExpired(scope.row.licenseExpiryDate) ? '已过期' : '即将过期' }}
                </el-tag>
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
const leaderboard = ref([])
const supplierWarnings = ref([])
const loading = ref(true)
const trendMode = ref('营收')

const statCards = computed(() => [
  {
    title: '今日销售总额',
    value: financeSummary.value.todaySalesAmount || 0,
    unit: '元',
    type: 'revenue',
    icon: 'ep:money',
    iconColor: '#03A9F4',
    iconBg: '#e1f5fe',
    trend: 12.5,
    footerText: '较昨日'
  },
  {
    title: '本月限用农药销售',
    value: warningStats.value.monthlyRestrictedSaleCount || 0,
    unit: '笔',
    type: 'restricted',
    icon: 'ep:warning',
    iconColor: '#FF9800',
    iconBg: '#fff3e0',
    trend: -2.1,
    footerText: '高毒限用合规管控'
  },
  {
    title: '临期预警批次',
    value: expiringList.value.length || 0,
    unit: '个',
    type: 'warning',
    icon: 'ep:clock',
    iconColor: '#E91E63',
    iconBg: '#fce4ec',
    trend: 0,
    footerText: '建议优先处理'
  },
  {
    title: '待收货款 (挂账)',
    value: financeSummary.value.totalReceivableAmount || 0,
    unit: '元',
    type: 'asset',
    icon: 'ep:wallet',
    iconColor: '#4CAF50',
    iconBg: '#e8f5e9',
    trend: 5.4,
    footerText: '授信风控中'
  }
])

const fastActions = [
  {
    name: '开具销售单',
    desc: '农资销售与处方录入',
    icon: 'ep:shopping-cart',
    color: '#409EFF',
    bg: '#ecf5ff',
    route: 'ErpSaleOrder'
  },
  {
    name: '入库登记',
    desc: '采购入库与批次录入',
    icon: 'ep:box',
    color: '#67C23A',
    bg: '#f0f9eb',
    route: 'ErpPurchaseOrder'
  },
  {
    name: '库存台账',
    desc: '进销存收发全记录',
    icon: 'ep:list',
    color: '#E6A23C',
    bg: '#fdf6ec',
    route: 'AgriStockBalanceReport'
  },
  {
    name: '视频中心',
    desc: '监控调取与归档查看',
    icon: 'ep:video-camera',
    color: '#F56C6C',
    bg: '#fef0f0',
    route: 'ErpAgriDashboard'
  }
]

const dailyStats = computed(() => financeSummary.value.dailyStats || [])

const chartOptions = computed(() => ({
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: dailyStats.value.map((i: any) => i.date),
    axisLine: { lineStyle: { color: '#eee' } },
    axisLabel: { color: '#999' }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { type: 'dashed', color: '#f5f5f5' } }
  },
  series: [
    {
      name: trendMode.value === '营收' ? '营收额(元)' : '交易笔数',
      type: 'line',
      smooth: true,
      data: trendMode.value === '营收'
        ? dailyStats.value.map((i: any) => i.revenue)
        : dailyStats.value.map((i: any) => i.count),
      lineStyle: { width: 4, color: '#409EFF' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(64,158,255,0.4)' },
            { offset: 1, color: 'rgba(64,158,255,0.1)' }
          ]
        }
      },
      itemStyle: { color: '#409EFF', borderWidth: 2 }
    }
  ]
}))

/** 获取统计数据 */
const fetchStats = async () => {
  loading.value = true
  try {
    const [overview, expiring, finance, leader, supplies] = await Promise.all([
      AgriReportApi.getAgriWarningOverview(),
      AgriReportApi.getExpiringStockList(180),
      AgriReportApi.getAgriFinanceSummary(),
      AgriReportApi.getRestrictedSaleLeaderboard(),
      AgriReportApi.getSupplierLicenseCountdown()
    ])
    warningStats.value = overview
    expiringList.value = expiring.slice(0, 10)
    financeSummary.value = finance
    leaderboard.value = leader
    supplierWarnings.value = supplies
  } finally {
    loading.value = false
  }
}

const isExpired = (date) => {
  if (!date) return false
  return dayjs(date).isBefore(dayjs())
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
