<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="系统模块" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入系统模块"
          clearable
          style="width: 240px;"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人员" prop="operName">
        <el-input
          v-model="queryParams.operName"
          placeholder="请输入操作人员"
          clearable
          style="width: 240px;"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="businessType">
        <el-select
          v-model="queryParams.businessType"
          placeholder="操作类型"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in businessTypeOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="操作状态"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in statusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间">
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 380px;"
          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
     <vxe-table
      ref="xTableRef"
      :data="logList"
      :loading="loading"
      border
      stripe
      height="500" 
      :row-config="{isHover: true}"
      :column-config="{resizable: true}"
      :tooltip-config="{showAll: true}"
      :sort-config="{trigger: 'cell'}"
      :filter-config="{showIcon: false}"
    >
      <vxe-column type="seq" width="60" title="#"></vxe-column>
      <vxe-column field="id" title="日志编号" width="100" sortable></vxe-column>
      <vxe-column field="title" title="系统模块" width="150"></vxe-column>
      <vxe-column field="businessType" title="操作类型" width="100">
         <template #default="{ row }">
            <span>{{ formatBusinessType(row.businessType) }}</span>
        </template>
      </vxe-column>
      <vxe-column field="requestMethod" title="请求方式" width="100"></vxe-column>
      <vxe-column field="operName" title="操作人员" width="120"></vxe-column>
      <vxe-column field="operIp" title="主机IP" width="130"></vxe-column>
      <!-- <vxe-column field="operLocation" title="操作地点" width="180"></vxe-column> -->
      <vxe-column field="status" title="操作状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ formatStatus(row.status) }}</el-tag>
        </template>
      </vxe-column>
      <vxe-column field="operTime" title="操作日期" width="180" sortable></vxe-column>
       <vxe-column field="costTime" title="消耗时间(ms)" width="150" sortable></vxe-column>
      <vxe-column title="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="View" @click="handleView(row)">详细</el-button>
        </template>
      </vxe-column>
    </vxe-table>

    <!-- 分页 -->
    <vxe-pager
      v-model:current-page="queryParams.pageNum"
      v-model:page-size="queryParams.pageSize"
      :total="total"
      :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
      @page-change="handlePageChange"
      style="margin-top: 10px;"
    />

    <!-- 操作日志详细 -->
    <el-dialog title="操作日志详细" v-model="openView" width="700px" append-to-body>
      <el-form :model="form" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="操作模块：">{{ form.title }} / {{ formatBusinessType(form.businessType) }}</el-form-item>
            <el-form-item label="登录信息：">{{ form.operName }} / {{ form.operIp }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求地址：">{{ form.operUrl }}</el-form-item>
            <el-form-item label="请求方式：">{{ form.requestMethod }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作方法：">{{ form.method }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求参数：">
               <el-input type="textarea" :rows="5" :value="formatJson(form.operParam)" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回参数：">
                 <el-input type="textarea" :rows="5" :value="formatJson(form.jsonResult)" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作状态：">
              <div v-if="form.status === 0">正常</div>
              <div v-else-if="form.status === 1">失败</div>
            </el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="消耗时间：">{{ form.costTime }}毫秒</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作时间：">{{ form.operTime }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="异常信息：" v-if="form.status === 1">
                 <el-input type="textarea" :rows="5" :value="form.errorMsg" readonly />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="openView = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="LogManagement">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh, View } from '@element-plus/icons-vue';
import { listLog } from '@/api/system/log';

const queryFormRef = ref(null);
const xTableRef = ref(null);

const loading = ref(true);
const showSearch = ref(true); // 控制搜索表单显示
const logList = ref([]);
const total = ref(0);
const dateRange = ref([]); // 日期范围
const openView = ref(false); // 是否显示详细弹窗
const form = ref({}); // 详细表单数据

// 业务类型选项 (可以从字典或常量获取)
const businessTypeOptions = ref([
  { value: 0, label: '其它' },
  { value: 1, label: '新增' },
  { value: 2, label: '修改' },
  { value: 3, label: '删除' },
  { value: 4, label: '授权' },
  { value: 5, label: '导出' },
  { value: 6, label: '导入' },
  { value: 7, label: '强退' },
  { value: 8, label: '清空' },
  { value: 9, label: '查询' },
  { value: 10, label: '登录' },
  { value: 11, label: '登出' },
]);

// 状态选项
const statusOptions = ref([
  { value: 0, label: '正常' },
  { value: 1, label: '异常' },
]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: undefined,
  operName: undefined,
  businessType: undefined,
  status: undefined,
  beginTime: undefined,
  endTime: undefined
});

// 获取日志列表
const getList = async () => {
  loading.value = true;
  // 处理日期范围
  queryParams.beginTime = dateRange.value?.[0];
  queryParams.endTime = dateRange.value?.[1];

  try {
    const response = await listLog(queryParams);
    if (response && response.data) {
        logList.value = response.data.records || []; // 适配 MybatisPlus IPage
        total.value = response.data.total || 0;
    } else {
        logList.value = [];
        total.value = 0;
        ElMessage.warning(response?.msg || '未能获取到日志数据');
    }
  } catch (error) {
    console.error("获取日志列表失败:", error);
    ElMessage.error('获取日志列表失败');
    logList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置按钮操作
const resetQuery = () => {
  dateRange.value = [];
  queryFormRef.value?.resetFields(); // 重置表单
  handleQuery(); // 重新查询
};

// 分页变更处理
const handlePageChange = ({ currentPage, pageSize }) => {
  queryParams.pageNum = currentPage;
  queryParams.pageSize = pageSize;
  getList();
};

// 格式化业务类型
const formatBusinessType = (type) => {
  const found = businessTypeOptions.value.find(opt => opt.value === type);
  return found ? found.label : '未知';
};

// 格式化状态
const formatStatus = (status) => {
  const found = statusOptions.value.find(opt => opt.value === status);
  return found ? found.label : '未知';
};

// 查看详细按钮操作
const handleView = (row) => {
  form.value = { ...row }; // 复制行数据到表单
  openView.value = true;
};

// 格式化 JSON 字符串
const formatJson = (jsonString) => {
    if (!jsonString) return '';
    try {
        const obj = JSON.parse(jsonString);
        return JSON.stringify(obj, null, 2); // 格式化输出，缩进2个空格
    } catch (e) {
        return jsonString; // 如果解析失败，返回原始字符串
    }
};

// 组件挂载后加载数据
onMounted(() => {
  getList();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.el-form-item {
    margin-bottom: 18px; /* 调整表单项间距 */
}
</style> 