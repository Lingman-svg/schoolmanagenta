<!--
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 23:20:54
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 00:05:30
 * @FilePath: \schoolmanagenta\school-management-frontend\src\views\TeacherManagement.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="searchData">
        <el-form-item prop="teacherName" label="教师姓名">
          <el-input v-model="searchData.teacherName" placeholder="请输入教师姓名" clearable />
        </el-form-item>
        <el-form-item prop="phone" label="联系方式">
          <el-input v-model="searchData.phone" placeholder="请输入联系方式" clearable />
        </el-form-item>
         <el-form-item prop="gender" label="性别">
           <el-select v-model="searchData.gender" placeholder="请选择性别" clearable>
             <el-option label="男" value="男" />
             <el-option label="女" value="女" />
             <el-option label="其他" value="其他" />
           </el-select>
        </el-form-item>
        <el-form-item prop="subjectId" label="所教科目">
           <el-select v-model="searchData.subjectId" placeholder="请选择科目" clearable filterable>
             <el-option 
                v-for="item in subjectOptions" 
                :key="item.id" 
                :label="item.subjectName" 
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
        <vxe-column field="teacherName" title="教师姓名" min-width="120" />
        <vxe-column field="gender" title="性别" width="80" align="center" />
        <vxe-column field="idCard" title="身份证号" min-width="180" />
        <vxe-column field="birthDate" title="出生日期" width="120" align="center" />
        <vxe-column field="age" title="年龄" width="80" align="center" />
        <vxe-column field="phone" title="联系方式" min-width="150" />
        <vxe-column field="address" title="当前住址" min-width="200" show-overflow />
        <vxe-column field="nativePlace" title="户籍" min-width="150" show-overflow />
        <vxe-column field="nation" title="民族" width="100" />
        <vxe-column field="subjectIds" title="所教科目" min-width="150">
           <template #default="{ row }">
             <!-- 根据 subjectIds 查找并显示科目名称 -->
             <span>{{ formatSubjectNames(row.subjectIds) }}</span>
           </template>
        </vxe-column>
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
    <teacher-form-dialog 
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="formData" 
      :subject-list="subjectOptions" 
      @save="handleSave"
      ref="dialogFormRef" 
    />

    <!-- 导入弹窗 -->
    <import-dialog
       v-model:visible="importDialogVisible"
       title="导入教师数据"
       :upload-url="teacherImportUrl" 
       :template-url="teacherTemplateUrl" 
       @success="handleImportSuccess"
    />

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox, ElCard, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton } from 'element-plus';
import { Search, Refresh, Plus, Delete, Download, Upload, Edit } from '@element-plus/icons-vue'; // 按需引入图标
// import { VXETable } from 'vxe-table'; // 用于可能的弹窗等交互 - Removed unused import
import TeacherFormDialog from '@/components/TeacherFormDialog.vue'; // 导入弹窗组件
import ImportDialog from '@/components/ImportDialog.vue'; // 导入 ImportDialog
import {
  getTeacherList,
  getTeacherInfo,
  addTeacher,
  updateTeacher,
  deleteTeacher,
  deleteTeachersBatch,
  // exportTeacherDataUrl, // 导入函数暂不使用，按钮触发弹窗 - Removed unused import
  // importTeacherData, // 导入函数暂不使用，按钮触发弹窗 - Removed unused import
  // ... API imports ...
} from '@/api/teacher';
import request from '@/utils/request'; // 假设 request 实例能处理 blob
import { listValidSubjects } from '@/api/subject'; // 修改导入的函数名

const loading = ref(false);
const searchFormRef = ref(null); // 搜索表单引用
const vxeTableRef = ref(null); // VxeTable 引用
const tableData = ref([]);
const multipleSelection = ref([]); // 表格多选中的数据
const dialogFormRef = ref(null); // 弹窗组件引用

// 搜索条件
const searchData = reactive({
  teacherName: '',
  phone: '',
  gender: '',
  subjectId: null, // TODO: 关联科目搜索
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

// 弹窗相关状态
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formData = ref({}); // 使用 ref 包装对象，方便替换
const subjectOptions = ref([]); // TODO: 获取科目列表
const importDialogVisible = ref(false); // 控制导入弹窗显示

// --- 计算属性，用于拼接 URL ---
const baseUrl = import.meta.env.VITE_APP_BASE_API; // 从环境变量获取基础 API 路径
const teacherImportUrl = computed(() => `${baseUrl}/teacher/import`);
// 假设模板下载也是一个后端接口，或者是一个静态文件路径
const teacherTemplateUrl = computed(() => `${baseUrl}/teacher/template`); // 示例 URL

// --- CRUD 方法 ---

// 获取列表数据
const fetchData = async () => {
  loading.value = true;
  try {
    // 合并搜索条件和分页参数
    const params = { ...searchData, pageNum: pagerConfig.currentPage, pageSize: pagerConfig.pageSize };
    const res = await getTeacherList(params);
    if (res.code === 200) {
      tableData.value = res.data.records;
      pagerConfig.total = Number(res.data.total);
    } else {
      ElMessage.error(res.msg || '获取数据失败');
    }
  } catch (error) {
    console.error("获取教师列表失败:", error);
    ElMessage.error('获取教师列表失败');
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  pagerConfig.currentPage = 1; // 重置到第一页
  fetchData();
};

// 重置搜索表单
const resetSearch = () => {
  searchFormRef.value?.resetFields(); // 重置表单
  // 手动重置 pageNum 和 pageSize 可能在 searchData 中的字段
  searchData.pageNum = 1;
  searchData.pageSize = pagerConfig.pageSize; // 或重置为默认值 10
  // searchData.subjectId = null; // 如果有科目搜索
  handleSearch(); // 重新查询
};

// 分页变化处理
const handlePageChange = ({ currentPage, pageSize }) => {
  pagerConfig.currentPage = currentPage;
  pagerConfig.pageSize = pageSize;
  // 更新 searchData 中的分页信息（如果需要）
  searchData.pageNum = currentPage;
  searchData.pageSize = pageSize;
  fetchData();
};

// 表格多选变化处理
const handleCheckboxChange = ({ records }) => {
  multipleSelection.value = records;
};
const handleCheckboxAllChange = ({ records }) => {
  multipleSelection.value = records;
};

// --- 按钮事件处理 (占位符) ---
const handleAdd = () => {
  dialogTitle.value = '新增教师';
  formData.value = { subjectIds: [] }; // 重置表单数据，确保 subjectIds 是数组
  dialogVisible.value = true;
};

const handleEdit = async (row) => {
  dialogTitle.value = '修改教师';
  try {
     // 显示 loading
     // TODO: 可以给弹窗加 loading 状态，或者全局 loading
    const res = await getTeacherInfo(row.id);
    if (res.code === 200) {
      formData.value = res.data; // 填充表单
      // 确保 subjectIds 是数组，如果后端返回 null 则设置为空数组
      if (!formData.value.subjectIds) {
          formData.value.subjectIds = [];
      }
      dialogVisible.value = true;
    } else {
      ElMessage.error(res.msg || '获取教师详情失败');
    }
  } catch(error) {
     console.error("获取教师详情失败:", error);
     ElMessage.error('获取教师详情失败');
  } finally {
     // 关闭 loading
  }
};

const handleDelete = (row) => {
  console.log('handleDelete', row);
  ElMessageBox.confirm(`确定要删除教师 " ${row.teacherName} " 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteTeacher(row.id);
      if (res.code === 200) {
        ElMessage.success('删除成功');
        fetchData(); // 刷新列表
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    } catch (error) {
       console.error("删除教师失败:", error);
       ElMessage.error('删除教师失败');
    }
  }).catch(() => {
    // 用户取消
  });
};

const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请至少选择一条数据');
    return;
  }
  const selectedNames = multipleSelection.value.map(item => item.teacherName).join(', ');
  const selectedIds = multipleSelection.value.map(item => item.id);

  ElMessageBox.confirm(`确定要批量删除教师 " ${selectedNames} " 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
     try {
      const res = await deleteTeachersBatch(selectedIds);
      if (res.code === 200) {
        ElMessage.success('批量删除成功');
        multipleSelection.value = []; // 清空选择
        vxeTableRef.value?.clearCheckboxRow(); // 清空 VxeTable 勾选状态
        fetchData(); // 刷新列表
      } else {
        ElMessage.error(res.msg || '批量删除失败');
      }
    } catch (error) {
       console.error("批量删除教师失败:", error);
       ElMessage.error('批量删除教师失败');
    }
  }).catch(() => {
    // 用户取消
  });
};

const handleExport = () => {
  console.log('handleExport');
  // 1. 获取当前查询条件（不含分页）
  // const { pageNum: _, pageSize: __, ...exportParams } = searchData; // Linter 不喜欢忽略的变量
  // 创建一个不包含分页参数的新对象
  const exportParams = { 
      teacherName: searchData.teacherName,
      phone: searchData.phone,
      gender: searchData.gender,
      subjectId: searchData.subjectId
      // 添加其他需要导出的查询条件
  };

  ElMessageBox.confirm('确定导出当前查询结果吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 显示 loading 状态
      loading.value = true; 
      
      // 2. 调用 axios 发送 POST 请求获取 blob 数据
      const response = await request({
        url: '/teacher/export', // 使用相对路径，baseUrl 已在 request 实例中配置
        method: 'post',
        data: exportParams, // 查询条件作为 body
        responseType: 'blob' // 重要：设置响应类型为 blob
      });

      // 3. 处理 Blob 数据并触发下载
      // 从响应头获取文件名 (需要后端设置 Content-Disposition)
      const contentDisposition = response.headers['content-disposition'];
      let fileName = '教师数据.xlsx'; // 默认文件名
      if (contentDisposition) {
        // 尝试匹配 filename*=utf-8'' 或 filename=
        const fileNameMatch = contentDisposition.match(/filename\*?=utf-8''([^;]+)|filename="([^;]+)"/i);
        if (fileNameMatch) {
            // 优先使用 filename*=utf-8'' (解码)，否则使用 filename=
            fileName = decodeURIComponent(fileNameMatch[1] || fileNameMatch[2]);
        } 
      }

      // 创建下载链接
      const blob = new Blob([response.data], { type: response.headers['content-type'] }); // 使用后端返回的 content-type
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
      // 尝试解析 Blob 中的错误信息
      if (error.response && error.response.data instanceof Blob) {
          const reader = new FileReader();
          reader.onload = function() {
              try {
                  const errData = JSON.parse(reader.result);
                  ElMessage.error(errData.msg || '导出失败');
              } catch /* _parseError */ { // 移除未使用的变量名
                   ElMessage.error('导出失败，无法解析错误信息');
              }
          };
          reader.onerror = function() {
              ElMessage.error('导出失败，无法读取错误信息');
          };
          reader.readAsText(error.response.data);
      } else {
         ElMessage.error('导出失败');
      }
    } finally {
        loading.value = false; // 关闭 loading 状态
    }
  }).catch(() => {});
};

const handleImport = () => {
  importDialogVisible.value = true;
};

// 处理导入成功后的回调
const handleImportSuccess = () => {
   fetchData(); // 导入成功后刷新列表
};

// --- 获取科目列表 ---
const fetchSubjectOptions = async () => {
  try {
    const res = await listValidSubjects(); // 修改调用的函数名
    if (res.code === 200) {
      subjectOptions.value = res.data; // 使用 API 返回的数据
    } else {
       ElMessage.error(res.msg || '获取科目列表失败');
    }
    // console.log('TODO: 获取科目列表');
    // 移除临时数据
    // subjectOptions.value = [
    //     { id: 1, subjectName: '语文' },
    //     { id: 2, subjectName: '数学' },
    //     { id: 3, subjectName: '英语' }
    // ];
  } catch (error) {
      console.error("获取科目列表失败:", error);
      ElMessage.error('获取科目列表失败');
  }
};

// --- 生命周期钩子 ---
onMounted(() => {
  fetchData(); // 组件挂载后加载首屏数据
  fetchSubjectOptions(); // 加载科目选项
});

// 处理弹窗保存
const handleSave = async (dataFromDialog) => {
  let loadingStopped = false; // 标记是否已停止 loading
  try {
    const isUpdate = !!dataFromDialog.id;
    const apiCall = isUpdate ? updateTeacher : addTeacher;
    const res = await apiCall(dataFromDialog);
    if (res.code === 200) {
      ElMessage.success(isUpdate ? '修改成功' : '新增成功');
      dialogVisible.value = false; // 关闭弹窗
      dialogFormRef.value?.stopLoading(); // *** 在成功时也停止 loading ***
      loadingStopped = true;
      fetchData(); // 刷新列表
    } else {
      ElMessage.error(res.msg || (isUpdate ? '修改失败' : '新增失败'));
      // 失败时停止 loading (已存在)
      dialogFormRef.value?.stopLoading(); 
      loadingStopped = true;
    }
  } catch (error) {
    console.error("保存教师失败:", error);
    ElMessage.error('保存教师失败');
    // 异常时停止 loading (已存在)
    dialogFormRef.value?.stopLoading();
    loadingStopped = true;
  } finally {
      // 添加 finally 块确保 loading 一定停止 (以防万一)
      if (!loadingStopped) {
          dialogFormRef.value?.stopLoading();
      }
  }
};

// --- 格式化函数 ---
// 根据科目 ID 数组格式化为科目名称字符串
const formatSubjectNames = (subjectIds) => {
  if (!subjectIds || subjectIds.length === 0) {
    return '-';
  }
  // 从 subjectOptions 中查找名称
  const names = subjectIds.map(id => {
    const subject = subjectOptions.value.find(option => option.id === id);
    return subject ? subject.subjectName : `ID(${id})`; // 如果找不到 ID，显示 ID 本身
  });
  return names.join(', ');
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