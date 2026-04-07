<template>

  <ContentWrap>
    <!-- 搜索工作栏 -->
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
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-tree-select
          v-model="queryParams.categoryId"
          :data="categoryList"
          :props="defaultProps"
          check-strictly
          default-expand-all
          placeholder="请输入分类"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="农资类型" prop="agriType">
        <el-select v-model="queryParams.agriType" clearable placeholder="全部类型" class="!w-130px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.ERP_AGRI_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="高毒限用" prop="isRestricted">
        <el-select v-model="queryParams.isRestricted" clearable placeholder="请选择" class="!w-130px">
          <el-option label="是" :value="1" />
          <el-option label="否" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['erp:product:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['erp:product:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="条码" align="center" prop="barCode" />
      <el-table-column label="名称" align="center" prop="name" min-width="150" />
      <el-table-column label="农资类型" align="center" prop="agriType" width="90">
        <template #default="{ row }">
          <DictTag v-if="row.agriType" :type="DICT_TYPE.ERP_AGRI_TYPE" :value="row.agriType" size="small" />
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="登记证号" align="center" prop="registrationNo" width="130">
        <template #default="{ row }">{{ row.registrationNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="登记证有效期" align="center" prop="registrationExpiryDate" width="150">
        <template #default="{ row }">
          <template v-if="row.registrationExpiryDate">
            <el-tag :type="getRegistrationTagType(row.registrationExpiryDate)" size="small">
              {{ formatDate(row.registrationExpiryDate, 'YYYY-MM-DD') }}
            </el-tag>
            <div v-if="getRegistrationDays(row.registrationExpiryDate) < 30" class="text-11px mt-2px"
              :class="getRegistrationDays(row.registrationExpiryDate) < 0 ? 'text-red-500' : 'text-orange-500'">
              {{ getRegistrationDays(row.registrationExpiryDate) < 0 ? '已过期' : `${getRegistrationDays(row.registrationExpiryDate)}天后到期` }}
            </div>
          </template>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="高毒限用" align="center" prop="isRestricted" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.isRestricted ? 'danger' : 'success'" size="small">
            {{ scope.row.isRestricted ? '高毒' : '普通' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="规格" align="center" prop="standard" />
      <el-table-column label="分类" align="center" prop="categoryName" />
      <el-table-column label="单位" align="center" prop="unitName" />
      <el-table-column
        label="采购价格"
        align="center"
        prop="purchasePrice"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        label="销售价格"
        align="center"
        prop="salePrice"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        label="最低价格"
        align="center"
        prop="minPrice"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="170">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="110">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['erp:product:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['erp:product:delete']"
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
  <ProductForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import dayjs from 'dayjs'
import { ProductApi, ProductVO } from '@/api/erp/product/product'
import { ProductCategoryApi, ProductCategoryVO } from '@/api/erp/product/category'
import ProductForm from './ProductForm.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { defaultProps, handleTree } from '@/utils/tree'
import { erpPriceTableColumnFormatter } from '@/utils'

/** ERP 产品列表 */
defineOptions({ name: 'ErpProduct' })

const getRegistrationDays = (date: any) => dayjs(date).diff(dayjs(), 'day')
const getRegistrationTagType = (date: any) => {
  const days = getRegistrationDays(date)
  if (days < 0) return 'danger'
  if (days < 30) return 'warning'
  return 'success'
}

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<ProductVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  categoryId: undefined,
  isRestricted: undefined,
  agriType: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const categoryList = ref<ProductCategoryVO[]>([]) // 产品分类列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProductApi.getProductPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ProductApi.deleteProduct(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ProductApi.exportProduct(queryParams)
    download.excel(data, '产品.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 产品分类
  const categoryData = await ProductCategoryApi.getProductCategorySimpleList()
  categoryList.value = handleTree(categoryData, 'id', 'parentId')
})
</script>
