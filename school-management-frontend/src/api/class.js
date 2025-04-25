import request from '@/utils/request'

// 获取班级列表 (分页)
export function getClassList(params) {
  return request({
    url: '/clazz/list',
    method: 'get',
    params
  })
}

// 获取班级详情
export function getClassInfo(id) {
  return request({
    url: `/clazz/${id}`,
    method: 'get'
  })
}

// 新增班级
export function addClass(data) {
  return request({
    url: '/clazz',
    method: 'post',
    data
  })
}

// 修改班级
export function updateClass(data) {
  return request({
    url: '/clazz',
    method: 'put',
    data
  })
}

// 删除班级
export function deleteClass(id) {
  return request({
    url: `/clazz/${id}`,
    method: 'delete'
  })
}

// 批量删除班级
export function deleteClassesBatch(ids) {
  return request({
    url: '/clazz/batch',
    method: 'delete',
    data: ids // Send IDs in the request body for batch delete
  })
}

// 导出班级数据 (此函数可能不需要，因为导出通常直接通过 request 发起)
// export function exportClasses(params) {
//   return request({
//     url: '/clazz/export',
//     method: 'post',
//     data: params,
//     responseType: 'blob'
//   })
// }

// 注意: 导入通常在组件内直接使用 upload 组件或发起 request，可能不需要单独的 API 函数 