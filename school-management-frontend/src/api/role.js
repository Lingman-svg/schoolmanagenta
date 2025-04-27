import request from '@/utils/request'

// 查询角色列表 (分页)
export function listRole(query) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params: query
  })
}

// 查询角色详细
export function getRole(roleId) {
  return request({
    url: '/system/role/' + roleId,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data: data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data: data
  })
}

// 角色状态修改
export function changeRoleStatus(id, isValid) {
  const data = {
    id,
    isValid
  }
  return request({
    url: '/system/role/changeStatus',
    method: 'put',
    data: data
  })
}

// 删除角色
export function delRole(roleId) {
   // 如果是批量删除，roleId 应该是数组，URL 也需要调整
   // 这里假设是单个删除，与后端 @DeleteMapping("/{roleIds}") 对应可能需要调整
   // 暂时按照单个删除实现，后端接口可能需要修改或前端调用时传递数组
   if (Array.isArray(roleId)) {
       // 如果确实是批量删除
       const ids = roleId.join(','); // 或者根据后端接收方式调整
       return request({
           url: '/system/role/' + ids,
           method: 'delete'
       });
   } else {
       return request({
           url: '/system/role/' + roleId,
           method: 'delete'
       });
   }
}

// 查询角色下拉列表 (所有)
export function optionselect() {
    return request({
        url: '/system/role/optionselect',
        method: 'get'
    });
}

// 导出角色列表 (如果需要)
// export function exportRole(query) {
//   return request({
//     url: '/system/role/export',
//     method: 'post', // 或 get，根据后端实现
//     params: query, // 或 data
//     responseType: 'blob'
//   })
// } 