<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="searchData">
        <el-form-item prop="studentName" label="学生姓名">
          <el-input v-model="searchData.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item prop="studentNumber" label="学号">
          <el-input v-model="searchData.studentNumber" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item prop="phone" label="联系方式">
          <el-input v-model="searchData.phone" placeholder="请输入联系方式" clearable />
        </el-form-item>
        <el-form-item prop="gender" label="性别">
          <el-select v-model="searchData.gender" placeholder="请选择性别" clearable style="width: 120px;">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item prop="currentClazzId" label="所在班级">
          <el-select v-model="searchData.currentClazzId" placeholder="请选择班级" clearable filterable>
             <!-- Populated by fetchClazzOptions -->
             <el-option
                v-for="item in clazzOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
             />
          </el-select>
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
       <!-- TODO: Maybe add buttons for class assignment/history? -->
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
        <vxe-column field="studentName" title="姓名" min-width="100" />
        <vxe-column field="studentNumber" title="学号" min-width="120" />
        <vxe-column field="gender" title="性别" width="60" align="center" />
        <vxe-column field="age" title="年龄" width="60" align="center" />
        <vxe-column field="idCard" title="身份证号" min-width="180" />
        <vxe-column field="birthDate" title="出生日期" width="120" align="center" />
        <vxe-column field="phone" title="联系方式" min-width="120" />
        <vxe-column field="currentClazzId" title="所在班级" min-width="120">
           <template #default="{ row }">
             <span>{{ formatClazzName(row.currentClazzId) }}</span>
           </template>
        </vxe-column>
        <vxe-column field="address" title="家庭住址" min-width="200" show-overflow />
        <vxe-column field="nativePlace" title="户籍" min-width="150" show-overflow />
        <vxe-column field="nation" title="民族" width="100" />
        <vxe-column field="createTime" title="创建时间" width="170" align="center" />
        <vxe-column title="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" icon="Edit" @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" link size="small" icon="Delete" @click="handleDelete(row)">删除</el-button>
            <el-button type="info" link size="small" icon="Tickets" @click="handleShowHistory(row)">班级历史</el-button> 
          </template>
        </vxe-column>
      </vxe-table>
    </el-card>

    <!-- 新增/修改弹窗 -->
    <student-form-dialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="formData"
      :clazz-list="clazzOptions"
      @save="handleSave"
      ref="dialogFormRef" 
    />

    <!-- 导入弹窗 -->
    <import-dialog
       v-model:visible="importDialogVisible"
       title="导入学生数据"
       :upload-url="studentImportUrl"
       :template-url="studentTemplateUrl"
       @success="handleImportSuccess"
    />

    <!-- Class History Dialog -->
    <student-class-history-dialog
       v-model:visible="historyDialogVisible"
       :student-name="currentStudentName"
       :history-list="currentStudentHistory"
       :loading="historyLoading"
    />

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox, ElCard, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton } from 'element-plus';
import { Search, Refresh, Plus, Delete, Download, Upload, Edit, Tickets } from '@element-plus/icons-vue';
import ImportDialog from '@/components/ImportDialog.vue';
import StudentFormDialog from '@/components/StudentFormDialog.vue';
import StudentClassHistoryDialog from '@/components/StudentClassHistoryDialog.vue';
import { getStudentList, getStudentInfo, addStudent, updateStudent, deleteStudent, deleteStudentsBatch, getStudentClassHistory } from '@/api/student';
import { getClassList } from '@/api/class';
import request from '@/utils/request';

const loading = ref(false);
const searchFormRef = ref(null);
const vxeTableRef = ref(null);
const tableData = ref([]);
const multipleSelection = ref([]);
const dialogFormRef = ref(null);
const clazzOptions = ref([]);

const searchData = reactive({
  studentName: '',
  studentNumber: '',
  phone: '',
  gender: '',
  currentClazzId: null,
});

const pagerConfig = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  layouts: ["Total", "Sizes", "PrevPage", "JumpNumber", "NextPage", "FullJump"],
  pageSizes: [10, 20, 50, 100]
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const formData = ref({});

const importDialogVisible = ref(false);

const historyDialogVisible = ref(false);
const historyLoading = ref(false);
const currentStudentHistory = ref([]);
const currentStudentName = ref('');

const baseUrl = import.meta.env.VITE_APP_BASE_API;
const studentImportUrl = computed(() => `${baseUrl}/student/import`);
const studentTemplateUrl = computed(() => `${baseUrl}/student/template`);

onMounted(() => {
  fetchData();
  fetchClazzOptions();
});

const fetchData = async () => {
  loading.value = true;
  const params = {
    ...searchData,
    pageNum: pagerConfig.currentPage,
    pageSize: pagerConfig.pageSize
  };
  try {
    const res = await getStudentList(params);
    if (res.code === 200) {
      tableData.value = res.data.records || [];
      pagerConfig.total = Number(res.data.total || 0);
    } else {
      ElMessage.error(res.msg || '获取学生列表失败');
    }
  } catch (error) {
    console.error("获取学生列表失败:", error);
    ElMessage.error('获取学生列表失败');
  } finally {
    loading.value = false;
  }
};

const fetchClazzOptions = async () => {
  try {
    const res = await getClassList({ pageNum: 1, pageSize: 9999 }); 
    if (res.code === 200) {
      clazzOptions.value = res.data?.records || [];
    } else {
      ElMessage.error(res.msg || '获取班级列表失败');
    }
  } catch (error) {
    console.error("获取班级列表失败:", error);
    ElMessage.error('获取班级列表失败');
  }
};

const handleSearch = () => {
  pagerConfig.currentPage = 1;
  fetchData();
};

const resetSearch = () => {
  searchFormRef.value?.resetFields();
  handleSearch();
};

const handlePageChange = ({ currentPage, pageSize }) => {
  pagerConfig.currentPage = currentPage;
  pagerConfig.pageSize = pageSize;
  fetchData();
};

const handleCheckboxChange = ({ records }) => {
  multipleSelection.value = records;
};
const handleCheckboxAllChange = ({ records }) => {
  multipleSelection.value = records;
};

const handleAdd = () => {
  dialogTitle.value = '新增学生';
  formData.value = {};
  dialogVisible.value = true;
  dialogFormRef.value?.formRef?.resetFields();
};

const handleEdit = async (row) => {
  dialogTitle.value = '修改学生';
  try {
    const res = await getStudentInfo(row.id);
    if (res.code === 200) {
      formData.value = res.data;
      dialogVisible.value = true;
    } else {
      ElMessage.error(res.msg || '获取学生详情失败');
    }
  } catch(error) {
    console.error("获取学生详情失败:", error);
    ElMessage.error('获取学生详情失败');
  }
};

const handleDelete = (row) => {
   ElMessageBox.confirm(`确定要删除学生 "${row.studentName}" 吗？此操作不可恢复！`, '警告', {
       confirmButtonText: '确定删除',
       cancelButtonText: '取消',
       type: 'warning'
   }).then(async () => {
       try {
           const res = await deleteStudent(row.id);
           if (res.code === 200) {
               ElMessage.success('删除成功');
               fetchData();
           } else {
               ElMessage.error(res.msg || '删除失败');
           }
       } catch (error) {
           console.error("删除学生失败:", error);
           ElMessage.error('删除失败');
       }
   }).catch(() => {
       ElMessage.info('已取消删除');
   });
};

const handleBatchDelete = () => {
   if (multipleSelection.value.length === 0) {
       ElMessage.warning('请至少选择一个学生进行删除');
       return;
   }
   const selectedNames = multipleSelection.value.map(item => item.studentName).join(', ');
   const selectedIds = multipleSelection.value.map(item => item.id);
   ElMessageBox.confirm(`确定要批量删除学生 "${selectedNames}" 吗？此操作不可恢复！`, '警告', {
       confirmButtonText: '确定删除',
       cancelButtonText: '取消',
       type: 'warning'
   }).then(async () => {
       try {
           const res = await deleteStudentsBatch(selectedIds);
           if (res.code === 200) {
               ElMessage.success('批量删除成功');
               multipleSelection.value = [];
               vxeTableRef.value?.clearCheckboxRow();
               fetchData();
           } else {
               ElMessage.error(res.msg || '批量删除失败');
           }
       } catch (error) {
           console.error("批量删除学生失败:", error);
           ElMessage.error('批量删除失败');
       }
   }).catch(() => {
       ElMessage.info('已取消批量删除');
   });
};

const handleExport = () => {
   const exportParams = { ...searchData };

   ElMessageBox.confirm('确定导出当前查询结果的学生数据吗？', '提示', {
       confirmButtonText: '确定导出',
       cancelButtonText: '取消',
       type: 'info'
   }).then(async () => {
       try {
           loading.value = true;
           const response = await request({
               url: '/student/export',
               method: 'post',
               data: exportParams,
               responseType: 'blob'
           });

           const contentDisposition = response.headers['content-disposition'];
           let fileName = '学生数据.xlsx';
           if (contentDisposition) {
               const fileNameMatch = contentDisposition.match(/filename\*?=utf-8''([^;]+)|filename="([^;]+)"/i);
               if (fileNameMatch) {
                   fileName = decodeURIComponent(fileNameMatch[1] || fileNameMatch[2]);
               }
           }
           const blob = new Blob([response.data], { type: response.headers['content-type'] || 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
           const downloadUrl = window.URL.createObjectURL(blob);
           const link = document.createElement('a');
           link.href = downloadUrl;
           link.download = fileName;
           document.body.appendChild(link);
           link.click();
           document.body.removeChild(link);
           window.URL.revokeObjectURL(downloadUrl);
           ElMessage.success('学生数据导出任务已创建');

       } catch (error) {
           console.error("导出学生数据失败:", error);
           ElMessage.error('导出学生数据失败');
       } finally {
           loading.value = false;
       }
   }).catch(() => {
       ElMessage.info('已取消导出');
   });
};

const handleImport = () => {
  importDialogVisible.value = true;
};

const handleImportSuccess = () => {
   ElMessage.success('学生数据导入成功');
   fetchData();
};

const handleSave = async (dataFromDialog) => {
  const isUpdate = !!dataFromDialog.id;
  const apiCall = isUpdate ? updateStudent : addStudent;
  const successMsg = isUpdate ? '修改成功' : '新增成功';
  const errorMsg = isUpdate ? '修改失败' : '新增失败';

  try {
    dialogFormRef.value?.setLoading(true);
    const res = await apiCall(dataFromDialog);
    if (res.code === 200) {
      ElMessage.success(successMsg);
      dialogVisible.value = false;
      fetchData();
    } else {
      ElMessage.error(res.msg || errorMsg);
    }
  } catch (error) {
    console.error(`${errorMsg}:`, error);
    ElMessage.error(errorMsg);
  } finally {
    dialogFormRef.value?.setLoading(false);
  }
};

const handleShowHistory = async (row) => {
    currentStudentName.value = row.studentName;
    historyDialogVisible.value = true;
    historyLoading.value = true;
    currentStudentHistory.value = [];
    try {
        const res = await getStudentClassHistory(row.id);
        if (res.code === 200) {
            currentStudentHistory.value = res.data || [];
        } else {
            ElMessage.error(res.msg || '获取班级历史失败');
        }
    } catch (error) {
        console.error("获取班级历史失败:", error);
        ElMessage.error('获取班级历史失败');
    } finally {
        historyLoading.value = false;
    }
};

const formatClazzName = (clazzId) => {
  if (!clazzId) return '-';
  const clazz = clazzOptions.value.find(option => option.id === clazzId);
  return clazz ? clazz.className : `未知班级(${clazzId})`;
};

</script>

<style scoped>
.search-wrapper {
  margin-bottom: 10px;
}
.table-toolbar-wrapper {
  margin-bottom: 10px;
}
</style> 