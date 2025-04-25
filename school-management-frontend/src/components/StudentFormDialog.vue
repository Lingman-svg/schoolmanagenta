<template>
  <el-dialog
    :title="title"
    :model-value="visible"
    width="600px"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="internalFormData"
      :rules="formRules"
      label-width="100px"
      style="padding-right: 30px;"
      v-loading="loading"
    >
      <el-form-item label="学生姓名" prop="studentName">
        <el-input v-model="internalFormData.studentName" placeholder="请输入学生姓名" />
      </el-form-item>
      <el-form-item label="学号" prop="studentNumber">
        <el-input v-model="internalFormData.studentNumber" placeholder="请输入学号" />
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="internalFormData.gender" placeholder="请选择性别" style="width: 100%;">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
           <el-form-item label="年龄" prop="age">
            <el-input-number v-model="internalFormData.age" :min="1" :max="100" controls-position="right" placeholder="请输入年龄" style="width: 100%;"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="身份证号" prop="idCard">
        <el-input v-model="internalFormData.idCard" placeholder="请输入身份证号" />
      </el-form-item>
       <el-form-item label="出生日期" prop="birthDate">
        <el-date-picker
          v-model="internalFormData.birthDate"
          type="date"
          placeholder="选择出生日期"
          value-format="YYYY-MM-DD"
          style="width: 100%;"
        />
      </el-form-item>
      <el-form-item label="联系方式" prop="phone">
        <el-input v-model="internalFormData.phone" placeholder="请输入联系方式" />
      </el-form-item>
      <el-form-item label="所在班级" prop="currentClazzId">
        <el-select v-model="internalFormData.currentClazzId" placeholder="请选择班级" filterable style="width: 100%;">
           <el-option
              v-for="item in clazzList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
           />
        </el-select>
      </el-form-item>
       <el-form-item label="家庭住址" prop="address">
        <el-input v-model="internalFormData.address" type="textarea" placeholder="请输入家庭住址" />
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item label="户籍" prop="nativePlace">
            <el-input v-model="internalFormData.nativePlace" placeholder="请输入户籍" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="民族" prop="nation">
            <el-input v-model="internalFormData.nation" placeholder="请输入民族" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="handleSubmit">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, defineProps, defineEmits } from 'vue';
import { ElMessage } from 'element-plus';
import { cloneDeep } from 'lodash-es'; // For deep cloning form data

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '弹窗标题'
  },
  formData: {
    type: Object,
    default: () => ({})
  },
  clazzList: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:visible', 'save']);

const formRef = ref(null);
const loading = ref(false); // Loading state for the form
const internalFormData = ref({}); // Internal copy to avoid direct prop mutation

// Helper function for ID card validation and info extraction
const IdCardUtil = {
  isValid: (idCard) => {
    if (!idCard) return false;
    const reg = /^\d{17}(\d|X)$/i;
    return reg.test(idCard);
  },
  getBirthDate: (idCard) => {
    if (!IdCardUtil.isValid(idCard)) return null;
    const year = idCard.substring(6, 10);
    const month = idCard.substring(10, 12);
    const day = idCard.substring(12, 14);
    // Basic date validity check
    const dateStr = `${year}-${month}-${day}`;
    const date = new Date(dateStr);
    // Check if the components match after Date object creation to handle invalid dates like 2023-02-30
    if (date.getFullYear() !== parseInt(year) || 
        (date.getMonth() + 1) !== parseInt(month) || 
        date.getDate() !== parseInt(day)) {
      return null; 
    }
    return dateStr; // Return as YYYY-MM-DD string
  },
  getGender: (idCard) => {
    if (!IdCardUtil.isValid(idCard)) return null;
    const genderCode = parseInt(idCard.substring(16, 17));
    return genderCode % 2 !== 0 ? '男' : '女';
  },
  getAge: (birthDateStr) => {
     if (!birthDateStr) return null;
     try {
        const birthDate = new Date(birthDateStr);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age >= 0 ? age : null; // Return null for invalid dates/ages
     } catch (e) {
         return null;
     }
  }
};

// --- 表单验证规则 ---
const formRules = reactive({
  studentName: [
    { required: true, message: '请输入学生姓名', trigger: 'blur', whitespace: true },
    { max: 100, message: '姓名长度不能超过100个字符', trigger: 'blur' }
  ],
  studentNumber: [
    { required: true, message: '请输入学号', trigger: 'blur', whitespace: true },
    { max: 50, message: '学号长度不能超过50个字符', trigger: 'blur' }
    // { pattern: /^[A-Za-z0-9]+$/, message: '学号格式不正确', trigger: 'blur' } // Example pattern
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { type: 'number', message: '年龄必须为数字值'}
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur', whitespace: true },
    { pattern: /^\d{17}(\d|X)$/i, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入联系方式', trigger: 'blur', whitespace: true },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  currentClazzId: [
    { required: true, message: '请选择所在班级', trigger: 'change' }
  ],
  address: [
    { max: 255, message: '家庭住址长度不能超过255个字符', trigger: 'blur' }
  ],
  nativePlace: [
     { max: 100, message: '户籍长度不能超过100个字符', trigger: 'blur' }
  ],
  nation: [
    { max: 50, message: '民族长度不能超过50个字符', trigger: 'blur' }
  ]
});

// --- 监听器，当外部 formData 变化时更新内部副本 ---
watch(() => props.formData, (newVal) => {
  internalFormData.value = cloneDeep(newVal || {});
  // Don't auto-fill based on prop change, only on user input in the dialog
  formRef.value?.clearValidate(); 
}, { deep: true, immediate: true });

// --- Watcher for ID Card input ---
watch(() => internalFormData.value.idCard, (newIdCard) => {
  if (IdCardUtil.isValid(newIdCard)) {
    const birthDate = IdCardUtil.getBirthDate(newIdCard);
    if (birthDate) {
        internalFormData.value.birthDate = birthDate;
        internalFormData.value.age = IdCardUtil.getAge(birthDate);
        // Optionally clear validation for birthDate and age if auto-filled?
        // formRef.value?.clearValidate(['birthDate', 'age']);
    } else {
        // Handle case where regex is valid but date components aren't (e.g., invalid month/day)
        internalFormData.value.birthDate = null;
        internalFormData.value.age = null;
    }
    internalFormData.value.gender = IdCardUtil.getGender(newIdCard);
     // Optionally clear validation for gender?
     // formRef.value?.clearValidate(['gender']);
  } else {
    // If ID card becomes invalid or empty, clear auto-filled fields
    // Check if the change was likely user clearing the field vs initial load
    if (newIdCard !== props.formData?.idCard) { // Avoid clearing on initial load if prop had invalid id
         internalFormData.value.birthDate = null;
         internalFormData.value.age = null;
         internalFormData.value.gender = null; 
    }
  }
}, { deep: true }); // Watch nested property change

// --- 事件处理 ---
const handleClose = () => {
  formRef.value?.resetFields(); // Reset form fields and validation
  emit('update:visible', false);
};

const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      loading.value = true; // Show loading state
      // Emit the save event with the internal form data
      // The parent component will handle the API call and closing the dialog on success/failure
      emit('save', cloneDeep(internalFormData.value));
      // We don't hide loading or close dialog here; parent controls it
    } else {
      ElMessage.warning('请检查表单填写是否正确');
    }
  });
};

// Method for parent component to manually trigger loading state if needed (e.g., during API call)
const setLoading = (isLoading) => {
  loading.value = isLoading;
};

// Expose methods to parent if needed (e.g., for setting loading state externally)
defineExpose({ setLoading, formRef });

</script>

<style scoped>
/* Add any specific styles for the dialog form here */
</style> 