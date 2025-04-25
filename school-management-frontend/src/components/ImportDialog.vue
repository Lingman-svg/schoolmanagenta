<template>
  <el-dialog 
    :title="title" 
    :model-value="visible" 
    width="400px" 
    append-to-body
    @close="handleClose"
  >
    <el-upload
      ref="uploadRef"
      :limit="1"
      accept=".xlsx, .xls"
      :action="uploadUrl" 
      :headers="uploadHeaders" 
      :disabled="isUploading"
      :on-progress="handleFileUploadProgress"
      :on-success="handleFileSuccess"
      :on-error="handleFileError"
      :auto-upload="false" 
      drag
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        将文件拖到此处，或<em>点击上传</em>
      </div>
      <template #tip>
        <div class="el-upload__tip text-center">
          <div class="el-upload__tip">
             <el-checkbox v-model="updateSupport" />
             是否更新已经存在的用户数据
             <span style="margin-left: 15px;">（可选）</span>
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link v-if="templateUrl" type="primary" :underline="false" style="font-size:12px;vertical-align: baseline; margin-left: 10px;" @click="downloadTemplate">下载模板</el-link>
        </div>
      </template>
    </el-upload>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" :loading="isUploading" @click="submitFileForm">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElDialog, ElUpload, ElIcon, ElLink, ElButton, ElMessage, ElCheckbox } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
// import { getToken } from '@/utils/auth'; // 假设有获取 token 的方法

const props = defineProps({
  title: {
    type: String,
    default: '通用导入'
  },
  visible: {
    type: Boolean,
    default: false
  },
  uploadUrl: { // 上传地址
    type: String,
    required: true
  },
   templateUrl: { // 模板下载地址 (可选)
     type: String,
     default: ''
   }
});

const emit = defineEmits(['update:visible', 'success']);

const uploadRef = ref(null);
const isUploading = ref(false);
const updateSupport = ref(false); // 是否更新已存在数据，可能需要传给后端

// 设置上传请求头，例如携带 Token
const uploadHeaders = ref({
  // Authorization: 'Bearer ' + getToken() 
  // 根据实际情况设置认证头
});

// 文件上传过程处理
const handleFileUploadProgress = (event, file, fileList) => {
  isUploading.value = true;
};

// 文件上传成功处理
const handleFileSuccess = (response, file, fileList) => {
  isUploading.value = false;
  uploadRef.value?.clearFiles(); // 清空已上传文件列表
  
  // 假设后端返回的结构是 { code: 200, msg: '...', data: '...' } 或类似
  if (response.code === 200) {
     // 如果后端在 data 中返回了详细信息
     const message = response.data || response.msg || '导入成功'; 
     ElMessage.success(message);
     emit('update:visible', false); // 关闭弹窗
     emit('success'); // 通知父组件导入成功，可能需要刷新列表
  } else {
     // 将后端返回的错误信息显示出来
     ElMessage.error(response.msg || '导入失败');
  }
};

// 文件上传错误处理
const handleFileError = (error, file, fileList) => {
  isUploading.value = false;
  ElMessage.error('上传失败，请检查文件或网络');
  console.error('Upload error:', error);
};

// 提交上传
const submitFileForm = () => {
   // 手动触发上传
   // 在这里可以组装需要额外传递给后端的参数，例如 updateSupport
   // uploadRef.value?.submit({ updateSupport: updateSupport.value }); // submit 方法可能不支持直接传递额外参数
   // 通常做法是在 action URL 上拼接参数，或者在 before-upload 中修改 data
   // 简单起见，我们假设 updateSupport 不传递，或者后端通过其他方式处理
   
   // 检查是否有文件被选择
   if (!uploadRef.value?.uploadFiles || uploadRef.value.uploadFiles.length === 0) {
       ElMessage.warning('请先选择要上传的文件');
       return;
   }
   uploadRef.value?.submit();
};

// 下载模板
const downloadTemplate = () => {
   if (props.templateUrl) {
     window.open(props.templateUrl, '_blank');
   } else {
     // 如果没有提供模板 URL，可以尝试前端生成或提示用户
     ElMessage.info('暂无可用模板');
   }
};

// 关闭弹窗
const handleClose = () => {
  if (isUploading.value) {
     ElMessage.warning('文件正在上传中，请稍候');
     return;
  }
  uploadRef.value?.clearFiles(); // 清空文件列表
  updateSupport.value = false; // 重置选项
  emit('update:visible', false);
};

</script>

<style scoped>
.text-center {
  text-align: center;
}
</style> 