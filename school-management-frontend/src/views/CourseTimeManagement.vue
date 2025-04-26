<template>
  <div class="course-time-management-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryFormRef">
        <el-form-item label="节课名称" prop="periodName">
          <el-input v-model="queryParams.periodName" placeholder="请输入节课名称" clearable />
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
         <!-- 可根据需要添加导入导出按钮 -->
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
        class="course-time-table"
      >
        <vxe-column type="checkbox" width="60"></vxe-column>
        <vxe-column field="id" title="ID" width="80" sortable></vxe-column>
        <vxe-column field="periodName" title="节课名称" min-width="150" sortable></vxe-column>
        <vxe-column field="startTime" title="开始时间" width="120" sortable></vxe-column>
        <vxe-column field="endTime" title="结束时间" width="120" sortable></vxe-column>
        <vxe-column field="isValid" title="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isValid === 1 ? 'success' : 'danger'">
              {{ row.isValid === 1 ? '有效' : '无效' }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column field="remark" title="备注" min-width="200" show-overflow-tooltip></vxe-column>
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
      <el-form :model="dialog.formData" :rules="rules" ref="courseTimeFormRef" label-width="80px">
        <el-form-item label="节课名称" prop="periodName">
          <el-input v-model="dialog.formData.periodName" placeholder="请输入节课名称" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker
            v-model="dialog.formData.startTime"
            placeholder="选择开始时间"
            value-format="HH:mm:ss" 
            format="HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker
            v-model="dialog.formData.endTime"
            placeholder="选择结束时间"
             value-format="HH:mm:ss"
             format="HH:mm:ss"
            style="width: 100%;"
          />
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

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue' // 移除未使用的导入导出图标
import { listCourseTimes, addCourseTime, updateCourseTime, deleteCourseTime, deleteCourseTimes } from '@/api/courseTime' // 更新导入路径

// --- refs ---
const queryFormRef = ref(null) // 查询表单引用
const courseTimeFormRef = ref(null) // 新增/修改表单引用
const xTableRef = ref(null)    // VxeTable 引用

// --- state ---
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([]) // 存储选中的行数据

// 查询参数
const queryParams = reactive({
  periodName: '',
  isValid: null,
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
    periodName: '',
    startTime: '', // 使用字符串格式 HH:mm:ss
    endTime: '', // 使用字符串格式 HH:mm:ss
    isValid: 1,
    remark: ''
});

// 对话框状态 (新增/修改)
const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false,
  formData: initialFormData()
})

// 表单校验规则
const rules = reactive({
  periodName: [
    { required: true, message: '节课名称不能为空', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '开始时间不能为空', trigger: 'change' } // trigger 改为 change
  ],
  endTime: [
    { required: true, message: '结束时间不能为空', trigger: 'change' },
    // 自定义校验：结束时间必须晚于开始时间
    { validator: (rule, value, callback) => {
        if (value && dialog.formData.startTime && value <= dialog.formData.startTime) {
          callback(new Error('结束时间必须晚于开始时间'));
        } else {
          callback();
        }
      }, trigger: 'change'
    }
  ]
})

// --- methods ---

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams, pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    const response = await listCourseTimes(params)
    tableData.value = response.data?.records || []
    pagination.total = response.data?.total || 0
  } catch (error) {
    console.error("Failed to fetch course time list:", error)
    ElMessage.error('加载列表失败') // 添加错误提示
  } finally {
    loading.value = false
  }
}

// 搜索按钮点击
const handleQuery = () => {
  pagination.pageNum = 1
  getList()
}

// 重置按钮点击
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

// 表格 Checkbox 选中事件
const handleSelectionChange = ({ records }) => {
  selectedRows.value = records // VxeTable 返回的是 records
}

// 分页事件
const handlePageChange = ({ currentPage, pageSize }) => {
  pagination.pageNum = currentPage
  pagination.pageSize = pageSize
  getList()
}

// 重置表单
const resetForm = () => {
    dialog.formData = initialFormData();
    courseTimeFormRef.value?.resetFields(); // 清除校验状态
}

// 新增按钮点击
const handleAdd = () => {
  resetForm() // 重置表单数据
  dialog.isEdit = false
  dialog.title = '新增节课时间'
  dialog.visible = true
}

// 修改按钮点击
const handleUpdate = (row) => {
  resetForm() // 先重置
  dialog.formData = { ...row } // 浅拷贝行数据到表单
  dialog.isEdit = true
  dialog.title = '修改节课时间'
  dialog.visible = true
}

// 提交表单 (新增/修改)
const submitForm = async () => {
  if (!courseTimeFormRef.value) return
  try {
    await courseTimeFormRef.value.validate() // 表单校验
    loading.value = true // 可选：添加加载状态
    if (dialog.isEdit) {
      await updateCourseTime(dialog.formData)
      ElMessage.success('修改成功')
    } else {
      await addCourseTime(dialog.formData)
      ElMessage.success('新增成功')
    }
    dialog.visible = false
    getList() // 刷新列表
  } catch (validationError) {
    console.log('Validation failed:', validationError)
    // ElMessage.error('请检查表单项') // validate 会自动提示
  }  finally {
      loading.value = false
  }
}

// 删除按钮点击
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除节课时间 "${row.periodName}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true
      await deleteCourseTime(row.id)
      ElMessage.success('删除成功')
      getList() // 刷新列表
    } catch (error) {
      console.error("Failed to delete course time:", error)
      ElMessage.error('删除失败')
    } finally {
        loading.value = false
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 批量删除按钮点击
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  const ids = selectedRows.value.map(row => row.id)
  const names = selectedRows.value.map(row => row.periodName).join(', ')
  ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 条节课时间 (${names}) 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true
      await deleteCourseTimes(ids)
      ElMessage.success('批量删除成功')
      getList() // 刷新列表
    } catch (error) {
      console.error("Failed to batch delete course times:", error)
      ElMessage.error('批量删除失败')
    } finally {
        loading.value = false
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// --- lifecycle hooks ---
onMounted(() => {
  getList() // 组件挂载后加载数据
})
</script>

<style scoped>
.course-time-management-container {
  /* 与 SubjectManagement 一致，使用 Flex 布局 */
  display: flex; 
  flex-direction: column;
  gap: 15px; 
  /* 移除 padding，因为 flex 和 gap 会处理布局和间距 */
  /* padding: 20px; */ 
}

.search-card,
.operate-card,
.table-card {
  /* 移除 margin-bottom，因为 gap 会处理间距 */
  /* margin-bottom: 20px; */
  border-radius: 4px;
}

.table-pager {
  margin-top: 15px; /* 与 SubjectManagement 统一间距 */
  text-align: right; 
}

.el-form--inline .el-form-item {
  margin-bottom: 10px;
}

.course-time-table {
  min-height: 300px; 
}
</style> 