<template>
  <el-dialog
    :model-value="visible"
    :title="title"
    width="600px"
    append-to-body
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="班级名称" prop="className">
        <el-input v-model="form.className" placeholder="请输入班级名称" />
      </el-form-item>
      <el-form-item label="年级" prop="grade">
        <el-input v-model="form.grade" placeholder="例如：一年级、初一" />
      </el-form-item>
      <el-form-item label="班主任" prop="teacherId">
        <el-select v-model="form.teacherId" placeholder="请选择班主任" filterable clearable style="width: 100%;">
          <el-option
             v-for="item in teacherList"
             :key="item.id"
             :label="item.teacherName"
             :value="item.id"
           />
        </el-select>
      </el-form-item>
       <el-row :gutter="20">
         <el-col :span="12">
          <el-form-item label="开班日期" prop="startDate">
            <el-date-picker
              v-model="form.startDate"
              type="date"
              placeholder="选择开班日期"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
            />
          </el-form-item>
         </el-col>
         <el-col :span="12">
           <el-form-item label="结业日期" prop="endDate">
             <el-date-picker
               v-model="form.endDate"
               type="date"
               placeholder="选择结业日期"
               value-format="YYYY-MM-DD"
               style="width: 100%;"
             />
           </el-form-item>
          </el-col>
       </el-row>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="0">未开班</el-radio>
          <el-radio :label="1">进行中</el-radio>
          <el-radio :label="2">已结业</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, defineProps, defineEmits } from 'vue';
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElDatePicker, ElRadioGroup, ElRadio, ElRow, ElCol } from 'element-plus';

// --- Props --- 
const props = defineProps({
  visible: { // Controls dialog visibility (v-model)
    type: Boolean,
    required: true,
  },
  title: { // Dialog title
    type: String,
    default: '弹窗',
  },
  formData: { // Data to populate the form (for editing)
    type: Object,
    default: () => ({}),
  },
  teacherList: { // List of teachers for the dropdown
      type: Array,
      default: () => []
  }
});

// --- Emits --- 
const emit = defineEmits(['update:visible', 'save']);

// --- Refs and Reactive Variables ---
const formRef = ref(null);
const loading = ref(false);
const form = reactive({
  id: null,
  className: '',
  grade: '',
  teacherId: null,
  startDate: '',
  endDate: '',
  status: 0, // Default status
  remark: ''
});

// --- Custom Validator ---
const validateEndDate = (rule, value, callback) => {
  if (value && form.startDate && value < form.startDate) {
    callback(new Error('结业日期不能早于开班日期'));
  } else {
    callback(); // Pass validation
  }
};

// --- Validation Rules --- 
const rules = reactive({
  className: [
    { required: true, message: '班级名称不能为空', trigger: 'blur' },
  ],
  grade: [
      { required: true, message: '年级不能为空', trigger: 'blur' },
  ],
  teacherId: [
      { required: true, message: '请选择班主任', trigger: 'change' },
  ],
  startDate: [
      { required: true, message: '请选择开班日期', trigger: 'change' },
  ],
  endDate: [
      // Custom validator for end date
      { validator: validateEndDate, trigger: 'change' }
  ],
  status: [
      { required: true, message: '请选择状态', trigger: 'change' },
  ]
});

// --- Helper function to calculate status ---
const calculateStatus = (startDate, endDate) => {
  if (startDate && endDate) {
    const today = new Date();
    const start = new Date(startDate);
    const end = new Date(endDate);
    // Reset time part for accurate date comparison
    today.setHours(0, 0, 0, 0);
    start.setHours(0, 0, 0, 0);
    end.setHours(0, 0, 0, 0);

    if (today < start) {
      return 0; // 未开班
    } else if (today > end) {
      return 2; // 已结业
    } else {
      return 1; // 进行中
    }
  }
  return null; // Return null if dates are incomplete
};

// --- Methods --- 

// Reset form fields to initial state (Moved before watch)
const resetFormFields = () => {
    form.id = null;
    form.className = '';
    form.grade = '';
    form.teacherId = null;
    form.startDate = '';
    form.endDate = '';
    form.status = 0; // Reset to default
    form.remark = '';
    formRef.value?.clearValidate(); // Clear validation messages
};

// --- Watcher --- 
// Watch for changes in props.formData and update the local reactive form
// This handles both initial population for edit and potentially resetting for add
watch(() => props.formData, (newVal) => {
  // Reset form first to clear old values
  resetFormFields();
  // Populate form with new data if provided (for editing)
  if (newVal && Object.keys(newVal).length > 0) {
    Object.assign(form, newVal);
  }
}, { deep: true, immediate: true });

// Watch for date changes to auto-calculate status
watch([() => form.startDate, () => form.endDate], ([newStart, newEnd], [oldStart, oldEnd]) => {
   // Check if either date actually changed to avoid unnecessary calculations
   if (newStart !== oldStart || newEnd !== oldEnd) {
        const calculatedStatus = calculateStatus(newStart, newEnd);
        if (calculatedStatus !== null) {
            form.status = calculatedStatus;
            // Optionally re-validate status if its rules depend on other fields
            // formRef.value?.validateField('status');
        }
   }
});

// Watch for startDate changes to re-validate endDate
watch(() => form.startDate, (newVal, oldVal) => {
  // Avoid triggering validation unnecessarily during initial setup or reset
  if (newVal !== oldVal && form.endDate) {
      // If startDate changes and endDate has a value, validate endDate
      formRef.value?.validateField('endDate', () => {});
  }
});

// Handle dialog close (via X button or Cancel button)
const handleClose = () => {
  if (loading.value) return; // Prevent closing while submitting
  emit('update:visible', false); // Update v-model:visible
  // No need to explicitly call resetFormFields here due to the watcher
};

// Handle form submission
const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate(); // Validate the form
    loading.value = true;
    emit('save', { ...form }); // Emit save event with a copy of the form data
    // The parent component will handle closing the dialog after successful save
  } catch (error) {
    // Validation failed or error during emit (less likely)
    if (error === false) {
        console.log('Form validation failed');
        ElMessage.warning('请检查表单输入');
    } else {
        console.error("Form submission error:", error);
    }
  } finally {
     // We rely on the parent to close the dialog, but we stop the loading here
     // If the parent handles the save API call, it might call stopLoading externally
     // For simplicity here, we stop loading after emitting.
    // If the save operation in the parent can fail, the parent should manage the loading state
    // via a prop or a method on this component.
     // For now, let's assume the parent closes the dialog, implicitly stopping loading needed.
     // stopLoading(); // Let's manage loading stop here for now.
     // The parent component `ClassManagement.vue`'s handleSave will eventually call stopLoading on success/error.
  }
};

// Method to be called by the parent component to stop the loading state
const stopLoading = () => {
  loading.value = false;
};

defineExpose({ stopLoading }); // Expose stopLoading to the parent

</script>

<style scoped>
/* Add any specific styles for the dialog if needed */
</style> 