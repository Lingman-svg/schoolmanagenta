import request from '@/utils/request'

// 查询学生列表 (分页)
export function getStudentList(params) {
  return request({
    url: '/student/list',
    method: 'get',
    params
  })
}

// 获取学生详细信息
export function getStudentInfo(id) {
  return request({
    url: `/student/${id}`,
    method: 'get'
  })
}

// 新增学生
export function addStudent(data) {
  return request({
    url: '/student',
    method: 'post',
    data
  })
}

// 修改学生信息
export function updateStudent(data) {
  return request({
    url: '/student',
    method: 'put',
    data
  })
}

// 删除学生 (逻辑删除)
export function deleteStudent(id) {
  return request({
    url: `/student/${id}`,
    method: 'delete'
  })
}

// 批量删除学生 (逻辑删除)
export function deleteStudentsBatch(ids) {
  return request({
    url: '/student/batch',
    method: 'delete',
    data: ids // 通常后端期望ID列表在请求体中
  })
}

// 获取学生班级变更历史
export function getStudentClassHistory(studentId) {
  return request({
    url: `/student/${studentId}/history`,
    method: 'get'
  })
}

// 导出学生数据 - 注意：导出通常不由普通 API 函数处理，而是直接在页面组件中通过 request 发起 blob 请求
// export function exportStudents(params) { ... }

// 获取学生导入模板 - 后端需要提供相应接口
// export function getStudentImportTemplate() { ... } 