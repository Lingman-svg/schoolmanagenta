<template>
  <div class="app-container system-config-management-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true">
        <el-form-item label="参数名称" prop="configName">
          <el-input
            v-model="queryParams.configName"
            placeholder="请输入参数名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input
            v-model="queryParams.configKey"
            placeholder="请输入参数键名"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
          <el-select v-model="queryParams.configType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
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
         <!-- 系统配置一般不进行批量删除和导入导出 -->
         <!-- <el-col :span="1.5">
           <el-button type="danger" plain @click="handleBatchDelete" :disabled="selectedRows.length === 0" :icon="Delete">批量删除</el-button>
         </el-col> -->
       </el-row>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="id"
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <!-- 系统配置一般不需要复选框 -->
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column label="参数主键" align="center" prop="id" width="100"/>
        <el-table-column label="参数名称" align="center" prop="configName" :show-overflow-tooltip="true" />
        <el-table-column label="参数键名" align="center" prop="configKey" :show-overflow-tooltip="true" />
        <el-table-column label="参数键值" align="center" prop="configValue" :show-overflow-tooltip="true"/>
        <el-table-column label="系统内置" align="center" prop="configType" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.configType === 1 ? 'warning' : 'success'">{{ scope.row.configType === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ scope.row.createTime }}</span> <!-- 可能需要格式化 -->
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180" fixed="right">
          <template #default="scope">
            <el-button
              link
              type="primary"
              :icon="Edit"
              @click="handleUpdate(scope.row)"
            >修改</el-button>
            <el-button
              link
              type="danger"
              :icon="Delete"
              @click="handleDelete(scope.row)"
              :disabled="scope.row.configType === 1"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 系统配置一般数据量不大，暂时不加分页 -->
      <!-- <vxe-pager
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
        @page-change="handlePageChange"
        class="table-pager"
      >
      </vxe-pager> -->
    </el-card>

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog
        :title="dialog.title"
        v-model="dialog.visible"
        width="600px"
        append-to-body
        :close-on-click-modal="false"
        @close="resetForm"
      >
      <el-form :model="dialog.formData" :rules="rules" ref="configFormRef" label-width="100px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="dialog.formData.configName" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="dialog.formData.configKey" placeholder="请输入参数键名" :disabled="dialog.isEdit && dialog.formData.configType === 1"/>
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="dialog.formData.configValue" type="textarea" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
           <el-radio-group v-model="dialog.formData.configType" :disabled="dialog.isEdit && dialog.formData.configType === 1">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
           <p style="font-size: 12px; color: #E6A23C; line-height: 1.5;">注意：系统内置参数创建后不可更改键名和类型，且不允许删除。</p>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dialog.formData.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="SystemConfigManagement">
import { ref, reactive, onMounted } from 'vue';
import { listConfigs, getConfigById, addConfig, updateConfig, deleteConfig } from '@/api/system';
import { ElMessageBox, ElMessage } from 'element-plus';
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue'; // 引入图标

// --- refs ---
const queryFormRef = ref(null); // 查询表单引用
const configFormRef = ref(null); // 新增/修改表单引用

// --- state ---
const loading = ref(true);
const tableData = ref([]); // 表格数据
const selectedRows = ref([]); // 存储选中的行数据 (虽然本页面不用，保持一致性)

// 查询参数 (不需要分页)
const queryParams = reactive({
  configName: undefined,
  configKey: undefined,
  configType: undefined
});

// 对话框初始表单数据结构
const initialFormData = () => ({
  id: undefined,
  configName: '',
  configKey: '',
  configValue: '',
  configType: 0, // 默认为非系统内置
  remark: ''
});

// 对话框状态 (新增/修改)
const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false,
  formData: initialFormData() // 初始化 formData
});

// 表单校验规则
const rules = reactive({
  configName: [
    { required: true, message: "参数名称不能为空", trigger: "blur" }
  ],
  configKey: [
    { required: true, message: "参数键名不能为空", trigger: "blur" }
    // 可以在这里添加更复杂的校验，例如键名格式
  ],
  configValue: [
    { required: true, message: "参数键值不能为空", trigger: "blur" }
  ],
   configType: [
    { required: true, message: "请选择是否系统内置", trigger: "change" }
  ]
});

// --- methods ---

// 获取列表数据 (无分页)
const getList = async () => {
  loading.value = true;
  try {
    // 注意：后端 list() 方法目前没有实现分页和条件查询
    // 如果后端实现了条件查询，应传递 queryParams
    const response = await listConfigs({
        configName: queryParams.configName || undefined, // 传 undefined 以便后端忽略空字符串
        configKey: queryParams.configKey || undefined,
        configType: queryParams.configType
    }); // 假设 API 支持对象形式的 params
    tableData.value = response.data || []; // 假设 R.data 是列表
  } catch (error) {
      console.error("Failed to fetch config list:", error);
      ElMessage.error("获取配置列表失败");
  } finally {
      loading.value = false;
  }
}

// 搜索按钮点击
const handleQuery = () => {
  // 后端接口需要支持按条件查询
  getList();
  // ElMessage.info('后端暂未实现按条件搜索，已显示所有数据。'); // 根据后端实现情况决定是否保留
}

// 重置按钮点击
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  getList();
}

// 表格行选中状态变化 (虽然本页面不用，保持一致性)
const handleSelectionChange = ({ records }) => {
  selectedRows.value = records;
};

// 重置表单
const resetForm = () => {
  dialog.formData = initialFormData(); // 使用初始函数重置
  configFormRef.value?.resetFields(); // 清除校验状态
}

// 新增按钮操作
const handleAdd = () => {
  resetForm();
  dialog.isEdit = false;
  dialog.title = "添加参数配置";
  dialog.visible = true;
}

// 修改按钮操作
const handleUpdate = async (row) => {
  resetForm();
  dialog.isEdit = true; // 标记为编辑状态
  const configId = row.id;
  try {
    const response = await getConfigById(configId);
    dialog.formData = response.data; // 假设 R.data 是配置对象
    dialog.title = "修改参数配置";
    dialog.visible = true;
  } catch (error) {
      console.error("Failed to fetch config details:", error);
      ElMessage.error("获取配置详情失败");
  }
}

// 删除按钮操作
const handleDelete = (row) => {
   if (row.configType === 1) {
      ElMessage.warning('系统内置参数不允许删除');
      return;
    }
  const configId = row.id;
  const configName = row.configName;
  ElMessageBox.confirm(`是否确认删除参数名称为 "${configName}" 的数据项？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async () => {
    try {
      await deleteConfig(configId);
      getList(); // 重新加载列表
      ElMessage.success("删除成功");
    } catch (error) {
        console.error("Failed to delete config:", error);
        // 后端返回的错误信息通常在 error.response.data.msg 或 error.message
        ElMessage.error(error.response?.data?.msg || error.message || "删除失败");
    }
  }).catch(() => {
      // 用户取消操作
       ElMessage.info("已取消删除");
  });
}

// 提交按钮 (新增/修改)
const submitForm = () => {
  configFormRef.value?.validate(async (valid) => {
    if (valid) {
      const action = dialog.isEdit ? updateConfig : addConfig;
      const successMessage = dialog.isEdit ? "修改成功" : "新增成功";
      const errorMessage = dialog.isEdit ? "修改失败" : "新增失败";

      try {
        await action(dialog.formData);
        ElMessage.success(successMessage);
        dialog.visible = false;
        getList(); // 重新加载列表
      } catch (error) {
          console.error(`Failed to ${dialog.isEdit ? 'update' : 'add'} config:`, error);
          ElMessage.error(error.response?.data?.msg || error.message || errorMessage);
      }
    } else {
      console.log('error submit!!');
      return false;
    }
  });
}

// 组件挂载后加载数据
onMounted(() => {
  getList();
});

</script>

<style scoped>
/* 可以添加特定样式，或全局引入 card 样式 */
.search-card, .operate-card, .table-card {
  margin-bottom: 10px;
}
/* .table-pager {
  margin-top: 10px;
} */
.dialog-footer {
  text-align: right;
}
</style> 