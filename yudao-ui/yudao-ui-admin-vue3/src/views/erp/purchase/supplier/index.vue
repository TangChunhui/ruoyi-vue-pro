<template>
  <doc-alert title="【采购】采购订单、入库、退货" url="https://doc.iocoder.cn/erp/purchase/" />

  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-200px"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          placeholder="请输入手机号码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-200px"
        />
      </el-form-item>
      <el-form-item label="资质状态" prop="licenseStatus">
        <el-select v-model="queryParams.licenseStatus" clearable placeholder="全部" class="!w-150px">
          <el-option label="已过期" value="expired" />
          <el-option label="30天内到期" value="expiring" />
          <el-option label="正常" value="normal" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="primary" plain @click="openForm('create')" v-hasPermi="['erp:supplier:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button type="success" plain @click="handleExport" :loading="exportLoading" v-hasPermi="['erp:supplier:export']">
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="filteredList" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="供应商名称" align="center" prop="name" min-width="140" />
      <el-table-column label="联系人" align="center" prop="contact" width="90" />
      <el-table-column label="手机号码" align="center" prop="mobile" width="130" />
      <el-table-column label="经营许可证号" align="center" prop="businessLicenseNo" min-width="160">
        <template #default="{ row }">{{ row.businessLicenseNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="资质有效期" align="center" prop="licenseExpiryDate" width="140">
        <template #default="{ row }">
          <template v-if="row.licenseExpiryDate">
            <el-tag :type="getLicenseTagType(row.licenseExpiryDate)" size="small">
              {{ formatDate(row.licenseExpiryDate, 'YYYY-MM-DD') }}
            </el-tag>
            <div v-if="getLicenseDays(row.licenseExpiryDate) < 30" class="text-11px mt-2px" :class="getLicenseDays(row.licenseExpiryDate) < 0 ? 'text-red-500' : 'text-orange-500'">
              {{ getLicenseDays(row.licenseExpiryDate) < 0 ? '已过期' : `${getLicenseDays(row.licenseExpiryDate)}天后到期` }}
            </div>
          </template>
          <span v-else class="text-gray-400">未填写</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="100">
        <template #default="{ row }">{{ row.remark || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="130" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['erp:supplier:update']">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['erp:supplier:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <SupplierForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { SupplierApi, SupplierVO } from '@/api/erp/purchase/supplier'
import SupplierForm from './SupplierForm.vue'
import dayjs from 'dayjs'

defineOptions({ name: 'ErpSupplier' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<SupplierVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  mobile: undefined,
  telephone: undefined,
  licenseStatus: undefined
})
const queryFormRef = ref()
const exportLoading = ref(false)

/** 前端过滤资质状态 */
const filteredList = computed(() => {
  if (!queryParams.licenseStatus) return list.value
  return list.value.filter((row) => {
    const days = getLicenseDays(row.licenseExpiryDate)
    if (queryParams.licenseStatus === 'expired') return days < 0
    if (queryParams.licenseStatus === 'expiring') return days >= 0 && days < 30
    if (queryParams.licenseStatus === 'normal') return !row.licenseExpiryDate || days >= 30
    return true
  })
})

const getLicenseDays = (date: any) => {
  if (!date) return 9999
  return dayjs(date).diff(dayjs(), 'day')
}

const getLicenseTagType = (date: any) => {
  const days = getLicenseDays(date)
  if (days < 0) return 'danger'
  if (days < 30) return 'warning'
  return 'success'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await SupplierApi.getSupplierPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await SupplierApi.deleteSupplier(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await SupplierApi.exportSupplier(queryParams)
    download.excel(data, '供应商.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
