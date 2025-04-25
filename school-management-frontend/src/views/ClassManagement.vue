<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="searchData">
        <el-form-item prop="className" label="班级名称">
          <el-input v-model="searchData.className" placeholder="请输入班级名称" clearable />
        </el-form-item>
        <el-form-item prop="grade" label="年级">
          <el-input v-model="searchData.grade" placeholder="请输入年级" clearable />
        </el-form-item>
        <el-form-item prop="teacherId" label="班主任">
           <el-select v-model="searchData.teacherId" placeholder="请选择班主任" clearable filterable>
             <!-- TODO: Populate teacher options -->
             <el-option
                v-for="item in teacherOptions"
                :key="item.id"
                :label="item.teacherName"
                :value="item.id"
             />
           </el-select>
        </el-form-item>
        <el-form-item prop="status" label="状态">
           <el-select v-model="searchData.status" placeholder="请选择状态" clearable>
             <el-option label="未开班" :value="0" />
             <el-option label="进行中" :value="1" />
             <el-option label="已结业" :value="2" />
           </el-select>
        </el-form-item>
        <el-form-item label="开班日期">
           <el-date-picker
              v-model="searchData.startDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              clearable
           />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">查询</el-button>
          <el-button icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card shadow="never" class="table-toolbar-wrapper">
       <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
       <el-button type="danger" plain icon="Delete" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">批量删除</el-button>
       <el-button type="info" plain icon="Upload" @click="handleImport">导入</el-button>
       <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
    </el-card>

    <!-- 表格区域 -->
    <el-card shadow="never">
      <vxe-table
        ref="vxeTableRef"
        :data="tableData"
        :loading="loading"
        border
        stripe
        :column-config="{ resizable: true }"
        :row-config="{ isHover: true }"
        :pager-config="pagerConfig"
        :checkbox-config="{ checkField: 'checked', highlight: true }"
        @page-change="handlePageChange"
        @checkbox-all="handleCheckboxAllChange"
        @checkbox-change="handleCheckboxChange"
      >
        <vxe-column type="checkbox" width="50" align="center" fixed="left" />
        <vxe-column field="id" title="ID" width="80" align="center" />
        <vxe-column field="className" title="班级名称" min-width="150" />
        <vxe-column field="grade" title="年级" min-width="100" />
        <vxe-column field="teacherId" title="班主任" min-width="120">
           <template #default="{ row }">
             <!-- TODO: Format teacher name based on teacherId -->
             <span>{{ formatTeacherName(row.teacherId) }}</span>
           </template>
        </vxe-column>
        <vxe-column field="startDate" title="开班日期" width="120" align="center" />
        <vxe-column field="endDate" title="结业日期" width="120" align="center" />
        <vxe-column field="status" title="状态" width="100" align="center">
           <template #default="{ row }">
             <!-- TODO: Format status -->
             <el-tag :type="getStatusTagType(row.status)">{{ formatStatus(row.status) }}</el-tag>
           </template>
        </vxe-column>
        <vxe-column field="remark" title="备注" min-width="200" show-overflow />
        <vxe-column field="createTime" title="创建时间" width="170" align="center" />
        <vxe-column title="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" icon="Edit" @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" link size="small" icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </vxe-column>
      </vxe-table>
    </el-card>

    <!-- 新增/修改弹窗 -->
    <class-form-dialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="formData"
      :teacher-list="teacherOptions"
      @save="handleSave"
      ref="dialogFormRef"
    />

    <!-- 导入弹窗 -->
    <import-dialog
       v-model:visible="importDialogVisible"
       title="导入班级数据"
       :upload-url="classImportUrl"
       :template-url="classTemplateUrl"
       @success="handleImportSuccess"
    />

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox, ElCard, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElDatePicker, ElTag } from 'element-plus';
import { Search, Refresh, Plus, Delete, Download, Upload, Edit } from '@element-plus/icons-vue'; // Import necessary icons
import ImportDialog from '@/components/ImportDialog.vue'; // Reuse ImportDialog
import ClassFormDialog from '@/components/ClassFormDialog.vue'; // Import ClassFormDialog
// Import API functions for class management and teacher list
import { getClassList, getClassInfo, addClass, updateClass, deleteClass, deleteClassesBatch } from '@/api/class';
import { listValidTeachers } from '@/api/teacher'; // Import teacher list API
import request from '@/utils/request'; // For export potentially

const loading = ref(false);
const searchFormRef = ref(null);
const vxeTableRef = ref(null);
const tableData = ref([]);
const multipleSelection = ref([]);
const dialogFormRef = ref(null); // For the future ClassFormDialog
const teacherOptions = ref([]); // To store teacher list for dropdowns

// 搜索条件
const searchData = reactive({
  className: '',
  grade: '',
  teacherId: null,
  status: null,
  startDateRange: [], // For date range picker
  startDateBegin: '',
  startDateEnd: '',
  pageNum: 1,
  pageSize: 10
});

// 分页配置
const pagerConfig = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  layouts: ["Total", "Sizes", "PrevPage", "JumpNumber", "NextPage", "FullJump"],
  pageSizes: [10, 20, 50, 100]
});

// 弹窗相关状态 (Add/Edit)
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formData = ref({}); // For the future ClassFormDialog

// 导入弹窗相关状态
const importDialogVisible = ref(false);

// --- 计算属性 (URLs for import/export) ---
const baseUrl = import.meta.env.VITE_APP_BASE_API;
const classImportUrl = computed(() => `${baseUrl}/clazz/import`);
const classTemplateUrl = computed(() => `${baseUrl}/clazz/template`); // TODO: Create backend endpoint for template download if needed

// --- 生命周期钩子 ---
onMounted(() => {
  fetchData();
  fetchTeacherOptions(); // Fetch teachers for dropdowns
});

// --- API 调用方法 ---
const fetchData = async () => {
  loading.value = true;
  // Process searchData.startDateRange into startDateBegin and startDateEnd
  searchData.startDateBegin = searchData.startDateRange?.[0] || '';
  searchData.startDateEnd = searchData.startDateRange?.[1] || '';

  // Create params object, excluding the original date range array
  const params = { ...searchData, pageNum: pagerConfig.currentPage, pageSize: pagerConfig.pageSize };
  delete params.startDateRange; // Remove the range array before sending

  try {
    const res = await getClassList(params);
    if (res.code === 200) {
      tableData.value = res.data.records; // Assuming backend returns { records: [...], total: ... }
      pagerConfig.total = Number(res.data.total);
    } else {
      ElMessage.error(res.msg || '获取班级列表数据失败');
      tableData.value = []; // Clear table on error
      pagerConfig.total = 0;
    }
  } catch (error) {
    console.error("获取班级列表失败:", error);
    ElMessage.error('获取班级列表失败');
    tableData.value = []; // Clear table on error
    pagerConfig.total = 0;
  } finally {
    loading.value = false;
  }
};

const fetchTeacherOptions = async () => {
  console.log("TODO: Fetch teacher options for dropdowns");
  try {
    const res = await listValidTeachers();
    if (res.code === 200) {
      teacherOptions.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取班主任列表失败');
    }
  } catch (error) {
    console.error("获取班主任列表失败:", error);
    ElMessage.error('获取班主任列表失败');
  }
};

// --- 事件处理方法 (Placeholders) ---
const handleSearch = () => {
  pagerConfig.currentPage = 1;
  fetchData();
};

const resetSearch = () => {
  searchFormRef.value?.resetFields();
  searchData.startDateRange = []; // Manually reset date range
  searchData.pageNum = 1;
  searchData.pageSize = pagerConfig.pageSize;
  handleSearch();
};

const handlePageChange = ({ currentPage, pageSize }) => {
  pagerConfig.currentPage = currentPage;
  pagerConfig.pageSize = pageSize;
  searchData.pageNum = currentPage;
  searchData.pageSize = pageSize;
  fetchData();
};

const handleCheckboxChange = ({ records }) => {
  multipleSelection.value = records;
};
const handleCheckboxAllChange = ({ records }) => {
  multipleSelection.value = records;
};

const handleAdd = () => {
  dialogTitle.value = '新增班级';
  formData.value = {}; // Reset form data for add
  dialogVisible.value = true;
};

const handleEdit = async (row) => {
  dialogTitle.value = '修改班级';
  try {
    const res = await getClassInfo(row.id);
    if (res.code === 200 && res.data) {
      formData.value = res.data; // Populate formData with fetched details
      dialogVisible.value = true; // Open the dialog
    } else {
      ElMessage.error(res.msg || '获取班级详情失败');
    }
  } catch(error) {
    console.error("获取班级详情失败:", error);
    ElMessage.error('获取班级详情失败，请稍后重试');
  }
};

const handleDelete = (row) => {
   ElMessageBox.confirm(`确定要删除班级 " ${row.className} " 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteClass(row.id); // Call deleteClass API
      if (res.code === 200) {
        ElMessage.success('删除成功');
        fetchData(); // Refresh list
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    } catch (error) {
       console.error("删除班级失败:", error);
       ElMessage.error('删除班级失败');
    }
  }).catch(() => {
       ElMessage.info('取消删除');
  });
};

const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请至少选择一条数据');
    return;
  }
  const selectedNames = multipleSelection.value.map(item => item.className).join(', ');
  const selectedIds = multipleSelection.value.map(item => item.id);
   ElMessageBox.confirm(`确定要批量删除班级 " ${selectedNames} " 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
     try {
      const res = await deleteClassesBatch(selectedIds); // Call deleteClassesBatch API
      if (res.code === 200) {
        ElMessage.success('批量删除成功');
        multipleSelection.value = []; // Clear selection
        vxeTableRef.value?.clearCheckboxRow(); // Clear VxeTable checkbox state
        fetchData(); // Refresh list
      } else {
        ElMessage.error(res.msg || '批量删除失败');
      }
    } catch (error) {
       console.error("批量删除班级失败:", error);
       ElMessage.error('批量删除班级失败');
    }
  }).catch(() => {
       ElMessage.info('取消批量删除');
  });
};

const handleExport = () => {
  console.log("TODO: Handle Export");
   const exportParams = {
      className: searchData.className,
      grade: searchData.grade,
      teacherId: searchData.teacherId,
      status: searchData.status,
      startDateBegin: searchData.startDateRange?.[0] || '',
      startDateEnd: searchData.startDateRange?.[1] || ''
  };

  ElMessageBox.confirm('确定导出当前查询结果吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true;
      const response = await request({
        url: '/clazz/export',
        method: 'post',
        data: exportParams,
        responseType: 'blob'
      });

      // --- Blob handling logic (copied from TeacherManagement) ---
      const contentDisposition = response.headers['content-disposition'];
      let fileName = '班级数据.xlsx';
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
      ElMessage.success('导出任务已开始');

    } catch (error) {
        console.error("导出失败:", error);
        // Simplified error handling for brevity
        ElMessage.error('导出失败');
    } finally {
        loading.value = false;
    }
  }).catch(() => {});
};

const handleImport = () => {
  importDialogVisible.value = true;
};

const handleImportSuccess = () => {
   fetchData(); // Refresh list after successful import
};

const handleSave = async (dataFromDialog) => {
  const isUpdate = !!dataFromDialog.id;
  const apiCall = isUpdate ? updateClass : addClass;
  try {
    // No need to set loading here, dialog does it internally
    const res = await apiCall(dataFromDialog);
    if (res.code === 200) {
      ElMessage.success(isUpdate ? '修改成功' : '新增成功');
      dialogVisible.value = false; // Close the dialog on success
      fetchData(); // Refresh the table data
    } else {
      ElMessage.error(res.msg || (isUpdate ? '修改失败' : '新增失败'));
    }
  } catch (error) {
    console.error("保存班级失败:", error);
    ElMessage.error('保存班级失败');
  } finally {
    // Stop the loading state in the dialog component regardless of success or failure
    dialogFormRef.value?.stopLoading();
  }
};

// --- 格式化函数 ---
const formatTeacherName = (teacherId) => {
  if (!teacherId) return '-';
  const teacher = teacherOptions.value.find(option => option.id === teacherId);
  return teacher ? teacher.teacherName : `ID(${teacherId})`;
};

const formatStatus = (status) => {
  switch (status) {
    case 0: return '未开班';
    case 1: return '进行中';
    case 2: return '已结业';
    default: return '未知';
  }
};

const getStatusTagType = (status) => {
   switch (status) {
    case 0: return 'info';
    case 1: return 'success';
    case 2: return 'warning';
    default: return 'danger';
  }
};

</script>

<style scoped>
.search-wrapper {
  margin-bottom: 10px;
}
.table-toolbar-wrapper {
  margin-bottom: 10px;
}
/* Add any specific styles for ClassManagement if needed */
</style> 