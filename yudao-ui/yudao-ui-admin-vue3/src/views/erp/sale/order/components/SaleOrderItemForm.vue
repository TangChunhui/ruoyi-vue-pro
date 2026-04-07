<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    v-loading="formLoading"
    label-width="0px"
    :inline-message="true"
    :disabled="disabled"
  >
    <!-- 条码扫描快速录入 -->
    <el-row v-if="!disabled" class="mb-10px">
      <el-col :span="14">
        <el-input
          v-model="barcodeValue"
          placeholder="扫描条码快速添加产品（扫描枪按 Enter 自动触发）"
          clearable
          @keyup.enter="handleBarcodeScan"
        >
          <template #prepend>条码扫描</template>
        </el-input>
      </el-col>
    </el-row>
    <el-table :data="formData" show-summary :summary-method="getSummaries" class="-mt-10px">
      <el-table-column label="序号" type="index" align="center" width="60" />
      <el-table-column label="产品名称" min-width="180">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.productId`" :rules="formRules.productId" class="mb-0px!">
            <el-select
              v-model="row.productId"
              clearable
              filterable
              @change="onChangeProduct($event, row)"
              placeholder="请选择产品"
            >
              <el-option
                v-for="item in productList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              >
                <span class="float-left">{{ item.name }}</span>
                <el-tag
                  v-if="item.isRestricted"
                  type="danger"
                  size="small"
                  class="float-right mt-5px"
                >
                  限用
                </el-tag>
              </el-option>
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="库存" min-width="100">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input disabled v-model="row.stockCount" :formatter="erpCountInputFormatter" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="条码" min-width="150">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input disabled v-model="row.productBarCode" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="单位" min-width="80">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input disabled v-model="row.productUnitName" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="生产批次" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.batchNo`" class="mb-0px!">
            <el-select
              v-model="row.batchNo"
              clearable
              filterable
              placeholder="选择批次"
              @change="onChangeBatch($event, row)"
            >
              <el-option
                v-for="batch in batchList[row.productId] || []"
                :key="batch.batchNo"
                :label="batch.batchNo"
                :value="batch.batchNo"
              >
                <span style="float: left">{{ batch.batchNo }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px; margin-left: 10px">
                  {{ dayjs(batch.expiryDate).format('YYYY-MM-DD') }}
                </span>
              </el-option>
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="生产日期" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.productionDate`" class="mb-0px!">
            <el-date-picker
              v-model="row.productionDate"
              type="date"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="选择日期"
              class="!w-100%"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="有效期至" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.expiryDate`" class="mb-0px!">
            <el-date-picker
              v-model="row.expiryDate"
              type="date"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="选择有效期"
              class="!w-100%"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="数量" prop="count" fixed="right" min-width="140">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.count`" :rules="formRules.count" class="mb-0px!">
            <el-input-number
              v-model="row.count"
              controls-position="right"
              :min="0.001"
              :precision="3"
              class="!w-100%"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="产品单价" fixed="right" min-width="120">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.productPrice`" class="mb-0px!">
            <el-input-number
              v-model="row.productPrice"
              controls-position="right"
              :min="0.01"
              :precision="2"
              class="!w-100%"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="金额" prop="totalProductPrice" fixed="right" min-width="100">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.totalProductPrice`" class="mb-0px!">
            <el-input
              disabled
              v-model="row.totalProductPrice"
              :formatter="erpPriceInputFormatter"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="税率（%）" fixed="right" min-width="115">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.taxPercent`" class="mb-0px!">
            <el-input-number
              v-model="row.taxPercent"
              controls-position="right"
              :min="0"
              :precision="2"
              class="!w-100%"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="税额" prop="taxPrice" fixed="right" min-width="120">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.taxPrice`" class="mb-0px!">
            <el-input disabled v-model="row.taxPrice" :formatter="erpPriceInputFormatter" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="税额合计" prop="totalPrice" fixed="right" min-width="100">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.totalPrice`" class="mb-0px!">
            <el-input disabled v-model="row.totalPrice" :formatter="erpPriceInputFormatter" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="备注" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.remark`" class="mb-0px!">
            <el-input v-model="row.remark" placeholder="请输入备注" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="60">
        <template #default="{ $index }">
          <el-button @click="handleDelete($index)" link>—</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form>
  <el-row justify="center" class="mt-3" v-if="!disabled">
    <el-button @click="handleAdd" round>+ 添加销售产品</el-button>
  </el-row>
</template>
<script setup lang="ts">
import { ProductApi, ProductVO } from '@/api/erp/product/product'
import { StockApi } from '@/api/erp/stock/stock'
import {
  erpCountInputFormatter,
  erpPriceInputFormatter,
  erpPriceMultiply,
  getSumValue
} from '@/utils'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  items: undefined
  disabled: false
}>()
const formLoading = ref(false) // 表单的加载中
const formData = ref([])
const formRules = reactive({
  productId: [{ required: true, message: '产品不能为空', trigger: 'blur' }],
  count: [{ required: true, message: '产品数量不能为空', trigger: 'blur' }]
})
const formRef = ref([]) // 表单 Ref
const productList = ref<ProductVO[]>([]) // 产品列表
const batchList = ref<Record<number, any[]>>({}) // 批次列表，Key 为产品编号
const barcodeValue = ref('') // 扫码输入值

/** 初始化设置出库项 */
watch(
  () => props.items,
  async (val) => {
    formData.value = val
  },
  { immediate: true }
)

/** 监听合同产品变化，计算合同产品总价 */
watch(
  () => formData.value,
  (val) => {
    if (!val || val.length === 0) {
      return
    }
    // 循环处理
    val.forEach((item) => {
      item.totalProductPrice = erpPriceMultiply(item.productPrice, item.count)
      item.taxPrice = erpPriceMultiply(item.totalProductPrice, item.taxPercent / 100.0)
      if (item.totalProductPrice != null) {
        item.totalPrice = item.totalProductPrice + (item.taxPrice || 0)
      } else {
        item.totalPrice = undefined
      }
    })
  },
  { deep: true }
)

/** 合计 */
const getSummaries = (param: any) => {
  const { columns, data } = param
  const sums: string[] = []
  columns.forEach((column, index: number) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    if (['count', 'totalProductPrice', 'taxPrice', 'totalPrice'].includes(column.property)) {
      const sum = getSumValue(data.map((item) => Number(item[column.property])))
      sums[index] =
        column.property === 'count' ? erpCountInputFormatter(sum) : erpPriceInputFormatter(sum)
    } else {
      sums[index] = ''
    }
  })

  return sums
}

/** 新增按钮操作 */
const handleAdd = () => {
  const row = {
    id: undefined,
    productId: undefined,
    productUnitName: undefined, // 产品单位
    productBarCode: undefined, // 产品条码
    productPrice: undefined,
    stockCount: undefined,
    count: 1,
    totalProductPrice: undefined,
    taxPercent: undefined,
    taxPrice: undefined,
    totalPrice: undefined,
    batchNo: undefined,
    productionDate: undefined,
    expiryDate: undefined,
    remark: undefined
  }
  formData.value.push(row)
}

/** 删除按钮操作 */
const handleDelete = (index: number) => {
  formData.value.splice(index, 1)
}

/** 处理产品变更 */
const onChangeProduct = (productId, row) => {
  const product = productList.value.find((item) => item.id === productId)
  if (product) {
    row.productUnitName = product.unitName
    row.productBarCode = product.barCode
    row.productPrice = product.salePrice
  }
  // 置空批次
  row.batchNo = undefined
  row.productionDate = undefined
  row.expiryDate = undefined
  // 加载库存
  setStockCount(row)
  // 加载批次
  fetchBatches(productId)
}

/** 异步加载产品的批次列表 */
const fetchBatches = async (productId: number) => {
  if (!productId || batchList.value[productId]) return
  const data = await StockApi.getStockList({ productId })
  batchList.value[productId] = data
}

/** 处理批次变更 */
const onChangeBatch = (batchNo, row) => {
  const batches = batchList.value[row.productId]
  if (!batches) return
  const batch = batches.find((b) => b.batchNo === batchNo)
  if (batch) {
    row.productionDate = batch.productionDate
    row.expiryDate = batch.expiryDate
  }
}

/** 加载库存 */
const setStockCount = async (row: any) => {
  if (!row.productId) {
    return
  }
  const count = await StockApi.getStockCount(row.productId)
  row.stockCount = count || 0
}

/** 判断是否存在高毒限用产品 */
const hasRestrictedItem = computed(() => {
  return formData.value.some((item) => {
    const product = productList.value.find((p) => p.id === item.productId)
    return product?.isRestricted
  })
})

/** 条码扫描处理 */
const handleBarcodeScan = () => {
  const barcode = barcodeValue.value.trim()
  if (!barcode) return
  const product = productList.value.find((p) => p.barCode === barcode)
  if (!product) {
    ElMessage.warning(`未找到条码为 "${barcode}" 的产品`)
    barcodeValue.value = ''
    return
  }
  const existingRow = formData.value.find((row) => row.productId === product.id)
  if (existingRow) {
    existingRow.count = (existingRow.count || 0) + 1
    ElMessage.success(`已增加 ${product.name} 数量`)
  } else {
    const row = {
      id: undefined,
      productId: product.id,
      productUnitName: product.unitName,
      productBarCode: product.barCode,
      productPrice: product.salePrice,
      stockCount: undefined,
      count: 1,
      totalProductPrice: undefined,
      taxPercent: undefined,
      taxPrice: undefined,
      totalPrice: undefined,
      batchNo: undefined,
      productionDate: undefined,
      expiryDate: undefined,
      remark: undefined
    }
    formData.value.push(row)
    setStockCount(row)
    fetchBatches(product.id)
    ElMessage.success(`已添加 ${product.name}`)
  }
  barcodeValue.value = ''
}

/** 表单校验 */
const validate = () => {
  return formRef.value.validate()
}
defineExpose({ validate, hasRestrictedItem })

/** 初始化 */
onMounted(async () => {
  productList.value = await ProductApi.getProductSimpleList()
  // 默认添加一个
  if (formData.value.length === 0) {
    handleAdd()
  }
})
</script>
