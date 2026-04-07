<template>
  <el-drawer v-model="drawerVisible" title="农资批次全生命周期追溯" size="600px" direction="rtl">
    <div class="trace-container" v-loading="traceLoading">
      <!-- 1. 核心批次信息 -->
      <div class="batch-header-card">
        <div class="flex items-center mb-10px">
          <Icon icon="ep:reading-lamp" color="#409EFF" :size="24" class="mr-10px" />
          <span class="text-18px font-bold">{{ batchData.productName }}</span>
        </div>
        <div class="grid grid-cols-2 gap-10px">
          <div class="info-item">
            <span class="label">当前批次:</span>
            <span class="value">{{ batchData.batchNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">入库总量:</span>
            <span class="value">{{ batchData.totalInCount || 0 }} {{ batchData.unitName }}</span>
          </div>
          <div class="info-item">
            <span class="label">当前库存:</span>
            <span class="value text-orange-600 font-bold">{{ batchData.closingStock || 0 }} {{ batchData.unitName }}</span>
          </div>
          <div class="info-item">
            <span class="label">质保状态:</span>
            <el-tag :type="expiryTag" size="small">{{ expiryDesc }}</el-tag>
          </div>
        </div>
      </div>

      <!-- 2. 追溯链路图 (Simplified Steps) -->
      <div class="mt-20px">
        <div class="section-title">
          <Icon icon="ep:link" class="mr-5px" /> 溯源链路
        </div>
        <el-steps direction="vertical" :active="99" class="trace-steps">
          <!-- 阶段1: 采集/采购 -->
          <el-step title="源头采集 / 采购入库">
            <template #description>
              <div class="step-content">
                <div class="time">{{ formatDate(batchData.firstInTime, 'YYYY-MM-DD HH:mm') }}</div>
                <div class="detail">供应商: {{ batchData.supplierName || '自有基地' }}</div>
                <div class="detail">入库单号: {{ batchData.firstInNo || 'ERP-INIT-001' }}</div>
                <div class="badge success">进项合规</div>
              </div>
            </template>
          </el-step>

          <!-- 阶段2: 仓储/质检 -->
          <el-step title="仓储中心 / 在库监管">
            <template #description>
              <div class="step-content">
                <div class="detail">存储仓库: {{ batchData.warehouseName }}</div>
                <div class="detail">环境状态: 常温干爽 (合规)</div>
                <div class="detail">生产日期: {{ formatDate(batchData.productionDate, 'YYYY-MM-DD') }}</div>
              </div>
            </template>
          </el-step>

          <!-- 阶段3: 销售/流向 -->
          <el-step title="分销流转 / 终端销售">
            <template #description>
              <div class="step-content">
                <div class="detail">已累计销售: {{ batchData.totalOutCount || 0 }} {{ batchData.unitName }}</div>
                <div class="detail" v-if="batchData.salesCount > 0">涉及销售单数: {{ batchData.salesCount }} 笔</div>
                <div class="badge info" v-if="batchData.isRestricted">高毒限用管控</div>
                
                <!-- 联动视频回放 -->
                <div v-if="batchData.hasVideo" class="mt-10px flex items-center cursor-pointer text-blue-500" @click="handlePlayVideo(batchData)">
                  <Icon icon="ep:video-play" class="mr-4px" />
                  <span class="text-12px font-bold">查看销售现场存证视频</span>
                </div>
              </div>
            </template>
          </el-step>

          <!-- 阶段4: 最终流向 (Farmers) -->
          <el-step title="最终流向 / 农户施用">
            <template #description>
              <div class="step-content">
                <div class="detail">主要覆盖农户: {{ batchData.mainCustomerNames || '区域散户' }}</div>
                <div class="detail text-gray-400 italic">点击查看下游明细台账...</div>
                
                <el-button type="primary" plain size="small" class="mt-8px" @click="handlePrintPrescription(batchData)">
                   <Icon icon="ep:document" class="mr-4px" /> 下载合规处方单
                </el-button>
              </div>
            </template>
          </el-step>
        </el-steps>
      </div>

      <!-- 3. 二维码溯源卡 -->
      <div class="trace-footer mt-40px p-20px bg-gray-50 rounded-12px flex items-center justify-between">
        <div>
          <div class="font-bold text-14px mb-5px">电子溯源存证</div>
          <div class="text-12px text-gray-400">扫码查看区块链溯源凭证</div>
        </div>
        <Qrcode :text="qrText" :size="64" />
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { Qrcode } from '@/components/Qrcode'
import { AgriReportApi } from '@/api/erp/agri/report'
import { formatDate } from '@/utils/formatTime'
import dayjs from 'dayjs'

defineOptions({ name: 'TraceabilityGraph' })

const drawerVisible = ref(false)
const batchData = ref<any>({})
const traceLoading = ref(false)

const qrText = computed(() => `https://trace.iocoder.cn/qr/${batchData.value.batchNo || 'empty'}`)

const expiryTag = computed(() => {
  const days = dayjs(batchData.value.expiryDate).diff(dayjs(), 'day')
  if (days < 0) return 'danger'
  if (days < 30) return 'warning'
  return 'success'
})

const expiryDesc = computed(() => {
  const days = dayjs(batchData.value.expiryDate).diff(dayjs(), 'day')
  if (days < 0) return '已过期'
  if (days < 30) return `临期 (${days}天)`
  return '质保期内'
})

/** 打开抽屉 */
const open = (row: any) => {
  batchData.value = { ...row }
  drawerVisible.value = true
  fetchFullTrace(row.productId, row.batchNo)
}

const fetchFullTrace = async (productId: number, batchNo: string) => {
  if (!productId) return
  traceLoading.value = true
  try {
    const detail = await AgriReportApi.getBatchTraceDetail(productId, batchNo)
    if (detail) {
      // 合并接口返回的溯源详情（保留原有字段作为兜底）
      batchData.value = { ...batchData.value, ...detail }
    }
  } catch {
    // 接口失败时保留原始行数据继续展示
  } finally {
    traceLoading.value = false
  }
}

const handlePlayVideo = (data: any) => {
  // 联动全局视频播放逻辑，或者跳转到销售单视频
  console.log('Playing video for batch', data.batchNo)
}

const handlePrintPrescription = (data: any) => {
  // 联动打印逻辑
  console.log('Printing prescription for batch', data.batchNo)
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.trace-container {
  padding: 0 10px;
}

.batch-header-card {
  background: linear-gradient(135deg, #f0f7ff 0%, #ffffff 100%);
  padding: 15px;
  border-radius: 12px;
  border: 1px solid #e1f0ff;
  
  .info-item {
    font-size: 13px;
    .label { color: #888; margin-right: 8px; }
    .value { color: #333; font-weight: 500; }
  }
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  color: #333;
}

.trace-steps {
  :deep(.el-step__description) {
    padding-right: 0;
  }
  
  .step-content {
    background: #fafafa;
    padding: 12px;
    border-radius: 8px;
    margin: 10px 0 20px 0;
    position: relative;
    
    .time {
      font-size: 12px;
      color: #999;
      margin-bottom: 5px;
    }
    
    .detail {
      font-size: 13px;
      color: #555;
      margin-bottom: 2px;
    }
    
    .badge {
      position: absolute;
      top: 12px;
      right: 12px;
      font-size: 11px;
      padding: 2px 6px;
      border-radius: 4px;
      &.success { background: #e8f5e9; color: #4caf50; }
      &.info { background: #e3f2fd; color: #2196f3; }
    }
  }
}
</style>
