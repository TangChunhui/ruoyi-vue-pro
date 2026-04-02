<template>
  <el-dialog v-model="dialogVisible" title="农资批次标签打印" width="400px" append-to-body>
    <div id="printArea" class="print-container">
      <div class="print-label">
        <div class="label-header">
          <div class="product-name">{{ batchInfo.productName }}</div>
          <div class="batch-tag">批次: {{ batchInfo.batchNo }}</div>
        </div>
        
        <div class="label-content">
          <div class="qrcode-wrapper">
            <Qrcode :text="qrCodeText" :size="100" />
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="label">规格:</span>
              <span class="value">{{ batchInfo.standard || '无' }}</span>
            </div>
            <div class="info-item">
              <span class="label">生产日期:</span>
              <span class="value">{{ formatDate(batchInfo.productionDate, 'YYYY-MM-DD') }}</span>
            </div>
            <div class="info-item">
              <span class="label">有效期至:</span>
              <span class="value" :class="{ 'text-red-500': isExpired }">
                {{ formatDate(batchInfo.expiryDate, 'YYYY-MM-DD') }}
              </span>
            </div>
          </div>
        </div>
        
        <div class="label-footer">
          <div class="trace-id">溯源码: {{ batchInfo.id }}</div>
          <div class="corp-name">农资合规监管系统</div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button v-print="'#printArea'" type="primary">打 印</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { Qrcode } from '@/components/Qrcode'
import { formatDate } from '@/utils/formatTime'
import dayjs from 'dayjs'

defineOptions({ name: 'BatchPrint' })

const dialogVisible = ref(false)
const batchInfo = ref<any>({})

const qrCodeText = computed(() => {
  // 模拟溯源链接或ID
  return `https://agri-trace.yudao.iocoder.cn/trace/${batchInfo.value.id || 'N/A'}`
})

const isExpired = computed(() => {
  if (!batchInfo.value.expiryDate) return false
  return dayjs(batchInfo.value.expiryDate).isBefore(dayjs())
})

/** 打开弹窗 */
const open = (row: any) => {
  batchInfo.value = { ...row }
  dialogVisible.value = true
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.print-container {
  display: flex;
  justify-content: center;
  padding: 10px;
}

.print-label {
  width: 320px;
  border: 2px solid #333;
  padding: 15px;
  background: #fff;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
  
  .label-header {
    border-bottom: 1px solid #333;
    padding-bottom: 8px;
    margin-bottom: 10px;
    
    .product-name {
      font-size: 18px;
      font-weight: bold;
      color: #000;
    }
    .batch-tag {
      font-size: 12px;
      color: #666;
      margin-top: 4px;
    }
  }
  
  .label-content {
    display: flex;
    align-items: center;
    
    .qrcode-wrapper {
      margin-right: 15px;
      border: 1px solid #eee;
      padding: 5px;
    }
    
    .info-list {
      flex: 1;
      .info-item {
        font-size: 13px;
        margin-bottom: 4px;
        .label { color: #888; margin-right: 5px; }
        .value { color: #333; font-weight: 500; }
      }
    }
  }
  
  .label-footer {
    margin-top: 15px;
    border-top: 1px dashed #ccc;
    padding-top: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .trace-id {
      font-size: 10px;
      color: #999;
    }
    .corp-name {
      font-size: 10px;
      color: #409EFF;
      font-weight: bold;
    }
  }
}

@media print {
  body * {
    visibility: hidden;
  }
  #printArea, #printArea * {
    visibility: visible;
  }
  #printArea {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }
}
</style>
