<!--
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 16:26:18
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 16:41:00
 * @FilePath: \schoolmanagenta\school-management-frontend\src\views\GradeManagement.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="searchData">
        <el-form-item prop="studentId" label="学生">
          <el-select v-model="searchData.studentId" placeholder="请选择学生" clearable filterable>
            <el-option v-for="option in studentOptions" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </el-form-item>
        <el-form-item prop="subjectId" label="科目">
          <el-select v-model="searchData.subjectId" placeholder="请选择科目" clearable filterable>
            <el-option v-for="option in subjectOptions" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </el-form-item>
         <el-form-item prop="clazzId" label="班级">
          <el-select v-model="searchData.clazzId" placeholder="请选择班级" clearable filterable>
            <el-option v-for="option in clazzOptions" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </el-form-item>
        <el-form-item prop="examName" label="考试名称">
          <el-input v-model="searchData.examName" placeholder="请输入考试名称" clearable />
        </el-form-item>
         <el-form-item prop="teacherId" label="录入/任课老师">
          <el-select v-model="searchData.teacherId" placeholder="请选择老师" clearable filterable>
            <el-option v-for="option in teacherOptions" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </el-form-item>
        <!-- TODO: Add search condition for score range or exam time range? -->
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card shadow="never" class="table-toolbar-wrapper">
       <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
       <el-button type="danger" plain :icon="Delete" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">批量删除</el-button>
       <el-button type="info" plain :icon="Upload" @click="handleImport">导入</el-button>
       <el-button type="warning" plain :icon="Download" @click="handleExport">导出</el-button>
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
        <vxe-column field="studentName" title="学生" min-width="120" />
        <vxe-column field="subjectName" title="科目" min-width="100" />
        <vxe-column field="clazzName" title="班级" min-width="150" />
        <vxe-column field="examName" title="考试名称" min-width="180" />
        <vxe-column field="score" title="分数" width="80" align="center" />
        <vxe-column field="examTime" title="考试时间" width="170" align="center" />
        <vxe-column field="teacherName" title="录入/任课老师" min-width="120" />
        <vxe-column field="recordTime" title="录入时间" width="170" align="center" />
        <vxe-column field="remark" title="备注" min-width="150" show-overflow />
        <vxe-column field="createTime" title="创建时间" width="170" align="center" />
        <vxe-column title="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" :icon="Edit" @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" link size="small" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </vxe-column>
      </vxe-table>
    </el-card>

     <!-- 新增/修改弹窗 -->
     <grade-form-dialog
       ref="dialogFormRef"
       v-model:visible="dialogVisible"
       :title="dialogTitle"
       :form-data="formData"
       :student-list="studentOptions"
       :subject-list="subjectOptions"
       :clazz-list="clazzOptions"
       :teacher-list="teacherOptions"
       @save="handleSave"
     />

     <!-- 导入弹窗 -->
     <import-dialog
       v-model:visible="importDialogVisible"
       title="导入成绩数据"
       :upload-url="gradeImportUrl" 
       :template-url="gradeTemplateUrl" 
       @success="handleImportSuccess" 
     />

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox, ElCard, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton } from 'element-plus';
import { Search, Refresh, Plus, Delete, Download, Upload, Edit } from '@element-plus/icons-vue';
import ImportDialog from '@/components/ImportDialog.vue';
import GradeFormDialog from '@/components/GradeFormDialog.vue';
import { getGradeList, addGrade, updateGrade, deleteGrade, deleteGradesBatch, exportGrades, getGradeTemplateUrl, getGradeImportUrl } from '@/api/grade';
import { getStudentList } from '@/api/student';
import { listValidSubjects } from '@/api/subject';
import { getClassList } from '@/api/class';
import { listValidTeachers } from '@/api/teacher';
import { downloadFile, getFileNameFromHeaders } from '@/utils/download';

const loading = ref(false);
const searchFormRef = ref(null);
const vxeTableRef = ref(null);
const tableData = ref([]);
const multipleSelection = ref([]);
const dialogFormRef = ref(null);

const studentOptions = ref([]);
const subjectOptions = ref([]);
const clazzOptions = ref([]);
const teacherOptions = ref([]);

const searchData = reactive({
  studentId: null,
  subjectId: null,
  clazzId: null,
  teacherId: null,
  examName: '',
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

const gradeImportUrl = computed(getGradeImportUrl);
const gradeTemplateUrl = computed(getGradeTemplateUrl);

onMounted(() => {
  fetchData();
  fetchStudentOptions();
  fetchSubjectOptions();
  fetchClazzOptions();
  fetchTeacherOptions();
});

const fetchData = async () => {
  loading.value = true;
  const params = {
    ...searchData,
    pageNum: pagerConfig.currentPage,
    pageSize: pagerConfig.pageSize
  };
  try {
    const res = await getGradeList(params);
    if (res.code === 200) {
      tableData.value = res.data.records || [];
      pagerConfig.total = Number(res.data.total || 0);
    } else {
      ElMessage.error(res.msg || '获取成绩列表失败');
    }
  } catch (error) {
    console.error("获取成绩列表失败:", error);
    ElMessage.error('获取成绩列表失败');
  } finally {
    loading.value = false;
  }
};

const fetchStudentOptions = async () => {
  try {
    const res = await getStudentList({ pageNum: 1, pageSize: 9999 });
    if (res.code === 200) {
      studentOptions.value = res.data?.records.map(s => ({
         value: s.id, 
         label: `${s.studentName} (${s.studentNumber})`,
         currentClazzId: s.currentClazzId
        })) || [];
    } else {
      console.error('Failed to fetch students:', res.msg);
    }
  } catch (error) {
    console.error('Error fetching students:', error);
  }
};

const fetchSubjectOptions = async () => {
  try {
    const res = await listValidSubjects();
    if (res.code === 200) {
      subjectOptions.value = res.data?.map(s => ({ value: s.id, label: s.subjectName })) || [];
    } else {
      console.error('Failed to fetch subjects:', res.msg);
    }
  } catch (error) {
    console.error('Error fetching subjects:', error);
  }
};

const fetchClazzOptions = async () => {
  try {
    const res = await getClassList({ pageNum: 1, pageSize: 9999 });
    if (res.code === 200) {
      clazzOptions.value = res.data?.records.map(c => ({ value: c.id, label: c.className })) || [];
    } else {
      console.error('Failed to fetch classes:', res.msg);
    }
  } catch (error) {
    console.error('Error fetching classes:', error);
  }
};

const fetchTeacherOptions = async () => {
  try {
    const res = await listValidTeachers();
    if (res.code === 200) {
      teacherOptions.value = res.data?.map(t => ({ value: t.id, label: t.teacherName })) || [];
    } else {
      console.error('Failed to fetch teachers:', res.msg);
    }
  } catch (error) {
    console.error('Error fetching teachers:', error);
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
  dialogTitle.value = '新增成绩';
  formData.value = {};
  dialogVisible.value = true;
  dialogFormRef.value?.formRef?.resetFields();
  dialogFormRef.value?.formRef?.clearValidate();
};

const handleEdit = (row) => {
  dialogTitle.value = '修改成绩';
  formData.value = { ...row };
  dialogVisible.value = true;
};

const handleDelete = (row) => {
   ElMessageBox.confirm(`确定要删除这条成绩记录吗 (学生: ${row.studentName}, 科目: ${row.subjectName}, 考试: ${row.examName})？此操作不可恢复！`, '警告', {
       confirmButtonText: '确定删除',
       cancelButtonText: '取消',
       type: 'warning'
   }).then(async () => {
       try {
           const res = await deleteGrade(row.id);
           if (res.code === 200) {
               ElMessage.success('删除成功');
               fetchData();
           } else {
               ElMessage.error(res.msg || '删除失败');
           }
       } catch (error) {
           console.error("删除成绩失败:", error);
           ElMessage.error('删除失败');
       }
   }).catch(() => {
       ElMessage.info('已取消删除');
   });
};

const handleBatchDelete = () => {
   if (multipleSelection.value.length === 0) {
       ElMessage.warning('请至少选择一条成绩记录进行删除');
       return;
   }
   const selectedIds = multipleSelection.value.map(item => item.id);
   ElMessageBox.confirm(`确定要批量删除选中的 ${selectedIds.length} 条成绩记录吗？此操作不可恢复！`, '警告', {
       confirmButtonText: '确定删除',
       cancelButtonText: '取消',
       type: 'warning'
   }).then(async () => {
       try {
           const res = await deleteGradesBatch(selectedIds);
           if (res.code === 200) {
               ElMessage.success('批量删除成功');
               multipleSelection.value = [];
               vxeTableRef.value?.clearCheckboxRow();
               fetchData();
           } else {
               ElMessage.error(res.msg || '批量删除失败');
           }
       } catch (error) {
           console.error("批量删除成绩失败:", error);
           ElMessage.error('批量删除失败');
       }
   }).catch(() => {
       ElMessage.info('已取消批量删除');
   });
};

const handleExport = () => {
   const exportParams = { ...searchData };
   ElMessageBox.confirm('确定导出当前查询结果的成绩数据吗？', '提示', {
       confirmButtonText: '确定导出',
       cancelButtonText: '取消',
       type: 'info'
   }).then(async () => {
       loading.value = true;
       try {
           const response = await exportGrades(exportParams);
           const fileName = getFileNameFromHeaders(response.headers, '成绩数据.xlsx');
           downloadFile(response.data, fileName);
           ElMessage.success('成绩数据导出成功');
       } catch (error) {
           console.error("导出成绩数据失败:", error);
           ElMessage.error('导出成绩数据失败');
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
  ElMessage.success('成绩导入成功');
  fetchData();
};

const handleSave = async (dataFromDialog) => {
  const isUpdate = !!dataFromDialog.id;
  const apiCall = isUpdate ? updateGrade : addGrade;
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

</script>

<style scoped>
.search-wrapper {
  margin-bottom: 10px;
}
.table-toolbar-wrapper {
  margin-bottom: 10px;
}
</style> 