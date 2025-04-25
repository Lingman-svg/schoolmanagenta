import { createRouter, createWebHistory } from 'vue-router'
// import HomeView from '../views/HomeView.vue' // 不再需要默认的 HomeView

// 引入 App.vue 作为布局组件（如果需要嵌套路由）
// 或者直接将 App.vue 作为根组件，其内部的 <router-view> 渲染匹配的路由

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
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
      meta: { title: '科目管理' } // 可选：用于面包屑或标签页标题
    },
    // --- 其他业务模块路由将在这里添加 ---
    // 例如：教师管理 (取消注释)
    {
      path: '/teachers', // 使用这个路径
      name: 'TeacherManagement',
      component: () => import('../views/TeacherManagement.vue'),
      meta: { title: '教师管理' }
    },
    {
      path: '/classes', // 使用这个路径
      name: 'ClassManagement',
      component: () => import('../views/ClassManagement.vue'),
      meta: { title: '班级管理' }
    },
    // ... 其他路由
  ]
})

// --- 路由守卫 (可选，用于权限控制等) ---
// router.beforeEach((to, from, next) => {
//   // ... 权限判断逻辑
//   next()
// })

export default router
