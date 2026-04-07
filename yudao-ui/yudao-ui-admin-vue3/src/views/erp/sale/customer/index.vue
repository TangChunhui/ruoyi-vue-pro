<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="80px"
    >
      <el-form-item :label="isFarmerMode ? '农户姓名' : '名称'" prop="name">
        <el-input
          v-model="queryParams.name"
          :placeholder="isFarmerMode ? '请输入农户姓名' : '请输入名称'"
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
      <el-form-item v-if="isFarmerMode" label="主要作物" prop="mainCrops">
        <el-input
          v-model="queryParams.mainCrops"
          placeholder="请输入主要作物"
          clearable
          @keyup.enter="handleQuery"
          class="!w-200px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['erp:customer:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> {{ isFarmerMode ? '新增农户' : '新增' }}
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['erp:customer:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <!-- ===== 农户模式列 ===== -->
      <template v-if="isFarmerMode">
        <el-table-column label="农户姓名" align="center" prop="name" min-width="100" />
        <el-table-column label="手机号码" align="center" prop="mobile" width="130" />
        <el-table-column label="联系人" align="center" prop="contact" width="100" />
        <el-table-column label="种植面积(亩)" align="center" prop="landArea" width="120">
          <template #default="{ row }">
            {{ row.landArea != null ? row.landArea : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="主要作物" align="center" prop="mainCrops" min-width="120">
          <template #default="{ row }">{{ row.mainCrops || '-' }}</template>
        </el-table-column>
        <el-table-column label="信用额度" align="center" prop="creditLimit" width="120">
          <template #default="{ row }">
            <span>{{ row.creditLimit != null ? '¥' + row.creditLimit.toFixed(2) : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="当前欠款" align="center" prop="currentDebt" width="120">
          <template #default="{ row }">
            <span :class="row.currentDebt > 0 ? 'text-red-500 font-bold' : ''">
              {{ row.currentDebt != null ? '¥' + row.currentDebt.toFixed(2) : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="欠款预警" align="center" width="100">
          <template #default="{ row }">
            <el-tag
              v-if="row.creditLimit > 0 && row.currentDebt >= row.creditLimit"
              type="danger"
              size="small"
            >超额</el-tag>
            <el-tag
              v-else-if="row.creditLimit > 0 && row.currentDebt >= row.creditLimit * 0.8"
              type="warning"
              size="small"
            >临近</el-tag>
            <el-tag v-else type="success" size="small">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="地址" align="center" prop="address" min-width="150">
          <template #default="{ row }">{{ row.address || '-' }}</template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" min-width="120">
          <template #default="{ row }">{{ row.remark || '-' }}</template>
        </el-table-column>
      </template>

      <!-- ===== 普通客户模式列 ===== -->
      <template v-else>
        <el-table-column label="名称" align="center" prop="name" />
        <el-table-column label="联系人" align="center" prop="contact" />
        <el-table-column label="手机号码" align="center" prop="mobile" />
        <el-table-column label="联系电话" align="center" prop="telephone" />
        <el-table-column label="电子邮箱" align="center" prop="email" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="排序" align="center" prop="sort" />
      </template>

      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" width="130">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['erp:customer:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['erp:customer:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <CustomerForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import download from '@/utils/download'
import { CustomerApi, CustomerVO } from '@/api/erp/sale/customer'
import CustomerForm from './CustomerForm.vue'
import { useRoute } from 'vue-router'

/** ERP 客户 / 农户管理 列表 */
defineOptions({ name: 'ErpCustomer' })

const route = useRoute()
const isFarmerMode = computed(() => route.path.includes('farmer'))

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<CustomerVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  mobile: undefined,
  telephone: undefined,
  mainCrops: undefined
})
const queryFormRef = ref()
const exportLoading = ref(false)

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CustomerApi.getCustomerPage(queryParams)
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
    await CustomerApi.deleteCustomer(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await CustomerApi.exportCustomer(queryParams)
    download.excel(data, isFarmerMode.value ? '农户.xls' : '客户.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
