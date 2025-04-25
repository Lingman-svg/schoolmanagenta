<template>
  <div class="subject-management-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryFormRef">
        <el-form-item label="科目代码" prop="subjectCode">
          <el-input v-model="queryParams.subjectCode" placeholder="请输入科目代码" clearable />
        </el-form-item>
        <el-form-item label="科目名称" prop="subjectName">
          <el-input v-model="queryParams.subjectName" placeholder="请输入科目名称" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="isValid">
           <el-select v-model="queryParams.isValid" placeholder="请选择状态" clearable>
            <el-option label="有效" :value="1" />
            <el-option label="无效" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" :icon="Search">搜索</el-button>
          <el-button @click="resetQuery" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="operate-card" shadow="never">
       <el-row :gutter="10">
         <el-col :span="1.5">
           <el-button type="primary" plain @click="handleAdd" :icon="Plus">新增</el-button>
         </el-col>
         <el-col :span="1.5">
           <el-button type="danger" plain @click="handleBatchDelete" :disabled="selectedRows.length === 0" :icon="Delete">批量删除</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button type="info" plain @click="handleImport" :icon="Upload">导入</el-button>
         </el-col>
         <el-col :span="1.5">
           <el-button type="warning" plain @click="handleExport" :icon="Download">导出</el-button>
         </el-col>
         <!-- 可以添加其他按钮 -->
       </el-row>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
      <vxe-table
        ref="xTableRef"
        :data="tableData"
        :loading="loading"
        border
        stripe
        :row-config="{ isHover: true }"
        :checkbox-config="{ checkField: 'checked', trigger: 'row', highlight: true }"
        @checkbox-change="handleSelectionChange"
        @checkbox-all="handleSelectionChange"
        class="subject-table"
      >
        <vxe-column type="checkbox" width="60"></vxe-column>
        <vxe-column field="id" title="ID" width="80" sortable></vxe-column>
        <vxe-column field="subjectCode" title="科目代码" min-width="150" sortable></vxe-column>
        <vxe-column field="subjectName" title="科目名称" min-width="200" sortable></vxe-column>
        <vxe-column field="isValid" title="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isValid === 1 ? 'success' : 'danger'">
              {{ row.isValid === 1 ? '有效' : '无效' }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column field="remark" title="备注" min-width="250" show-overflow-tooltip></vxe-column>
        <vxe-column field="createTime" title="创建时间" width="180" sortable></vxe-column>
        <vxe-column title="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleUpdate(row)" :icon="Edit">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
          </template>
        </vxe-column>
      </vxe-table>

      <!-- 分页区域 -->
      <vxe-pager
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
        @page-change="handlePageChange"
        class="table-pager"
      >
      </vxe-pager>
    </el-card>

    <!-- 新增/修改对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      append-to-body
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form :model="dialog.formData" :rules="rules" ref="subjectFormRef" label-width="80px">
        <el-form-item label="科目代码" prop="subjectCode">
          <el-input v-model="dialog.formData.subjectCode" placeholder="请输入科目代码" />
        </el-form-item>
        <el-form-item label="科目名称" prop="subjectName">
          <el-input v-model="dialog.formData.subjectName" placeholder="请输入科目名称" />
        </el-form-item>
        <el-form-item label="状态" prop="isValid">
          <el-radio-group v-model="dialog.formData.isValid">
            <el-radio :label="1">有效</el-radio>
            <el-radio :label="0">无效</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dialog.formData.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

     <!-- 导入对话框 (占位) -->
    <el-dialog title="科目导入" v-model="importDialog.visible" width="400px" append-to-body>
        <el-upload
          ref="uploadRef"
          :limit="1"
          accept=".xlsx, .xls"
          :auto-upload="false"
          :on-exceed="handleExceed"
          :on-change="handleFileChange"
          drag
        >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
                <div class="el-upload__tip text-center">
                    <span>仅允许导入xls、xlsx格式文件。</span>
                    <!-- <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;">下载模板</el-link> -->
                </div>
            </template>
        </el-upload>
        <template #footer>
            <el-button @click="importDialog.visible = false">取消</el-button>
            <el-button type="primary" @click="submitImport" :loading="importDialog.loading">确定</el-button>
        </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, Upload, Download, UploadFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'; // 确保已导入 request
import { listSubjects, addSubject, updateSubject, deleteSubject, deleteSubjects, /* exportSubjects, */ importSubjects } from '@/api/subject' // 移除未使用的 exportSubjects

// --- refs ---
const queryFormRef = ref(null) // 查询表单引用
const subjectFormRef = ref(null) // 新增/修改表单引用
const xTableRef = ref(null)    // VxeTable 引用
const uploadRef = ref(null) // 上传组件引用

// --- state ---
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([]) // 存储选中的行数据

// 查询参数
const queryParams = reactive({
  subjectCode: '',
  subjectName: '',
  isValid: null,
  // 分页参数将在 pagination 中管理
})

// 分页配置
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 对话框初始表单数据结构
const initialFormData = () => ({
    id: null,
    subjectCode: '',
    subjectName: '',
    isValid: 1,
    remark: ''
});

// 对话框状态 (新增/修改)
const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false,
  formData: initialFormData() // 初始化 formData
})

// 表单校验规则
const rules = reactive({
  subjectCode: [
    { required: true, message: '科目代码不能为空', trigger: 'blur' }
  ],
  subjectName: [
    { required: true, message: '科目名称不能为空', trigger: 'blur' }
  ]
})

// 导入对话框状态
const importDialog = reactive({
    visible: false,
    loading: false,
    uploadFile: null // 存储待上传的文件
})


// --- methods ---

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams, pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    const response = await listSubjects(params) // response = { code, msg, data: { records, total, ... } }
    // 从 response.data 中获取记录和总数
    tableData.value = response.data?.records || [] 
    pagination.total = response.data?.total || 0 
  } catch (error) {
    console.error("Failed to fetch subject list:", error)
  } finally {
    loading.value = false
  }
}

// 搜索按钮点击
const handleQuery = () => {
  pagination.pageNum = 1 // 搜索时重置到第一页
  getList()
}

// 重置按钮点击
const resetQuery = () => {
  queryFormRef.value?.resetFields() // 重置表单
  handleQuery() // 重新查询
}

// 分页变化处理
const handlePageChange = ({ currentPage, pageSize }) => {
  pagination.pageNum = currentPage
  pagination.pageSize = pageSize
  getList()
}

// 处理表格选中行变化
const handleSelectionChange = ({ records }) => {
   selectedRows.value = records
}

// 重置表单和清除校验
const resetForm = () => {
    dialog.formData = initialFormData(); // 使用函数重置
    // 使用 nextTick 确保 DOM 更新后再清除校验
    // 或者在 @close 事件中处理
    // 使用 $nextTick 可能会更保险，但通常 resetFields 可以工作
    // import { nextTick } from 'vue'
    // nextTick(() => { subjectFormRef.value?.resetFields(); });
     subjectFormRef.value?.resetFields();
     // subjectFormRef.value?.clearValidate(); // 通常 resetFields 会包含清除，如果不行再加
}

// 新增按钮点击
const handleAdd = () => {
  resetForm() // 重置表单
  dialog.isEdit = false
  dialog.title = '新增科目'
  dialog.visible = true
  // 清除可能存在的校验状态 (保险起见)
  subjectFormRef.value?.clearValidate();
}

// 修改按钮点击
const handleUpdate = (row) => {
  resetForm() // 先重置确保干净
  // dialog.formData = { ...row } // 浅拷贝可能导致问题，如果 row 是 proxy
  Object.assign(dialog.formData, row); // 更安全的填充方式
  dialog.isEdit = true
  dialog.title = '修改科目'
  dialog.visible = true
  // 清除可能存在的校验状态 (保险起见)
  subjectFormRef.value?.clearValidate();
}

// 删除按钮点击
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除科目 "${row.subjectName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteSubject(row.id)
      ElMessage.success('删除成功')
      getList() // 刷新列表
    } catch (error) {
      console.error("Failed to delete subject:", error)
      // ElMessage.error('删除失败')
    }
  }).catch(() => { /* 取消操作 */ })
}

// 批量删除按钮点击
const handleBatchDelete = () => {
    if (selectedRows.value.length === 0) {
        ElMessage.warning('请至少选择一条数据');
        return;
    }
    const ids = selectedRows.value.map(row => row.id);
    const names = selectedRows.value.map(row => row.subjectName).join(', ');
    ElMessageBox.confirm(`确定要删除选中的科目 "${names}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        try {
            await deleteSubjects(ids);
            ElMessage.success('批量删除成功');
            getList(); // 刷新列表
            xTableRef.value?.clearCheckboxRow() // 清空表格选中
        } catch (error) {
            console.error("Failed to batch delete subjects:", error);
        }
    }).catch(() => {});
};


// 导出按钮点击
const handleExport = async () => {
    ElMessageBox.confirm('确认导出所有符合条件的科目数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            loading.value = true;
            
            const response = await request({
                url: '/api/subjects/export', // **** 修改 URL ****
                method: 'post', 
                data: queryParams,
                responseType: 'blob' 
            });

            // --- Blob 处理逻辑 --- 
            const contentDisposition = response.headers['content-disposition'];
            let fileName = '科目数据.xlsx'; // 默认文件名
            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename\*?=utf-8''([^;]+)|filename="([^;]+)"/i);
                if (fileNameMatch) {
                    fileName = decodeURIComponent(fileNameMatch[1] || fileNameMatch[2]);
                }
            }

            const blob = new Blob([response.data], { type: response.headers['content-type'] });
            const downloadUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = downloadUrl;
            link.download = fileName;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(downloadUrl);
            // --- Blob 处理逻辑结束 --- 

            ElMessage.success('导出任务已开始');

        } catch (error) {
            console.error("Failed to export subjects:", error);
            // 添加 Blob 错误解析
            if (error.response && error.response.data instanceof Blob) {
                const reader = new FileReader();
                reader.onload = function() {
                    try {
                        const errData = JSON.parse(reader.result);
                        ElMessage.error(errData.msg || '导出失败');
                    } catch /* _parseError */ {
                         ElMessage.error('导出失败，无法解析错误信息');
                    }
                };
                reader.onerror = function() {
                    ElMessage.error('导出失败，无法读取错误信息');
                };
                reader.readAsText(error.response.data);
            } else {
               // 如果 request.js 已经弹了消息，这里可以不再弹
               // ElMessage.error('导出失败'); 
            }
        } finally {
            loading.value = false;
        }
    }).catch(() => {});
}

// 导入按钮点击
const handleImport = () => {
    importDialog.uploadFile = null; // 清空已选文件
    uploadRef.value?.clearFiles(); // 清空上传列表
    importDialog.visible = true;
}

// 处理文件选择变化
const handleFileChange = (file) => {
    importDialog.uploadFile = file.raw; // 存储原始文件对象
};

// 处理文件超出限制
const handleExceed = () => {
    ElMessage.warning('只能选择一个文件，请先移除当前文件再选择新的文件');
};

// 提交导入
const submitImport = async () => {
    if (!importDialog.uploadFile) {
        ElMessage.warning('请先选择要导入的文件');
        return;
    }
    importDialog.loading = true;
    try {
        const formData = new FormData();
        formData.append('file', importDialog.uploadFile);
        const responseMsg = await importSubjects(formData); // 调用导入接口
        ElMessage.success(responseMsg); // 显示后端返回的导入结果
        importDialog.visible = false;
        getList(); // 刷新列表
    } catch (error) {
        console.error("Failed to import subjects:", error);
        // 错误消息已在 request.js 中处理，或后端直接返回在 message 中
        // ElMessage.error('导入失败');
    } finally {
        importDialog.loading = false;
    }
};


// 提交表单 (新增/修改)
const submitForm = async () => {
  if (!subjectFormRef.value) return;
  try {
    // 触发表单校验
    await subjectFormRef.value.validate();

    // 校验通过，执行提交逻辑
    if (dialog.isEdit) {
      await updateSubject(dialog.formData);
      ElMessage.success('修改成功');
    } else {
      await addSubject(dialog.formData);
      ElMessage.success('新增成功');
    }
    dialog.visible = false;
    getList(); // 刷新列表
  } catch (validationError) {
    // 校验失败
    console.log('Form validation failed:', validationError);
    // ElMessage.warning('请检查表单输入项'); // 校验失败时 Form 组件会自动提示
  }
}

// --- lifecycle hooks ---
onMounted(() => {
  getList() // 组件挂载后加载数据
})

</script>

<style scoped>
.subject-management-container {
  display: flex;
  flex-direction: column;
  gap: 15px; /* 卡片之间的间距 */
}

.search-card,
.operate-card,
.table-card {
  border-radius: 4px;
}

/* 调整 VxePager 样式 */
.table-pager {
  margin-top: 15px;
  text-align: right;
}

.el-form--inline .el-form-item {
  margin-bottom: 10px; /* 调整搜索项间距 */
}

/* 导入对话框样式 */
.el-upload__tip.text-center{
    text-align: center;
    margin-top: 10px;
}

/* 给表格添加一个 class 以便设置高度 */
.subject-table {
    /* 考虑设置一个最小高度或计算高度，避免内容过少时分页器紧贴表格 */
    min-height: 300px;
}
</style>
