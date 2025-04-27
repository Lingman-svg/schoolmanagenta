<template>
  <div class="app-container role-management-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input
            v-model="queryParams.roleName"
            placeholder="请输入角色名称"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input
            v-model="queryParams.roleKey"
            placeholder="请输入权限字符"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="isValid">
          <el-select
            v-model="queryParams.isValid"
            placeholder="角色状态"
            clearable
            style="width: 240px"
          >
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            style="width: 240px"
            value-format="YYYY-MM-DD"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          ></el-date-picker>
        </el-form-item> -->
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="operate-card" shadow="never">
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
            :disabled="!selectedRows.length"
            @click="handleDelete"
          >删除</el-button>
        </el-col>
        <!-- <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
          >导出</el-button>
        </el-col> -->
      </el-row>
    </el-card>

    <!-- 表格区域 -->
     <el-card class="table-card" shadow="never">
        <vxe-table
            ref="xTableRef" 
            :data="roleList"
            :loading="loading"
            border
            stripe
            :row-config="{ isHover: true }"
            :checkbox-config="{ checkField: 'checked', trigger: 'row', highlight: true }"
            @checkbox-change="handleSelectionChange"
            @checkbox-all="handleSelectionChange"
        >
            <vxe-column type="checkbox" width="60"></vxe-column>
            <vxe-column field="id" title="角色编号" width="120" sortable></vxe-column>
            <vxe-column field="roleName" title="角色名称" min-width="150" sortable></vxe-column>
            <vxe-column field="roleKey" title="权限字符" min-width="150" sortable></vxe-column>
            <vxe-column field="roleSort" title="显示顺序" width="100" sortable></vxe-column>
            <vxe-column field="isValid" title="状态" width="100" align="center">
                <template #default="{ row }">
                <el-switch
                    v-model="row.isValid"
                    :active-value="1"
                    :inactive-value="0"
                    @change="handleStatusChange(row)"
                    :disabled="row.roleKey === 'admin'" 
                ></el-switch>
                </template>
            </vxe-column>
            <vxe-column field="createTime" title="创建时间" width="180" sortable></vxe-column>
            <vxe-column title="操作" width="220" fixed="right" align="center">
                <template #default="{ row }">
                    <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" :disabled="row.roleKey === 'admin'">修改</el-button>
                    <el-button link type="danger" icon="Delete" @click="handleDelete(row)" :disabled="row.roleKey === 'admin'">删除</el-button>
                </template>
            </vxe-column>
        </vxe-table>

        <!-- 分页 -->
        <vxe-pager
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="handlePageChange"
            class="table-pager"
            v-show="pagination.total > 0"
        >
      </vxe-pager>
    </el-card>

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" append-to-body @close="cancel">
       <el-form ref="roleFormRef" :model="dialog.formData" :rules="rules" label-width="100px">
          <el-form-item label="角色名称" prop="roleName">
             <el-input v-model="dialog.formData.roleName" placeholder="请输入角色名称" />
          </el-form-item>
          <el-form-item prop="roleKey">
             <template #label>
                <span>
                   <el-tooltip content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasRole('admin')`)" placement="top">
                      <el-icon><question-filled /></el-icon>
                   </el-tooltip>
                   权限字符
                </span>
             </template>
             <el-input v-model="dialog.formData.roleKey" placeholder="请输入权限字符" />
          </el-form-item>
          <el-form-item label="角色顺序" prop="roleSort">
             <el-input-number v-model="dialog.formData.roleSort" controls-position="right" :min="0" />
          </el-form-item>
          <el-form-item label="状态">
             <el-radio-group v-model="dialog.formData.isValid">
                <el-radio :label="1">正常</el-radio>
                <el-radio :label="0">停用</el-radio>
             </el-radio-group>
          </el-form-item>
          <el-form-item label="菜单权限">
             <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event)">展开/折叠</el-checkbox>
             <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event)">全选/全不选</el-checkbox>
             <el-checkbox v-model="menuCheckStrictly" @change="handleCheckedTreeConnect($event)">父子联动</el-checkbox>
             <el-tree
                class="tree-border"
                :data="menuOptions"
                show-checkbox
                ref="menuTreeRef"
                node-key="id"
                :check-strictly="!menuCheckStrictly"
                empty-text="加载中，请稍候"
                :props="{ label: 'label', children: 'children' }"
             ></el-tree>
          </el-form-item>
          <el-form-item label="备注">
             <el-input v-model="dialog.formData.remark" type="textarea" placeholder="请输入内容"></el-input>
          </el-form-item>
       </el-form>
       <template #footer>
          <div class="dialog-footer">
             <el-button type="primary" @click="submitForm">确 定</el-button>
             <el-button @click="cancel">取 消</el-button>
          </div>
       </template>
    </el-dialog>

    <!-- 分配数据权限对话框 -->
    <!-- <el-dialog :title="dataScopeDialog.title" v-model="dataScopeDialog.visible" width="500px" append-to-body>
       ...
    </el-dialog> -->

  </div>
</template>

<script setup name="RoleManagement">
import { ref, reactive, onMounted, nextTick } from 'vue';
import { listRole, getRole, addRole, updateRole, changeRoleStatus, delRole } from '@/api/role';
import { treeselect as menuTreeselect, roleMenuTreeselect } from "@/api/menu"; // 引入菜单相关 API
import { ElMessageBox, ElMessage } from 'element-plus';
import { QuestionFilled } from '@element-plus/icons-vue'; // 只保留 QuestionFilled

// --- refs ---
const queryFormRef = ref(null);
const roleFormRef = ref(null);
const menuTreeRef = ref(null); // 菜单树引用
const xTableRef = ref(null); // VxeTable 引用

// --- state ---
const loading = ref(true);
const selectedRows = ref([]); // 存储 VxeTable 选中的行数据
const roleList = ref([]); // 角色表格数据
const total = ref(0); // 总条数
const menuOptions = ref([]); // 菜单树选项
const menuExpand = ref(false);
const menuNodeAll = ref(false);
const menuCheckStrictly = ref(true); // 默认父子不联动

// 分页配置 (适配 VxePager)
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 查询参数 (将分页参数移到 pagination)
const queryParams = reactive({
  roleName: undefined,
  roleKey: undefined,
  isValid: undefined
});

// 对话框初始表单数据结构
const initialFormData = () => ({
    id: undefined,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    isValid: 1,
    menuIds: [],
    // deptIds: [], // 数据权限相关
    remark: undefined
});

// 对话框状态 (新增/修改)
const dialog = reactive({
  visible: false,
  title: '',
  formData: initialFormData()
});

// 表单校验规则
const rules = reactive({
    roleName: [
        { required: true, message: "角色名称不能为空", trigger: "blur" }
    ],
    roleKey: [
        { required: true, message: "权限字符不能为空", trigger: "blur" }
    ],
    roleSort: [
        { required: true, message: "角色顺序不能为空", trigger: "blur" }
    ]
});

// --- methods ---

/** 查询角色列表 */
async function getList() {
    loading.value = true;
    // 组合查询参数和分页参数
    const params = {
        ...queryParams,
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize
    };
    try {
        const response = await listRole(params);
        roleList.value = response.data.records || [];
        total.value = response.data.total || 0;
    } catch (error) {
        console.error("获取角色列表失败:", error);
        ElMessage.error("获取角色列表失败");
    } finally {
        loading.value = false;
    }
}

/** 分页事件处理 */
function handlePageChange({ currentPage, pageSize }) {
  pagination.currentPage = currentPage;
  pagination.pageSize = pageSize;
  getList();
}

/** 搜索按钮操作 */
function handleQuery() {
    pagination.currentPage = 1; // 搜索时重置到第一页
    getList();
}

/** 重置按钮操作 */
function resetQuery() {
    // dateRange.value = []; // 如果使用日期范围查询
    queryFormRef.value?.resetFields();
    pagination.currentPage = 1;
    pagination.pageSize = 10; // 可选：重置页面大小
    getList();
}

// 表格 Checkbox 选中状态变化 (适配 VxeTable)
function handleSelectionChange({ records }) {
    selectedRows.value = records;
}

/** 查询菜单树结构 */
async function getMenuTreeselect() {
    try {
        const response = await menuTreeselect();
        menuOptions.value = response.data || [];
    } catch (error) {
        console.error("获取菜单树失败:", error);
    }
}

/** 根据角色ID查询菜单树结构 */
async function getRoleMenuTreeselect(roleId) {
    try {
        const response = await roleMenuTreeselect(roleId);
        menuOptions.value = response.data.menus || [];
        return response.data.checkedKeys || [];
    } catch (error) {
        console.error("获取角色菜单树失败:", error);
        return [];
    }
}

/** 角色状态修改 */
async function handleStatusChange(row) {
    let text = row.isValid === 1 ? "启用" : "停用";
    // 检查是否是 admin 角色
    if (row.roleKey === 'admin') {
        ElMessage.warning("不允许修改超级管理员状态");
        // 状态回滚
        row.isValid = row.isValid === 1 ? 0 : 1;
        return;
    }
    try {
        await ElMessageBox.confirm('确认要"' + text + '""' + row.roleName + '"角色吗?', "警告", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        });
        await changeRoleStatus(row.id, row.isValid);
        ElMessage.success(text + "成功");
    } catch (error) {
        ElMessage.error(text + "失败");
        // Rollback state on error
        row.isValid = row.isValid === 1 ? 0 : 1;
    }
}

// 重置表单和菜单树
function reset() {
    if (menuTreeRef.value != null) {
        menuTreeRef.value.setCheckedKeys([]);
    }
    menuExpand.value = false;
    menuNodeAll.value = false;
    menuCheckStrictly.value = true; // 重置为父子不关联
    dialog.formData = initialFormData();
    roleFormRef.value?.resetFields();
}

/** 新增按钮操作 */
function handleAdd() {
    reset();
    getMenuTreeselect(); // 获取所有菜单树
    dialog.visible = true;
    dialog.title = "添加角色";
}

/** 修改按钮操作 */
async function handleUpdate(row) {
    reset();
    const roleId = row.id;
    try {
        const roleResp = await getRole(roleId);
        dialog.formData = roleResp.data;
        dialog.visible = true;
        dialog.title = "修改角色";

        const checkedKeys = await getRoleMenuTreeselect(roleId);
        await nextTick();
        menuTreeRef.value?.setCheckedKeys([]);
        checkedKeys.forEach((v) => {
            menuTreeRef.value?.setChecked(v, true, false);
        });

    } catch (error) {
        console.error("获取角色信息或菜单树失败:", error);
        ElMessage.error("加载角色数据失败");
    }
}

/** 提交按钮（新增/修改） */
async function submitForm() {
    roleFormRef.value?.validate(async (valid) => {
        if (valid) {
            const isEdit = dialog.formData.id !== undefined;
            dialog.formData.menuIds = getMenuAllCheckedKeys(); // 获取选中的菜单ID

            const action = isEdit ? updateRole : addRole;
            const successMessage = isEdit ? "修改成功" : "新增成功";
            const errorMessage = isEdit ? "修改失败" : "新增失败";

            try {
                await action(dialog.formData);
                ElMessage.success(successMessage);
                dialog.visible = false;
                await getList(); // 重新加载列表
            } catch (error) {
                console.error(`${errorMessage}:`, error);
                ElMessage.error(error.response?.data?.msg || errorMessage);
            }
        } else {
            console.log('校验失败');
        }
    });
}

/** 删除按钮操作 */
async function handleDelete(row) {
    // 使用 VxeTable 的选中行数据 selectedRows
    const roleIds = row.id ? [row.id] : selectedRows.value.map(r => r.id);
     if (!roleIds || roleIds.length === 0) {
        ElMessage.warning("请选择要删除的角色");
        return;
    }
    // 获取角色名称用于确认框
    let roleNamesToDelete;
    if (row.id) {
        roleNamesToDelete = row.roleName;
    } else {
        roleNamesToDelete = selectedRows.value.map(r => r.roleName).join(", ");
    }

    // 检查是否包含 admin 角色
    const hasAdmin = row.id ? row.roleKey === 'admin' : selectedRows.value.some(r => r.roleKey === 'admin');
    if (hasAdmin) {
        ElMessage.error("不允许删除超级管理员角色");
        return;
    }

    try {
        await ElMessageBox.confirm(`是否确认删除角色编号为"${roleIds.join(',')}" / 名称为 "${roleNamesToDelete}" 的数据项?`, "警告", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        });
        await delRole(roleIds); // 假设 delRole API 能处理数组
        await getList();
        ElMessage.success("删除成功");
        selectedRows.value = []; // 清空选中行
        xTableRef.value?.clearCheckboxRow(); // 清空 VxeTable 选中状态
    } catch (error) {
         console.error("删除失败:", error);
         ElMessage.error(error.response?.data?.msg || "删除失败");
    }
}

// 树权限（展开/折叠）
function handleCheckedTreeExpand(value) {
    menuOptions.value.forEach(nodeData => {
        const node = menuTreeRef.value?.getNode(nodeData.id);
        if (node) {
            node.expanded = value;
        }
    });
}
// 树权限（全选/全不选）
function handleCheckedTreeNodeAll(value) {
    menuTreeRef.value?.setCheckedNodes(value ? menuOptions.value.map(n=>n.id) : []); // 使用 setCheckedKeys 可能更直接
}
// 树权限（父子联动）
function handleCheckedTreeConnect(value) {
    menuCheckStrictly.value = value;
}
// 所有菜单节点数据
function getMenuAllCheckedKeys() {
    let checkedKeys = menuTreeRef.value?.getCheckedKeys() || [];
    let halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || [];
    // 确保不重复添加
    const combinedKeys = new Set([...checkedKeys, ...halfCheckedKeys]);
    return Array.from(combinedKeys);
}

/** 取消按钮 */
function cancel() {
    dialog.visible = false;
    reset();
}

// --- 生命周期 ---
onMounted(() => {
    getList();
});

</script>

<style scoped>
.search-card, .operate-card, .table-card {
    margin-bottom: 10px;
}
.tree-border {
    margin-top: 5px;
    border: 1px solid #e5e6e7;
    background: #fff none;
    border-radius: 4px;
    width: 100%;
    height: 300px; /* Adjust height as needed */
    overflow-y: auto;
}
.table-pager {
  margin-top: 10px;
  /* float: right; VxePager 默认可能不需要浮动 */
}
.dialog-footer {
    text-align: right;
}
.mb8 {
    margin-bottom: 8px;
}
</style> 