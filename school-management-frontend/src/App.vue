<!--
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 21:35:19
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 20:27:27
 * @FilePath: \schoolmanagenta\school-management-frontend\src\App.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<script setup>
import { computed } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'; // 导入 user store
import { ElMessageBox, ElMessage } from 'element-plus'
// 引入 Element Plus 图标 (需要先安装 @element-plus/icons-vue: npm install @element-plus/icons-vue)
import { ArrowDown } from '@element-plus/icons-vue'
import { checkPermission } from '@/directives/permission'; // 导入检查函数

const route = useRoute()
const router = useRouter(); // 获取 router 实例
const userStore = useUserStore(); // 获取 user store 实例

// 计算当前激活的菜单项路径
const activeMenu = computed(() => {
  const { path } = route
  return path
})

// --- 从路由动态生成菜单 --- 
const menuRoutes = computed(() => {
    // 获取所有路由规则
    const routes = router.options.routes;
    // 过滤并转换路由为菜单结构 (这里做一个简单的示例，可能需要递归处理嵌套路由)
    // TODO: 后续需要根据权限进行过滤
    return filterAndBuildMenu(routes);
});

function filterAndBuildMenu(routes, basePath = '') {
    const menu = [];
    routes.forEach(routeItem => {
        // 忽略隐藏的路由和没有 meta 或 title 的路由
        if (!routeItem.meta || routeItem.meta.hideInMenu || !routeItem.meta.title) {
            return;
        }

        // 构建完整路径
        const fullPath = resolvePath(basePath, routeItem.path);

        // 简单处理：假设路由配置是一层或两层结构 (用于子菜单)
        const menuItem = {
            index: fullPath,
            title: routeItem.meta.title,
            icon: routeItem.meta.icon, // 假设 meta 中有 icon
            permission: routeItem.meta.permission, // 新增：从 meta 获取权限标识
            children: []
        };

        // 如果有子路由，递归处理 (这里简化处理，只考虑两层)
        // 实际应用中可能需要更复杂的递归逻辑来处理多级菜单
        if (routeItem.children && routeItem.children.length > 0) {
             // 递归调用时也传递 permission (或者在子路由 meta 中定义)
             menuItem.children = filterAndBuildMenu(routeItem.children, fullPath);
             // 如果子菜单为空（都被过滤掉了），则不显示父菜单 (除非父菜单本身也配置为可点击)
             // 并且父菜单本身没有权限要求，或者有权限
             if (menuItem.children.length === 0 && (!menuItem.permission || !checkPermission(menuItem.permission))) { // 考虑父菜单权限
                 return;
             }
        }
        
        // TODO: 在这里根据 userStore.userInfo.permissions 过滤 menuItem - 已通过 v-permission 实现
        // 移除之前的注释和过滤逻辑，直接添加
        menu.push(menuItem);

    });
    return menu;
}

// 辅助函数：解析路径 (处理相对路径和绝对路径)
function resolvePath(basePath, routePath) {
    if (/^(https?:|mailto:|tel:)/.test(routePath)) {
        return routePath;
    }
    if (routePath.startsWith('/')) {
        return routePath;
    }
    return basePath ? `${basePath}/${routePath}` : routePath;
}

// TODO: 实现权限检查函数 (可以直接使用 directives/permission.js 中的 checkPermission)

// 处理用户下拉菜单命令
const handleUserCommand = (command) => {
  if (command === 'logout') {
    handleLogout();
  }
};

// 处理登出逻辑
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    await userStore.logout();
    // 登出后跳转到登录页
    router.push('/login'); // 假设登录页路由为 /login
    ElMessage.success('退出成功');
  } catch (error) {
    // 如果用户点击取消，或者 logout action 失败
    if (error !== 'cancel') {
        console.error('Logout failed:', error);
        ElMessage.error('退出时发生错误');
    }
  }
};
</script>

<template>
  <el-container class="app-container">
    <!-- 左侧菜单 -->
    <el-aside width="200px" class="app-aside">
      <div class="app-logo">小学管理系统</div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        router
        background-color="#ffffff"
        text-color="#303133"
        active-text-color="#409EFF"
      >
        <!-- 动态渲染菜单 -->
        <template v-for="menuItem in menuRoutes" :key="menuItem.index">
            <!-- 应用 v-permission 指令到子菜单 -->
            <el-sub-menu 
                v-if="menuItem.children && menuItem.children.length > 0" 
                :index="menuItem.index"
                v-permission="menuItem.permission" 
            >
                <template #title>
                    <el-icon v-if="menuItem.icon"><component :is="menuItem.icon" /></el-icon>
                    <span>{{ menuItem.title }}</span>
                </template>
                <!-- 渲染子菜单项 -->
                 <!-- 应用 v-permission 指令到子菜单项 -->
                 <el-menu-item 
                     v-for="child in menuItem.children" 
                     :key="child.index" 
                     :index="child.index"
                     v-permission="child.permission"
                 >
                    <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
                    <span>{{ child.title }}</span>
                 </el-menu-item>
            </el-sub-menu>
            <!-- 应用 v-permission 指令到一级菜单项 -->
            <el-menu-item 
                v-else 
                :index="menuItem.index"
                v-permission="menuItem.permission"
            >
                <el-icon v-if="menuItem.icon"><component :is="menuItem.icon" /></el-icon>
                <span>{{ menuItem.title }}</span>
            </el-menu-item>
        </template>

        <!-- 移除静态菜单 -->
        <!-- 
        <el-sub-menu index="/base-data"> ... </el-sub-menu>
        <el-sub-menu index="/course-management"> ... </el-sub-menu>
        <el-sub-menu index="/system"> ... </el-sub-menu> 
        -->

      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部 Header -->
      <el-header class="app-header">
        <div>面包屑导航 / 用户信息</div>
        <!-- 用户信息与退出 -->
        <div class="user-info">
           <el-dropdown @command="handleUserCommand">
              <span class="el-dropdown-link">
                <!-- 显示用户名，需要从 userStore.userInfo 获取 -->
                {{ userStore.userInfo.name || '用户名' }} 
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <!-- <el-dropdown-item command="profile">个人中心</el-dropdown-item> -->
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区域 -->
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.app-container {
  height: 100vh; /* 让容器铺满整个视口高度 */
}

.app-aside {
  background-color: #ffffff; /* 侧边栏背景白色 */
  border-right: 1px solid #e6e6e6; /* 右边框 */
  display: flex;
  flex-direction: column;
}

.app-logo {
  height: 60px; /* 与 header 高度一致 */
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  color: #409EFF; /* Logo 蓝色 */
  background-color: #ffffff; /* Logo 背景白色 */
  flex-shrink: 0; /* 防止 logo 被压缩 */
}

.el-menu {
  border-right: none; /* 移除 el-menu 自带的右边框 */
  flex-grow: 1; /* 让菜单填充剩余空间 */
  overflow-y: auto; /* 如果菜单过长，允许滚动 */
}

.app-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #ffffff; /* Header 背景白色 */
  border-bottom: 1px solid #e6e6e6; /* 底部边框 */
}

.app-main {
  background-color: #f0f2f5; /* 主内容区域背景稍灰 */
  padding: 20px;
  overflow-y: auto; /* 如果内容过长，允许滚动 */
}

/* 路由切换动画 (可选) */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.user-info {
  margin-right: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}
</style>
