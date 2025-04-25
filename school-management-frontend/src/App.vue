<script setup>
import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
// 引入 Element Plus 图标 (需要先安装 @element-plus/icons-vue: npm install @element-plus/icons-vue)
import { Setting, Calendar, Tools } from '@element-plus/icons-vue'

const route = useRoute()

// 计算当前激活的菜单项路径
const activeMenu = computed(() => {
  const { path } = route
  return path
})
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
        <!-- 示例菜单项 -->
        <el-sub-menu index="/base-data">
          <template #title>
            <el-icon><Setting /></el-icon> <!-- 需要引入 Element Plus 图标 -->
            <span>基础数据管理</span>
          </template>
          <el-menu-item index="/subjects">科目管理</el-menu-item>
          <!-- 其他基础数据菜单项 -->
          <el-menu-item index="/teachers">教师管理</el-menu-item>
          <el-menu-item index="/classes">班级管理</el-menu-item>
          <el-menu-item index="/students">学生管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/course-management">
          <template #title>
             <el-icon><Calendar /></el-icon>
             <span>课程教学管理</span>
          </template>
          <el-menu-item index="/course-times">节课管理</el-menu-item>
          <el-menu-item index="/courses">课程表管理</el-menu-item>
          <el-menu-item index="/grades">成绩管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/system">
           <template #title>
             <el-icon><Tools /></el-icon>
             <span>系统管理</span>
           </template>
           <el-menu-item index="/users">用户管理</el-menu-item>
           <el-menu-item index="/roles">角色管理</el-menu-item>
           <el-menu-item index="/menus">菜单管理</el-menu-item>
           <el-menu-item index="/logs">日志管理</el-menu-item>
           <el-menu-item index="/config">系统参数</el-menu-item>
        </el-sub-menu>

        <!-- 其他菜单项 -->

      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部 Header -->
      <el-header class="app-header">
        <div>面包屑导航 / 用户信息</div>
        <!-- 可以放置面包屑导航、用户信息、退出按钮等 -->
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
</style>
