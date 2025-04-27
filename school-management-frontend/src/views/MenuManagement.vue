<template>
  <div class="app-container menu-management-container">
    <!-- 搜索与操作区域 -->
     <el-card class="search-operate-card" shadow="never">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true">
            <el-form-item label="菜单名称" prop="menuName">
                <el-input
                    v-model="queryParams.menuName"
                    placeholder="请输入菜单名称"
                    clearable
                    style="width: 200px"
                    @keyup.enter="handleQuery"
                />
            </el-form-item>
            <el-form-item label="状态" prop="isValid">
                 <el-select v-model="queryParams.isValid" placeholder="菜单状态" clearable style="width: 120px">
                    <el-option label="正常" :value="1" />
                    <el-option label="停用" :value="0" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                 <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                 <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
                 <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
            </el-form-item>
        </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
        <el-table
            v-if="refreshTable"
            v-loading="loading"
            :data="menuList"
            row-key="id"
            :default-expand-all="isExpandAll"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
            border
            stripe
        >
            <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="200"></el-table-column>
            <el-table-column prop="icon" label="图标" align="center" width="80">
                <template #default="scope">
                    <!-- 假设使用 Element Plus 图标 -->
                    <el-icon v-if="scope.row.icon"><component :is="scope.row.icon" /></el-icon>
                </template>
            </el-table-column>
            <el-table-column prop="orderNum" label="排序" width="80" align="center"></el-table-column>
            <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true" width="180"></el-table-column>
            <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true"></el-table-column>
             <el-table-column prop="path" label="路由地址" :show-overflow-tooltip="true" width="150"></el-table-column>
            <el-table-column prop="isValid" label="状态" align="center" width="80">
                <template #default="scope">
                    <el-tag :type="scope.row.isValid === 1 ? 'success' : 'danger'">
                        {{ scope.row.isValid === 1 ? '正常' : '停用' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="visible" label="可见" align="center" width="80">
                 <template #default="scope">
                    <el-tag :type="scope.row.visible === 0 ? 'success' : 'info'">
                        {{ scope.row.visible === 0 ? '显示' : '隐藏' }}
                    </el-tag>
                </template>
            </el-table-column>
             <el-table-column label="创建时间" align="center" prop="createTime" width="180">
                <template #default="scope">
                    <span>{{ scope.row.createTime }}</span> <!-- 可能需要格式化 -->
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width" fixed="right">
                <template #default="scope">
                    <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
                    <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)" :disabled="scope.row.menuType === 'F'">新增</el-button>
                    <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="700px" append-to-body @close="resetForm">
        <el-form ref="menuFormRef" :model="dialog.formData" :rules="rules" label-width="100px">
            <el-row>
                <el-col :span="24">
                    <el-form-item label="上级菜单" prop="parentId">
                        <el-tree-select
                            v-model="dialog.formData.parentId"
                            :data="menuOptions"
                            :props="{ value: 'id', label: 'label', children: 'children' }"
                            value-key="id"
                            placeholder="选择上级菜单"
                            check-strictly
                            filterable
                        />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="菜单类型" prop="menuType">
                        <el-radio-group v-model="dialog.formData.menuType">
                            <el-radio label="M">目录</el-radio>
                            <el-radio label="C">菜单</el-radio>
                            <el-radio label="F">按钮</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="24" v-if="dialog.formData.menuType != 'F'">
                     <el-form-item label="菜单图标" prop="icon">
                         <!-- 这里可以使用图标选择器组件，暂时用 Input 代替 -->
                        <el-input v-model="dialog.formData.icon" placeholder="请输入图标名称或选择图标" />
                         <span>(例如：el-icon-setting, user, house 等 Element Plus 图标名)</span>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="菜单名称" prop="menuName">
                        <el-input v-model="dialog.formData.menuName" placeholder="请输入菜单名称" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="显示排序" prop="orderNum">
                        <el-input-number v-model="dialog.formData.orderNum" controls-position="right" :min="0" />
                    </el-form-item>
                </el-col>
                <el-col :span="12" v-if="dialog.formData.menuType != 'F'">
                    <el-form-item prop="path">
                         <template #label>
                            <span>
                                <el-tooltip content="访问的路由地址，如：`user`。如需外链，则以 `http(s)://` 开头" placement="top">
                                    <el-icon><question-filled /></el-icon>
                                </el-tooltip>
                                路由地址
                            </span>
                        </template>
                        <el-input v-model="dialog.formData.path" placeholder="请输入路由地址" />
                    </el-form-item>
                </el-col>
                <el-col :span="12" v-if="dialog.formData.menuType == 'C'">
                    <el-form-item prop="component">
                        <template #label>
                             <span>
                                <el-tooltip content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" placement="top">
                                    <el-icon><question-filled /></el-icon>
                                </el-tooltip>
                                组件路径
                            </span>
                        </template>
                        <el-input v-model="dialog.formData.component" placeholder="请输入组件路径" />
                    </el-form-item>
                </el-col>
                <el-col :span="12" v-if="dialog.formData.menuType != 'M'">
                    <el-form-item prop="perms">
                         <template #label>
                            <span>
                                <el-tooltip content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)" placement="top">
                                     <el-icon><question-filled /></el-icon>
                                </el-tooltip>
                                权限字符
                            </span>
                        </template>
                        <el-input v-model="dialog.formData.perms" placeholder="请输入权限标识" maxlength="100" />
                    </el-form-item>
                </el-col>
                  <el-col :span="12" v-if="dialog.formData.menuType != 'F'">
                    <el-form-item prop="isFrame">
                         <template #label>
                            <span>
                                <el-tooltip content="选择是外链则路由地址需要以`http(s)://`开头" placement="top">
                                    <el-icon><question-filled /></el-icon>
                                </el-tooltip>
                                是否外链
                            </span>
                        </template>
                        <el-radio-group v-model="dialog.formData.isFrame">
                            <el-radio :label="1">是</el-radio>
                            <el-radio :label="0">否</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12" v-if="dialog.formData.menuType != 'F'">
                    <el-form-item prop="visible">
                         <template #label>
                             <span>
                                <el-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                                    <el-icon><question-filled /></el-icon>
                                </el-tooltip>
                                显示状态
                            </span>
                        </template>
                        <el-radio-group v-model="dialog.formData.visible">
                             <el-radio :label="0">显示</el-radio>
                            <el-radio :label="1">隐藏</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                 <el-col :span="12" v-if="dialog.formData.menuType != 'F'">
                     <el-form-item prop="isValid">
                         <template #label>
                             <span>
                                <el-tooltip content="选择停用则路由将完全无法访问" placement="top">
                                    <el-icon><question-filled /></el-icon>
                                </el-tooltip>
                                菜单状态
                            </span>
                        </template>
                        <el-radio-group v-model="dialog.formData.isValid">
                            <el-radio :label="1">正常</el-radio>
                            <el-radio :label="0">停用</el-radio>
                        </el-radio-group>
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

<script setup name="MenuManagement">
import { ref, reactive, onMounted, nextTick } from 'vue';
import { listMenu, getMenu, addMenu, updateMenu, delMenu, treeselect } from '@/api/menu';
import { ElMessageBox, ElMessage } from 'element-plus';

// --- refs ---
const queryFormRef = ref(null);
const menuFormRef = ref(null);

// --- state ---
const loading = ref(true);
const menuList = ref([]); // 用于存储树形结构的菜单列表
const menuOptions = ref([]); // 用于上级菜单下拉树
const isExpandAll = ref(false); // 是否展开，默认全部折叠
const refreshTable = ref(true); // 控制表格刷新

// 查询参数
const queryParams = reactive({
  menuName: undefined,
  isValid: undefined // 使用 isValid 对应后端的 'status' 或 'isValid'
});

// 对话框初始表单数据结构
const initialFormData = () => ({
  id: undefined,
  parentId: 0, // 默认顶级
  menuType: 'M', // 默认目录
  icon: '',
  menuName: '',
  orderNum: 0,
  isFrame: 0,
  path: '',
  component: '',
  perms: '',
  visible: 0,
  isValid: 1 // 默认正常
});

// 对话框状态 (新增/修改)
const dialog = reactive({
  visible: false,
  title: '',
  formData: initialFormData()
});

// 表单校验规则
const rules = reactive({
  menuName: [
    { required: true, message: "菜单名称不能为空", trigger: "blur" }
  ],
  orderNum: [
    { required: true, message: "菜单顺序不能为空", trigger: "blur" }
  ],
  path: [
    { required: true, message: "路由地址不能为空", trigger: "blur" }
  ],
  parentId: [
      { required: true, message: "上级菜单不能为空", trigger: ["blur", "change"] }
  ]
});

// --- methods ---

/** 查询菜单列表 */
async function getList() {
  loading.value = true;
  try {
    const response = await listMenu(queryParams);
    // 后端返回的是扁平列表，需要前端转换成树形结构
    menuList.value = handleTree(response.data || [], 'id', 'parentId');
  } catch (error) {
      console.error("获取菜单列表失败:", error);
      ElMessage.error("获取菜单列表失败");
  } finally {
      loading.value = false;
  }
}

/** 查询菜单下拉树结构 */
async function getTreeselect() {
  try {
    const response = await treeselect();
    // 添加一个根节点选项
    const rootOption = { id: 0, label: '主类目', children: [] };
    menuOptions.value = [rootOption, ...(response.data || [])];
  } catch (error) {
      console.error("获取菜单下拉树失败:", error);
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value?.resetFields();
  getList();
}

// 重置表单
function resetForm() {
  dialog.formData = initialFormData();
  menuFormRef.value?.resetFields();
}

/** 新增按钮操作 */
function handleAdd(row) {
  resetForm();
  getTreeselect(); // 加载下拉树数据
  if (row != null && row.id) {
    // 如果传入了行数据，表示在当前行下新增
    dialog.formData.parentId = row.id;
  } else {
    dialog.formData.parentId = 0; // 否则默认为顶级
  }
  dialog.visible = true;
  dialog.title = "添加菜单";
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  resetForm();
  await getTreeselect(); // 确保下拉树数据已加载
  try {
    const response = await getMenu(row.id);
    dialog.formData = response.data;
    dialog.visible = true;
    dialog.title = "修改菜单";
  } catch (error) {
      console.error("获取菜单详情失败:", error);
      ElMessage.error("获取菜单详情失败");
  }
}

/** 提交按钮 */
async function submitForm() {
  menuFormRef.value?.validate(async (valid) => {
    if (valid) {
      const isEdit = dialog.formData.id !== undefined;
      const action = isEdit ? updateMenu : addMenu;
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
  await ElMessageBox.confirm(`是否确认删除名称为"${row.menuName}"的数据项？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  });
  try {
    await delMenu(row.id);
    await getList(); // 重新加载列表
    ElMessage.success("删除成功");
  } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error(error.response?.data?.msg || "删除失败");
  }
}

/** 取消按钮 */
function cancel() {
  dialog.visible = false;
  resetForm();
}

// --- 工具函数 ---

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
function handleTree(data, id = 'id', parentId = 'parentId', children = 'children') {
    let config = {
        id: id,
        parentId: parentId,
        childrenList: children
    };

    var childrenListMap = {};
    var nodeIds = {};
    var tree = [];

    for (let d of data) {
        let parentId = d[config.parentId];
        if (childrenListMap[parentId] == null) {
            childrenListMap[parentId] = [];
        }
        nodeIds[d[config.id]] = d;
        childrenListMap[parentId].push(d);
    }

    for (let d of data) {
        let parentId = d[config.parentId];
        if (nodeIds[parentId] == null) {
            tree.push(d);
        }
    }

    for (let t of tree) {
        adaptToChildrenList(t);
    }

    function adaptToChildrenList(o) {
        if (childrenListMap[o[config.id]] !== null) {
            o[config.childrenList] = childrenListMap[o[config.id]];
        }
        if (o[config.childrenList]) {
            for (let c of o[config.childrenList]) {
                adaptToChildrenList(c);
            }
        }
    }
    return tree;
}

// --- 生命周期 ---
onMounted(() => {
  getList();
});

</script>

<style scoped>
.search-operate-card, .table-card {
    margin-bottom: 10px;
}
.dialog-footer {
    text-align: right;
}
</style> 