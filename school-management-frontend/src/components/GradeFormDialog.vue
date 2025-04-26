<template>
  <el-dialog
    :model-value="visible"
    :title="title"
    width="650px"
    :close-on-click-modal="false"
    append-to-body
    @update:model-value="emit('update:visible', $event)"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="localFormData" :rules="formRules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="学生" prop="studentId">
            <el-select
              v-model="localFormData.studentId"
              placeholder="请选择学生"
              filterable
              clearable
              :disabled="isEditMode"
              style="width: 100%;"
            >
              <el-option
                v-for="item in studentList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="科目" prop="subjectId">
            <el-select
              v-model="localFormData.subjectId"
              placeholder="请选择科目"
              filterable
              clearable
              :disabled="isEditMode"
              style="width: 100%;"
            >
              <el-option
                v-for="item in subjectList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
           <el-form-item label="班级" prop="clazzId">
            <el-select
              v-model="localFormData.clazzId"
              placeholder="请选择班级 (考试时所在班级)"
              filterable
              clearable
               :disabled="isEditMode"
              style="width: 100%;"
            >
              <el-option
                v-for="item in clazzList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
         <el-col :span="12">
           <el-form-item label="考试名称" prop="examName">
            <el-input
              v-model="localFormData.examName"
              placeholder="请输入考试名称"
              clearable
               :disabled="isEditMode"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
         <el-col :span="12">
           <el-form-item label="分数" prop="score">
             <el-input-number
               v-model="localFormData.score"
               :precision="2"
               :step="0.1"
               :min="0"
               :max="100"
               placeholder="请输入分数"
               controls-position="right"
               style="width: 100%;"
             />
           </el-form-item>
         </el-col>
         <el-col :span="12">
           <el-form-item label="考试时间" prop="examTime">
             <el-date-picker
               v-model="localFormData.examTime"
               type="datetime"
               placeholder="选择考试时间"
               style="width: 100%;"
             />
           </el-form-item>
         </el-col>
      </el-row>

       <el-row :gutter="20">
         <el-col :span="12">
           <el-form-item label="任课老师" prop="teacherId">
             <el-select
               v-model="localFormData.teacherId"
               placeholder="选择任课老师 (可选)"
               filterable
               clearable
               style="width: 100%;"
             >
               <el-option
                 v-for="item in teacherList"
                 :key="item.value"
                 :label="item.label"
                 :value="item.value"
               />
             </el-select>
           </el-form-item>
         </el-col>
         <el-col :span="12">
           <el-form-item label="录入时间" prop="recordTime">
             <el-date-picker
               v-model="localFormData.recordTime"
               type="datetime"
               placeholder="选择录入时间 (默认当前)"
               style="width: 100%;"
             />
           </el-form-item>
         </el-col>
      </el-row>

      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="localFormData.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
        />
      </el-form-item>

    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, reactive, computed } from 'vue';
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElDatePicker, ElInputNumber, ElRow, ElCol } from 'element-plus';

const props = defineProps({
  visible: { // .sync
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  formData: { // Initial form data for edit
    type: Object,
    default: () => ({})
  },
  studentList: {
    type: Array,
    default: () => []
  },
  subjectList: {
    type: Array,
    default: () => []
  },
  clazzList: {
    type: Array,
    default: () => []
  },
  teacherList: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:visible', 'save']);

const formRef = ref(null);
const loading = ref(false);
const localFormData = ref({});

// Determine if it's edit mode based on formData having an ID
const isEditMode = computed(() => !!localFormData.value?.id);

// Validation rules
const formRules = reactive({
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  clazzId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  examName: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  score: [
      { required: true, message: '请输入分数', trigger: 'blur' },
      { type: 'number', message: '分数必须为数字', trigger: 'blur' },
      // { min: 0, max: 100, message: '分数必须在 0 到 100 之间', trigger: 'blur' } // InputNumber handles this
  ],
  // examTime, recordTime, teacherId, remark are optional based on backend logic
});

// Watch for prop changes to update local form data
watch(() => props.formData, (newData) => {
  // Deep copy to avoid modifying the prop directly
  localFormData.value = JSON.parse(JSON.stringify(newData || {}));
  // Ensure score is a number for InputNumber
  if (localFormData.value.score !== null && localFormData.value.score !== undefined) {
      localFormData.value.score = Number(localFormData.value.score);
  }
}, { immediate: true, deep: true });

// Watch for visibility change to reset validation
watch(() => props.visible, (isVisible) => {
  if (!isVisible) {
    formRef.value?.clearValidate(); // Clear validation on close
  }
});

// Watch for studentId change to auto-fill classId in Add mode
watch(() => localFormData.value.studentId, (newStudentId, oldStudentId) => {
  // Only trigger in Add mode (!isEditMode) and when studentId is valid and changes
  if (!isEditMode.value && newStudentId && newStudentId !== oldStudentId) {
    const selectedStudent = props.studentList.find(s => s.value === newStudentId);
    if (selectedStudent && selectedStudent.currentClazzId) {
      localFormData.value.clazzId = selectedStudent.currentClazzId;
      // Optionally clear validation for clazzId as it's auto-filled
      formRef.value?.clearValidate('clazzId');
    } else {
      // Clear clazzId if student not found or has no current class
      localFormData.value.clazzId = null;
    }
  }
  // Also clear clazzId if student is deselected in Add mode
  else if (!isEditMode.value && !newStudentId && oldStudentId) {
    localFormData.value.clazzId = null;
  }
});

const handleClose = () => {
  emit('update:visible', false);
};

const handleSubmit = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    loading.value = true;
    // Important: Emit a deep copy of the data
    emit('save', JSON.parse(JSON.stringify(localFormData.value)));
    // Loading state should be handled by the parent component after the API call
    // setLoading(false); // Parent should do this
  } catch (error) {
    console.error('Form validation/submit error:', error);
    ElMessage.error('表单校验失败');
    loading.value = false; // Stop loading on error
  }
};

// Method to be called by parent to stop loading indicator
const setLoading = (status) => {
  loading.value = status;
};

// Expose necessary refs and methods to parent
defineExpose({
  formRef, // Allow parent to reset validation if needed
  setLoading
});

</script>

<style scoped>
/* Add any specific styles for the dialog here */
</style>
