/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 20:10:37
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 20:22:06
 * @FilePath: \schoolmanagenta\school-management-frontend\src\api\login.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import request from '@/utils/request';

// 登录方法
export function login(username, password) {
  const data = {
    username,
    password
  };
  return request({
    url: '/login', // 登录接口路径
    method: 'post',
    data: data
  });
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/getInfo', // 获取用户信息接口路径
    method: 'get'
  });
}

// 登出方法
export function logout() {
  return request({
    url: '/logout', // 登出接口路径
    method: 'post'
  });
}

// 获取用户详细信息 (如果需要单独接口)
// export function getInfo() {
//   return request({
//     url: '/getInfo', // 获取用户信息接口路径
//     method: 'get'
//   });
// } 