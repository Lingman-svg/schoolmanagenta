/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 19:30:29
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 19:35:01
 * @FilePath: \schoolmanagenta\school-management-frontend\src\api\user.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import request from '@/utils/request'

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(userId) {
  return request({
    url: '/user/' + userId,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/user',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUser(userIds) {
  // 如果 userId 是单个 ID，确保它是数组或路径能处理
  return request({
    url: '/user/' + userIds, // 直接传递数组或单个ID
    method: 'delete'
  })
}

// 用户密码重置
export function resetUserPwd(id, password) {
  const data = {
    id,
    password
  }
  return request({
    url: '/user/resetPwd',
    method: 'put',
    data: data
  })
}

// 用户状态修改
export function changeUserStatus(id, isValid) {
  const data = {
    id,
    isValid
  }
  return request({
    url: '/user/changeStatus',
    method: 'put',
    data: data
  })
}

// 查询角色列表 (如果弹窗需要显示所有角色)
// export function listRoles() { ... } 