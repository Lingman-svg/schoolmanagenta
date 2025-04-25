<template>
  <el-dialog
    :model-value="visible" 
    :title="title"
    width="60%" 
    @close="handleClose"
    :close-on-click-modal="false" 
  >
    <el-form
      ref="formRef"
      :model="localFormData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item label="教师姓名" prop="teacherName">
            <el-input v-model="localFormData.teacherName" placeholder="请输入教师姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="localFormData.idCard" placeholder="请输入身份证号" @change="handleIdCardChange"/>
             <span class="form-tip">输入身份证后将自动计算生日、年龄、性别</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
         <el-col :span="12">
          <el-form-item label="性别" prop="gender">
             <el-select v-model="localFormData.gender" placeholder="请选择或自动计算" :disabled="isGenderDisabled">
               <el-option label="男" value="男" />
               <el-option label="女" value="女" />
               <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出生日期" prop="birthDate">
            <el-date-picker
              v-model="localFormData.birthDate"
              type="date"
              placeholder="请选择或自动计算"
              value-format="YYYY-MM-DD" 
              :disabled="isBirthDateDisabled"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
       
      </el-row>
      <el-row>
         <el-col :span="12">
          <el-form-item label="年龄">
            <el-input :value="calculatedAge" placeholder="根据出生日期自动计算" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系方式" prop="phone">
            <el-input v-model="localFormData.phone" placeholder="请输入联系方式" />
          </el-form-item>
        </el-col>
      </el-row>
        <el-row>
        <el-col :span="12">
          <el-form-item label="户籍" prop="nativePlace">
            <el-input v-model="localFormData.nativePlace" placeholder="请输入户籍" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="民族" prop="nation">
            <el-input v-model="localFormData.nation" placeholder="请输入民族" />
          </el-form-item>
        </el-col>
      </el-row>
       <el-row>
         <el-col :span="24">
          <el-form-item label="当前住址" prop="address">
            <el-input type="textarea" v-model="localFormData.address" placeholder="请输入当前住址" />
          </el-form-item>
        </el-col>
       </el-row>
       <el-row>
         <el-col :span="24">
           <el-form-item label="所教科目" prop="subjectIds">
             <el-select 
                v-model="localFormData.subjectIds" 
                multiple 
                placeholder="请选择所教科目" 
                style="width: 100%"
                filterable
              >
               <el-option
                 v-for="item in subjectList"
                 :key="item.id"
                 :label="item.subjectName" 
                 :value="item.id"
               />
             </el-select>
           </el-form-item>
         </el-col>
       </el-row>
       <!-- 可以添加 Remark, IsValid 等字段 -->
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="formLoading">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed, nextTick } from 'vue';
import { ElDialog, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElRow, ElCol, ElDatePicker, ElMessage } from 'element-plus';
import dayjs from 'dayjs'; // 用于日期计算
import { validateIDCard, getBirthDateFromIdCard, getGenderFromIdCard } from '@/utils/validate'; // 假设有身份证校验和信息提取工具

const props = defineProps({
  visible: { // 控制弹窗显示/隐藏
    type: Boolean,
    default: false
  },
  title: { // 弹窗标题
    type: String,
    default: ''
  },
  formData: { // 表单数据
    type: Object,
    default: () => ({})
  },
  subjectList: { // 可选的科目列表
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:visible', 'save']);

const formRef = ref(null);
const formLoading = ref(false);
const isBirthDateDisabled = ref(false); // 控制出生日期是否可编辑
const isGenderDisabled = ref(false); // 控制性别是否可编辑

// 表单数据副本，避免直接修改 prop
const localFormData = reactive({});

// --- 先定义需要被 watch 回调调用的函数 ---

// 处理身份证输入变化
const handleIdCardChange = (value) => {
  if (validateIDCard(value)) {
    localFormData.birthDate = getBirthDateFromIdCard(value); // 格式 YYYY-MM-DD
    localFormData.gender = getGenderFromIdCard(value); // '男' 或 '女'
    isBirthDateDisabled.value = true;
    isGenderDisabled.value = true;
  } else {
     // 身份证无效时，允许手动编辑生日和性别
     isBirthDateDisabled.value = false;
     isGenderDisabled.value = false;
  }
   // 强制更新视图
   nextTick(() => {});
};

// --- 然后定义 watch --- 

// 监听 prop formData 变化，更新本地副本
watch(() => props.formData, (newData) => {
  // 深拷贝或根据需要赋值
   Object.assign(localFormData, newData); 
   // 重置时可能需要清空未定义的字段
   if (!newData.id) { // 如果是新增，确保字段存在或为初始值
       localFormData.teacherName = newData.teacherName || '';
       localFormData.idCard = newData.idCard || '';
       localFormData.birthDate = newData.birthDate || null;
       localFormData.gender = newData.gender || '';
       localFormData.phone = newData.phone || '';
       localFormData.nativePlace = newData.nativePlace || '';
       localFormData.nation = newData.nation || '';
       localFormData.address = newData.address || '';
       localFormData.subjectIds = newData.subjectIds || [];
   } 
   // 触发一次身份证校验逻辑 (现在函数已定义)
   handleIdCardChange(localFormData.idCard);
    // 清除之前的校验状态
   nextTick(() => {
     formRef.value?.clearValidate();
   });
}, { immediate: true, deep: true }); 


// --- 其他定义 (formRules, calculatedAge, handleClose, handleSubmit, stopLoading) --- 

// 表单校验规则
const formRules = reactive({
  teacherName: [
    { required: true, message: '请输入教师姓名', trigger: 'blur' }
  ],
  idCard: [
    // 允许为空，但输入了就要校验格式
    { validator: (rule, value, callback) => {
        if (value && !validateIDCard(value)) {
          callback(new Error('身份证号码格式不正确'));
        } else {
          callback();
        }
      }, trigger: 'blur' 
    }
  ],
   phone: [
    // 简单校验，可根据需要加强
     { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式不正确', trigger: 'blur' } 
   ],
   subjectIds: [
      { type: 'array', message: '请选择所教科目', trigger: 'change' } // 可选校验，根据业务决定是否必填
   ]
  // 其他字段校验...
});

// 计算年龄
const calculatedAge = computed(() => {
  if (localFormData.birthDate) {
    try {
      return dayjs().diff(dayjs(localFormData.birthDate), 'year');
    } catch {
      return ''; // 移除未使用的 'e'
    }
  }
  return '';
});

// 处理弹窗关闭
const handleClose = () => {
  emit('update:visible', false);
   // 关闭时重置表单校验状态可能更好，或者在打开时清除
   // formRef.value?.resetFields(); // 这会重置数据，可能不是期望的
  // formRef.value?.clearValidate();
};

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    formLoading.value = true;
    // 准备提交的数据
    const dataToSubmit = { ...localFormData };
    // 可以在这里移除计算字段（如 age），如果后端不需要
    // delete dataToSubmit.age;
    emit('save', dataToSubmit);
  } catch (error) {
    // 校验失败
    console.log('Form validation failed:', error);
     ElMessage.warning('请检查表单填写是否正确');
  } finally {
     // 不管成功失败，emit 后由父组件控制 loading 和关闭
     // formLoading.value = false; 
  }
};

// 暴露方法
const stopLoading = () => {
  formLoading.value = false;
};

defineExpose({ stopLoading });

</script>

<style scoped>
.form-tip {
  color: #909399;
  font-size: 12px;
  margin-left: 10px;
}
</style> 