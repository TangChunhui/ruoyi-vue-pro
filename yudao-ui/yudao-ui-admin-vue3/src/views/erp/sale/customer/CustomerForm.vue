<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <!-- 基础信息 -->
        <el-col :span="12">
          <el-form-item :label="isFarmerMode ? '农户姓名' : '客户名称'" prop="name">
            <el-input v-model="formData.name" :placeholder="isFarmerMode ? '请输入农户姓名' : '请输入客户名称'" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号码" prop="mobile">
            <el-input v-model="formData.mobile" placeholder="请输入手机号码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系人" prop="contact">
            <el-input v-model="formData.contact" placeholder="请输入联系人" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="telephone">
            <el-input v-model="formData.telephone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-col>

        <!-- 农户扩展信息 -->
        <template v-if="isFarmerMode">
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input v-model="formData.address" placeholder="请输入详细地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-divider content-position="left">农业信息</el-divider>
          </el-col>
          <el-col :span="12">
            <el-form-item label="种植面积(亩)" prop="landArea">
              <el-input-number v-model="formData.landArea" :min="0" :precision="2" class="!w-full" placeholder="请输入种植面积" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主要作物" prop="mainCrops">
              <el-input v-model="formData.mainCrops" placeholder="如：水稻、小麦、玉米" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-divider content-position="left">信用信息</el-divider>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用额度(元)" prop="creditLimit">
              <el-input-number v-model="formData.creditLimit" :min="0" :precision="2" class="!w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前欠款(元)">
              <el-input-number v-model="formData.currentDebt" disabled :precision="2" class="!w-full" />
              <div v-if="formData.currentDebt > 0" class="text-red-500 text-12px mt-4px">
                <Icon icon="ep:warning" /> 该农户存在欠款
              </div>
            </el-form-item>
          </el-col>
        </template>

        <!-- 其他信息（普通客户或农户高级信息） -->
        <el-col :span="24">
          <el-divider content-position="left">{{ isFarmerMode ? '其他信息' : '企业信息' }}</el-divider>
        </el-col>
        <template v-if="!isFarmerMode">
          <el-col :span="12">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入电子邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="传真" prop="fax">
              <el-input v-model="formData.fax" placeholder="请输入传真" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纳税人识别号" prop="taxNo">
              <el-input v-model="formData.taxNo" placeholder="请输入纳税人识别号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="税率(%)" prop="taxPercent">
              <el-input-number v-model="formData.taxPercent" :min="0" :precision="2" placeholder="请输入税率" class="!w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户行" prop="bankName">
              <el-input v-model="formData.bankName" placeholder="请输入开户行" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户账号" prop="bankAccount">
              <el-input v-model="formData.bankAccount" placeholder="请输入开户账号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户地址" prop="bankAddress">
              <el-input v-model="formData.bankAddress" placeholder="请输入开户地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="种植面积(亩)" prop="landArea">
              <el-input-number v-model="formData.landArea" :min="0" :precision="2" class="!w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主要作物" prop="mainCrops">
              <el-input v-model="formData.mainCrops" placeholder="请输入主要作物" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用额度" prop="creditLimit">
              <el-input-number v-model="formData.creditLimit" :min="0" :precision="2" class="!w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前欠款">
              <el-input-number v-model="formData.currentDebt" disabled :precision="2" class="!w-full" />
            </el-form-item>
          </el-col>
        </template>
        <el-col :span="12">
          <el-form-item label="开启状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
                :key="dict.value"
                :value="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="!isFarmerMode" :span="12">
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="formData.sort" placeholder="请输入排序" class="!w-full" :precision="0" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input type="textarea" v-model="formData.remark" placeholder="请输入备注" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { CustomerApi, CustomerVO } from '@/api/erp/sale/customer'
import { CommonStatusEnum } from '@/utils/constants'
import { useRoute } from 'vue-router'

/** ERP 客户 / 农户 表单 */
defineOptions({ name: 'CustomerForm' })

const route = useRoute()
const isFarmerMode = computed(() => route.path.includes('farmer'))

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref({
  id: undefined,
  name: undefined,
  contact: undefined,
  mobile: undefined,
  telephone: undefined,
  email: undefined,
  fax: undefined,
  remark: undefined,
  status: undefined,
  sort: undefined,
  taxNo: undefined,
  taxPercent: undefined,
  bankName: undefined,
  bankAccount: undefined,
  bankAddress: undefined,
  address: undefined,
  landArea: undefined,
  mainCrops: undefined,
  creditLimit: undefined,
  currentDebt: undefined
})
const formRules = reactive({
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '开启状态不能为空', trigger: 'blur' }]
})
const formRef = ref()

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await CustomerApi.getCustomer(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

/** 提交表单 */
const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = formData.value as unknown as CustomerVO
    if (formType.value === 'create') {
      await CustomerApi.createCustomer(data)
      message.success(t('common.createSuccess'))
    } else {
      await CustomerApi.updateCustomer(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    contact: undefined,
    mobile: undefined,
    telephone: undefined,
    email: undefined,
    fax: undefined,
    remark: undefined,
    status: CommonStatusEnum.ENABLE,
    sort: 0,
    taxNo: undefined,
    taxPercent: undefined,
    bankName: undefined,
    bankAccount: undefined,
    bankAddress: undefined,
    address: undefined,
    landArea: undefined,
    mainCrops: undefined,
    creditLimit: 0,
    currentDebt: 0
  }
  formRef.value?.resetFields()
}
</script>
