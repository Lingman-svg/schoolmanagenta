/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 21:35:19
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 21:44:29
 * @FilePath: \schoolmanagenta\school-management-frontend\src\router\index.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'; // 导入用户 store
// import HomeView from '../views/HomeView.vue' // 不再需要默认的 HomeView

// 引入 App.vue 作为布局组件（如果需要嵌套路由）
// 或者直接将 App.vue 作为根组件，其内部的 <router-view> 渲染匹配的路由

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 添加登录页路由
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/UserLogin.vue'),
      meta: { title: '登录', hideInMenu: true } // hideInMenu: true 避免显示在菜单中
    },
    // 重定向根路径到科目管理或其他默认页
    {
      path: '/',
      redirect: '/subjects' // 或者 '/dashboard' 等
    },
    // 移除默认的 home 和 about 路由
    // {
    //   path: '/',
    //   name: 'home',
    //   component: HomeView
    // },
    // {
    //   path: '/about',
    //   name: 'about',
    //   component: () => import('../views/AboutView.vue')
    // },
    // --- 添加科目管理路由 ---
    {
      path: '/subjects',
      name: 'SubjectManagement',
      // 使用动态导入实现路由懒加载
      component: () => import('../views/SubjectManagement.vue'),
      meta: { title: '科目管理', permission: 'base:subject:view' } // 可选：用于面包屑或标签页标题
    },
    // --- 其他业务模块路由将在这里添加 ---
    // 例如：教师管理 (取消注释)
    {
      path: '/teachers', // 使用这个路径
      name: 'TeacherManagement',
      component: () => import('../views/TeacherManagement.vue'),
      meta: { title: '教师管理', permission: 'base:teacher:view' }
    },
    {
      path: '/classes', // 使用这个路径
      name: 'ClassManagement',
      component: () => import('../views/ClassManagement.vue'),
      meta: { title: '班级管理', permission: 'base:class:view' }
    },
    {
      path: '/students', // 使用这个路径
      name: 'StudentManagement',
      component: () => import('../views/StudentManagement.vue'),
      meta: { title: '学生管理', permission: 'base:student:view' }
    },
    {
      path: '/grades',
      name: 'GradeManagement',
      component: () => import('../views/GradeManagement.vue'),
      meta: { title: '成绩管理', permission: 'base:grade:view' }
    },
    // --- 添加节课时间管理路由 ---
    {
      path: '/course-times', // 更新路径，与其他模块一致
      name: 'CourseTimeManagement', // 路由名称
      component: () => import('../views/CourseTimeManagement.vue'), // 更新组件导入路径
      meta: { title: '节课时间管理', permission: 'base:courseTime:view' } // 页面标题
    },
    // --- 添加课程表管理路由 ---
    {
      path: '/courses', // 定义访问路径
      name: 'CourseManagement', // 路由名称
      component: () => import('../views/CourseManagement.vue'), // 指向新创建的组件
      meta: { title: '课程表管理', permission: 'base:course:view' } // 页面标题
    },
    // --- 添加系统配置管理路由 ---
    {
      path: '/config', // 路径保持不变，与 Controller 对应
      name: 'SystemConfigManagement', // 更新路由名称
      component: () => import('../views/SystemConfigManagement.vue'), // 更新组件路径和名称
      meta: { title: '系统配置管理', permission: 'base:config:view' } // 更新页面标题
    },
    // --- 添加菜单管理路由 ---
    {
      path: '/system/menu', // 与 Controller 对应
      name: 'MenuManagement', // 路由名称
      component: () => import('../views/MenuManagement.vue'), // 指向新创建的组件
      meta: { title: '菜单管理', permission: 'base:menu:view' } // 页面标题
    },
    // --- 添加角色管理路由 ---
    {
      path: '/system/role', // 与 Controller 对应
      name: 'RoleManagement', // 路由名称
      component: () => import('../views/RoleManagement.vue'), // 指向新创建的组件
      meta: { title: '角色管理', permission: 'base:role:view' } // 页面标题
    },
    // --- 添加用户管理路由 ---
    {
      path: '/system/user', // 定义访问路径
      name: 'UserManagement', // 路由名称
      component: () => import('../views/UserManagement.vue'), // 指向新创建的组件
      meta: { title: '用户管理', permission: 'base:user:view' } // 页面标题
    },
    // ... 其他路由
    {
      path: '/ai/chat', // Define the path for the AI chat page
      name: 'AiChat',
      component: () => import('../views/AiChat.vue'),
      meta: { title: 'AI 助手', icon: 'ChatDotRound' } // Example title and icon
      // Add permission if needed: permission: 'system:ai:chat'
    },
  ]
})

// --- 路由守卫 (导航守卫) ---
const whiteList = ['/login']; // 不需要登录即可访问的页面路径列表

router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore();
    const token = userStore.token;

    if (token) {
        // 1. 如果已登录 (有 token)
        if (to.path === '/login') {
            // 如果尝试访问登录页，重定向到主页
            next({ path: '/' });
        } else {
            // 检查是否已获取用户信息 (角色/权限)
            // 注意：userStore.userInfo 可能只是 {}，需要检查是否有实际数据，例如用户名
            if (userStore.userInfo && userStore.userInfo.name) { 
                // 已获取用户信息，直接放行
                next();
            } else {
                try {
                    // 尝试获取用户信息
                    await userStore.getInfo();
                    // 获取成功后，再次检查目标路径（如果异步获取时路由改变）
                    // 或者直接放行，因为 to 已经是正确的
                    next(); 
                } catch (error) {
                    // 获取用户信息失败 (例如 token 失效)
                    console.error('Get user info error:', error);
                    // 清除 token 并重定向到登录页
                    await userStore.logout();
                    next(`/login?redirect=${to.path}`);
                }
            }
        }
    } else {
        // 2. 如果未登录 (没有 token)
        if (whiteList.includes(to.path)) {
            // 如果在白名单中，直接放行
            next();
        } else {
            // 否则，重定向到登录页，并带上目标路径作为 redirect 参数
            next(`/login?redirect=${to.path}`);
        }
    }
});

export default router
