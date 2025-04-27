/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 21:35:19
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 20:30:59
 * @FilePath: \schoolmanagenta\school-management-frontend\src\main.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

// --- Element Plus --- (需要先安装: npm install element-plus)
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 如果需要中文语言包
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
// 导入所有 Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// --- VxeTable --- 
// 引入 VxeTable 核心样式
import 'vxe-table/lib/style.css'
// 引入 Vxe PC UI 库（可能是为了补充组件或解决特定加载问题）
import VxeUIAll from 'vxe-pc-ui' 
import 'vxe-pc-ui/lib/style.css'
// 引入 VxeTable 核心库（与 VxeUIAll 结合使用）
import VxeUITable from 'vxe-table' 

import App from './App.vue'
import router from './router'
import permissionDirective from './directives/permission'; // 导入权限指令

// --- Axios (后面会封装) --- (需要先安装: npm install axios)
// import axios from 'axios'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 全局注册权限指令
app.directive('permission', permissionDirective);

// --- 注册 Element Plus ---
// size 用于设置表单组件的默认尺寸，locale 用于设置语言
app.use(ElementPlus, { locale: zhCn, size: 'default' }) // 或者 'large', 'small'

// --- 注册 Element Plus Icons ---
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// --- 注册 Vxe 相关库 ---
// 注意: 这种同时注册 VxeUIAll 和 VxeUITable 的方式可能特定于解决遇到的加载问题。
// 标准 VxeTable v4 通常推荐直接 import VXETable from 'vxe-table' 并 app.use(VXETable)。
app.use(VxeUIAll) // 注册 Vxe PC UI 库
app.use(VxeUITable) // 注册 VxeTable 核心库

// --- 全局配置 Axios (可选，通常在封装层配置) ---
// app.config.globalProperties.$axios = axios
// app.config.globalProperties.$http = axios // 或者用 $http

app.mount('#app')
