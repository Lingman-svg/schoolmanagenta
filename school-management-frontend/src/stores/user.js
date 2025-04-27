import { defineStore } from 'pinia';
import { ref } from 'vue';
import { login as loginApi, logout as logoutApi, getInfo as getInfoApi } from '@/api/login'; // 导入 login, logout 和 getInfo API
// import { getUserInfo } from '@/api/user'; // 暂时注释掉未使用的 getUserInfo
import Cookies from 'js-cookie'; // 使用 js-cookie 处理 Token 存储

const TOKEN_KEY = 'Admin-Token'; // 定义 Cookie key

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(Cookies.get(TOKEN_KEY) || ''); // 从 Cookie 初始化 Token
  const userInfo = ref({}); // 用户信息，例如 name, avatar, roles, permissions

  // Actions
  async function login(loginForm) {
    return new Promise((resolve, reject) => {
        loginApi(loginForm.username.trim(), loginForm.password).then(response => {
            // 假设登录成功后，response.data 直接是 token 字符串
            // 或者 response.data 是 { code: 200, msg: '...', data: { token: '...' } }
            // 需要根据实际后端返回调整
            const responseData = response.data; // 假设 R.data 是 token
            if (responseData) {
                const newToken = responseData; // 直接获取 token
                token.value = newToken;
                Cookies.set(TOKEN_KEY, newToken); // Token 存入 Cookie
                resolve(); // 返回成功
            } else {
                 // 处理后端 R.code 非 200 但 HTTP 状态码是 200 的情况
                reject(response.msg || '登录失败');
            }
        }).catch(error => {
            console.error("Login API Error:", error);
            reject(error?.response?.data?.msg || error.message || '登录时发生错误');
        });
    });
  }

  // 获取用户信息 (通常在登录成功后或页面刷新时调用)
  async function getInfo() {
     return new Promise((resolve, reject) => {
         getInfoApi().then(response => {
             // 后端返回 R.data = { user: {...}, roles: [...], permissions: [...] }
             const resData = response.data;
             if (!resData || !resData.user) {
                 return reject('获取用户信息失败: 无效的响应数据');
             }
             
             const user = resData.user; // 后端返回的 user 对象
             const roles = resData.roles || []; // 角色标识 Set
             const permissions = resData.permissions || []; // 权限标识 Set

             // 保存用户信息 (根据需要选择字段)
             userInfo.value = {
                 id: user.id,
                 name: user.nickName || user.userName, // 优先使用昵称
                 avatar: user.avatar,
                 roles: Array.from(roles), // 转换为数组
                 permissions: Array.from(permissions) // 转换为数组
             }; 
             resolve(resData); // 返回完整的响应数据供路由守卫等使用
         }).catch(error => {
             console.error("Get Info API Error:", error);
             reject(error?.response?.data?.msg || error.message || '获取用户信息时发生错误');
         });
     });
  }

  // 登出
  async function logout() {
    return new Promise((resolve, reject) => { // 添加 reject
        logoutApi().then(() => {
            token.value = '';
            userInfo.value = {};
            Cookies.remove(TOKEN_KEY);
            // TODO: 清除其他可能存在的用户状态/缓存
            resolve();
        }).catch(error => {
            // 即使登出接口失败，前端也应该清除状态
            token.value = '';
            userInfo.value = {};
            Cookies.remove(TOKEN_KEY);
            console.error("Logout API Error:", error);
            reject(error); // 抛出错误供调用方处理
        });
    });
  }

  // Getters (隐式，可以直接访问 token.value, userInfo.value)

  return { token, userInfo, login, getInfo, logout };
}); 