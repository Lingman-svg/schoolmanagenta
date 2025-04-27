<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="phoneNumber">
        <el-input
          v-model="queryParams.phoneNumber"
          placeholder="请输入手机号码"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="isValid">
        <el-select
          v-model="queryParams.isValid"
          placeholder="用户状态"
          clearable
          style="width: 240px"
        >
          <el-option label="正常" :value="0" />
          <el-option label="停用" :value="1" />
        </el-select>
      </el-form-item>
      <!-- TODO: 添加日期范围查询 -->
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple" 
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <!-- TODO: 添加导入导出按钮 -->
       <el-col :span="1.5">
        <el-tooltip content="隐藏搜索" placement="top">
          <el-button circle icon="Search" @click="showSearch = !showSearch" />
        </el-tooltip>
         <el-tooltip content="刷新" placement="top">
           <el-button circle icon="Refresh" @click="getList" />
         </el-tooltip>
       </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户编号" align="center" prop="id" />
      <el-table-column label="用户名称" align="center" prop="userName" :show-overflow-tooltip="true" />
      <el-table-column label="用户昵称" align="center" prop="nickName" :show-overflow-tooltip="true" />
      <el-table-column label="邮箱" align="center" prop="email" :show-overflow-tooltip="true" />
      <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
      <el-table-column label="状态" align="center" prop="isValid">
        <template #default="scope">
          <el-switch
            v-model="scope.row.isValid"
            :active-value="0" 
            :inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          <el-dropdown @command="(command) => handleCommand(command, scope.row)">
             <el-button link type="primary" icon="More">更多</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="handleResetPwd" icon="Key">重置密码</el-dropdown-item>
                <!-- <el-dropdown-item command="handleAuthRole" icon="CircleCheck">分配角色</el-dropdown-item> -->
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
     <el-pagination
       v-show="total > 0"
       v-model:page="queryParams.pageNum"
       v-model:limit="queryParams.pageSize"
       :page-sizes="[10, 20, 50, 100]"
       :background="true"
       layout="total, sizes, prev, pager, next, jumper"
       :total="total"
       @size-change="getList"
       @current-change="getList"
       class="mt-4 justify-end"
     />

    <!-- 添加或修改用户对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" append-to-body>
      <el-form :model="form" :rules="rules" ref="userFormRef" label-width="80px">
         <el-row>
           <el-col :span="12">
             <el-form-item label="用户昵称" prop="nickName">
               <el-input v-model="form.nickName" placeholder="请输入用户昵称" />
             </el-form-item>
           </el-col>
           <el-col :span="12">
             <el-form-item label="手机号码" prop="phonenumber">
               <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
             </el-form-item>
           </el-col>
         </el-row>
         <el-row>
           <el-col :span="12">
             <el-form-item label="邮箱" prop="email">
               <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
             </el-form-item>
           </el-col>
           <el-col :span="12">
            <el-form-item v-if="!form.id" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" />
            </el-form-item>
           </el-col>
          </el-row>
         <el-row>
           <el-col :span="12">
             <el-form-item v-if="!form.id" label="用户密码" prop="password">
               <el-input v-model="form.password" placeholder="请输入用户密码" type="password" show-password />
             </el-form-item>
           </el-col>
            <el-col :span="12">
              <el-form-item label="用户性别" prop="sex">
                <el-select v-model="form.sex" placeholder="请选择">
                  <el-option label="男" value="0"></el-option>
                  <el-option label="女" value="1"></el-option>
                  <el-option label="未知" value="2"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
         </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="isValid">
              <el-radio-group v-model="form.isValid">
                <el-radio :label="0">正常</el-radio>
                <el-radio :label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
             <el-form-item label="角色" prop="roleIds">
                 <!-- TODO: 实现角色选择器 (例如 el-select multiple 或 el-transfer) -->
                 <el-select v-model="form.roleIds" multiple placeholder="请选择角色">
                     <el-option
                         v-for="item in roleOptions" 
                         :key="item.id"
                         :label="item.roleName"
                         :value="item.id"
                     ></el-option>
                 </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
           <el-col :span="24">
             <el-form-item label="备注" prop="remark">
               <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
             </el-form-item>
           </el-col>
         </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="UserManagement">
import { ref, reactive, onMounted } from 'vue';
import { listUser, getUser, delUser, addUser, updateUser, resetUserPwd, changeUserStatus } from '@/api/user';
import { optionselect as listRolesOptions } from '@/api/role'; // 导入并重命名，避免与变量名冲突
import { ElMessage, ElMessageBox } from 'element-plus';
// import dayjs from 'dayjs'; // 移除未使用的导入

// --- 响应式状态定义 ---
const loading = ref(true); // 表格加载状态
const ids = ref([]); // 选中数组
const single = ref(true); // 非单个禁用
const multiple = ref(true); // 非多个禁用
const total = ref(0); // 总条数
const userList = ref([]); // 用户表格数据
const showSearch = ref(true); // 显示搜索条件

// 弹窗状态
const dialog = reactive({
  visible: false,
  title: ''
});

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: undefined,
  phoneNumber: undefined,
  isValid: undefined
});

// 表单参数
const form = ref({});
// 角色选项 (需要从API加载)
const roleOptions = ref([]); 

// 表单校验规则
const rules = reactive({
  userName: [
    { required: true, message: "用户名称不能为空", trigger: "blur" },
    { min: 3, max: 20, message: '用户名称长度必须介于 3 和 20 之间', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: "用户昵称不能为空", trigger: "blur" }
  ],
  password: [
    { required: true, message: "用户密码不能为空", trigger: "blur" },
    { min: 6, max: 20, message: '用户密码长度必须介于 6 和 20 之间', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phonenumber: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
});

// --- Refs --- 
const queryFormRef = ref(null);
const userFormRef = ref(null);

// --- 方法定义 ---

/** 查询用户列表 */
function getList() {
  loading.value = true;
  listUser(queryParams).then(response => {
    // 后端返回 R<IPage<UserVo>>
    // response.code === 200 (R.SUCCESS_CODE)
    // response.data 是 IPage 对象
    // response.data.records 是列表数据
    // response.data.total 是总数
    if (response && response.code === 200 && response.data) {
        const pageData = response.data;
        userList.value = pageData.records || []; // 添加空值保护
        total.value = pageData.total || 0; // 添加空值保护
    } else {
        userList.value = [];
        total.value = 0;
        // 使用 R 类返回的 msg
        ElMessage.error(response?.msg || '查询用户列表失败');
    }
    loading.value = false;
  }).catch((error) => { // 添加 error 参数
      console.error("Error fetching user list:", error); // 添加日志
      loading.value = false;
      userList.value = [];
      total.value = 0;
      ElMessage.error('查询用户列表时发生错误');
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  resetForm();
  loadRoles(); // 加载角色选项
  dialog.visible = true;
  dialog.title = "添加用户";
  form.value.password = '123456'; // 默认密码
  form.value.isValid = 0; // 默认状态
}

/** 修改按钮操作 */
function handleUpdate(row) {
  resetForm();
  const userId = row.id || ids.value[0];
  loadRoles(); // 加载角色选项 
  getUser(userId).then(response => {
    // 假设 response.data 是 UserVo
    if (response && response.data) {
        form.value = response.data;
        // UserVo 返回了 roleIds 数组
        dialog.visible = true;
        dialog.title = "修改用户";
        form.value.password = undefined; // 修改时不显示密码
    } else {
         ElMessage.error(response?.msg || '获取用户信息失败');
    }
  });
}

/** 提交按钮 */
function submitForm() {
  userFormRef.value?.validate(valid => {
    if (valid) {
      if (form.value.id) {
        updateUser(form.value).then(() => {
           ElMessage.success("修改成功");
           dialog.visible = false;
           getList();
        });
      } else {
        addUser(form.value).then(() => {
           ElMessage.success("新增成功");
           dialog.visible = false;
           getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const userIds = row.id ? [row.id] : ids.value;
  ElMessageBox.confirm(
    `是否确认删除用户编号为"${userIds.join(',')}"的数据项?`,
    "警告",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  ).then(() => {
    return delUser(userIds); 
  }).then(() => {
    getList();
    ElMessage.success("删除成功");
  }).catch(() => {});
}

/** 状态修改 */
function handleStatusChange(row) {
  let text = row.isValid === 0 ? "启用" : "停用";
  ElMessageBox.confirm(`确认要"${text}"${row.userName}用户吗?`, "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }).then(() => {
      return changeUserStatus(row.id, row.isValid);
    }).then(() => {
      ElMessage.success(text + "成功");
    }).catch(() => {
      // 状态取反
      row.isValid = row.isValid === 0 ? 1 : 0;
    });
}

/** 重置密码按钮操作 */
function handleResetPwd(row) {
     ElMessageBox.prompt('请输入"' + row.userName + '"的新密码', "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      closeOnClickModal: false,
      inputPattern: /^.{6,20}$/, // 密码长度6-20位
      inputErrorMessage: "用户密码长度必须介于 6 和 20 之间",
    }).then(({ value }) => {
       resetUserPwd(row.id, value).then(() => {
           ElMessage.success("密码重置成功，新密码是：" + value);
       }).catch(() => {
           ElMessage.error("密码重置失败");
       });
    }).catch(() => {
        ElMessage.info("已取消重置密码");
    });
}

/** 更多操作命令处理 */
function handleCommand(command, row) {
  switch (command) {
    case "handleResetPwd":
      handleResetPwd(row);
      break;
    // case "handleAuthRole":
      // handleAuthRole(row);
      // break;
    default:
      break;
  }
}

/** 表单重置 */
function resetForm() {
  form.value = {
    id: undefined,
    userName: undefined,
    nickName: undefined,
    password: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: undefined,
    isValid: 0,
    roleIds: [],
    remark: undefined
  };
  userFormRef.value?.resetFields();
}

/** 取消按钮 */
function cancel() {
  dialog.visible = false;
  resetForm();
}

// --- 生命周期钩子 ---
onMounted(() => {
  getList();
  loadRoles(); // 加载角色列表选项
});

// --- 辅助函数 (如果需要) ---
/** 加载角色列表 */
function loadRoles() {
  listRolesOptions().then(response => {
      // 假设 response.data 是角色列表 [{id: 1, roleName: 'Admin'}, ...]
      if (response && response.data) {
          roleOptions.value = response.data;
      } else {
          roleOptions.value = [];
          ElMessage.error("加载角色列表失败");
      }
  }).catch(() => {
      roleOptions.value = [];
      ElMessage.error("加载角色列表失败");
  });
}

/** 格式化日期时间 */
function formatDateTime(time) {
    if (!time) {
        return '';
    }
    // return dayjs(time).format('YYYY-MM-DD HH:mm:ss'); // 使用 dayjs
    // 或者使用简单的内建方法 (兼容性可能稍差)
    try {
      return new Date(time).toLocaleString();
    } catch (error) { // e -> error，避免与事件变量混淆，并移除未使用警告 (虽然这里也没用上)
      console.error("Error formatting date:", time, error); // 添加日志
      return time; // 格式化失败返回原始值
    }
}

</script>

<style scoped>
/* 添加一些样式 */
.mb8 {
  margin-bottom: 8px;
}
.mt-4 {
    margin-top: 1rem;
}
.justify-end {
    justify-content: flex-end;
}
</style> 