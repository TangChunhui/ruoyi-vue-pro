<template>
  <el-dialog v-model="dialogVisible" title="农资销售处方单/合规凭证" width="800px" append-to-body>
    <div id="compliancePrintArea" class="prescription-container">
      <div class="prescription-header">
        <div class="title">农资凭证 (处方单)</div>
        <div class="order-no">订单号: {{ orderInfo.no }}</div>
      </div>
      
      <div class="buyer-info-section">
        <el-row :gutter="10">
          <el-col :span="12">
            <div class="info-group">
              <span class="label">购药人/单位:</span>
              <span class="value">{{ orderInfo.customerName || '散客' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-group">
              <span class="label">身份证号:</span>
              <span class="value">{{ orderInfo.buyerIdCard || '未录入' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-group">
              <span class="label">联系电话:</span>
              <span class="value">{{ orderInfo.customerMobile || '--' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-group">
              <span class="label">销售日期:</span>
              <span class="value">{{ formatDate(orderInfo.orderTime, 'YYYY-MM-DD') }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="compliance-context mt-15px">
        <el-row :gutter="10">
          <el-col :span="8">
            <div class="info-group">
              <span class="label">防治对象:</span>
              <span class="value">{{ orderInfo.usageIntent || '--' }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-group">
              <span class="label">建议用量:</span>
              <span class="value">{{ orderInfo.dosageAdvice || '--' }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-group">
              <span class="label">施用方法:</span>
              <span class="value">{{ orderInfo.usageMethod || '--' }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="items-table mt-20px">
        <table class="agri-table">
          <thead>
            <tr>
              <th>产品名称</th>
              <th>规格型号</th>
              <th>生产批次</th>
              <th>登记证号</th>
              <th>数量</th>
              <th>金额</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in orderInfo.items" :key="item.id">
              <td>{{ item.productName }}</td>
              <td>{{ item.productStandard || '--' }}</td>
              <td>{{ item.batchNo }}</td>
              <td>{{ item.registrationNo || '--' }}</td>
              <td>{{ item.count }} {{ item.productUnitName }}</td>
              <td>¥{{ erpPriceInputFormatter(item.totalPrice) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="prescription-footer mt-30px">
        <el-row>
          <el-col :span="12">
            <div class="signature-box">销售员 (签章): {{ orderInfo.saleUserName || '' }}</div>
          </el-col>
          <el-col :span="12">
            <div class="signature-box text-right">购药人确认: ________________</div>
          </el-col>
        </el-row>
        <div class="warning-text mt-15px">
          ※ 农资使用须知: 请务必按照建议用量和施用方法操作。高毒农药严禁用于果蔬及水产养殖。
        </div>
      </div>
    </div>
    
    <template #footer>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button v-print="'#compliancePrintArea'" type="primary">打 印</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { formatDate } from '@/utils/formatTime'
import { erpPriceInputFormatter } from '@/utils'

defineOptions({ name: 'SaleCompliancePrint' })

const dialogVisible = ref(false)
const orderInfo = ref<any>({})

/** 打开弹窗 */
const open = (row: any) => {
  orderInfo.value = { ...row }
  dialogVisible.value = true
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.prescription-container {
  padding: 30px;
  background: #fff;
  color: #333;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
  border: 1px solid #eee;
}

.prescription-header {
  text-align: center;
  border-bottom: 2px solid #333;
  padding-bottom: 15px;
  margin-bottom: 20px;
  
  .title {
    font-size: 24px;
    font-weight: bold;
    letter-spacing: 2px;
  }
  .order-no {
    font-size: 12px;
    color: #666;
    margin-top: 5px;
  }
}

.info-group {
  margin-bottom: 10px;
  font-size: 14px;
  .label {
    font-weight: bold;
    margin-right: 8px;
    color: #555;
  }
  .value {
    border-bottom: 1px solid #ddd;
    padding: 0 5px;
  }
}

.agri-table {
  width: 100%;
  border-collapse: collapse;
  th, td {
    border: 1px solid #333;
    padding: 8px;
    text-align: center;
    font-size: 13px;
  }
  th {
    background: #f5f7fa;
  }
}

.warning-text {
  font-size: 12px;
  color: #F56C6C;
  border: 1px dashed #F56C6C;
  padding: 10px;
  background: #fef0f0;
}

.signature-box {
  font-size: 14px;
  margin-top: 40px;
}

@media print {
  body * {
    visibility: hidden;
  }
  #compliancePrintArea, #compliancePrintArea * {
    visibility: visible;
  }
  #compliancePrintArea {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }
}
</style>
