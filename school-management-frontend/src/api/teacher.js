/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 23:17:13
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 00:56:19
 * @FilePath: \schoolmanagenta\school-management-frontend\src\api\teacher.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import request from '@/utils/request'; // 假设 request 实例已封装好

// 获取教师分页列表
export function getTeacherList(params) {
  return request({
    url: '/teacher/list',
    method: 'get',
    params // 使用 GET 请求时，参数通常放在 params
  });
}

// 根据 ID 获取教师详情
export function getTeacherInfo(id) {
  return request({
    url: `/teacher/${id}`,
    method: 'get'
  });
}

// 新增教师
export function addTeacher(data) {
  return request({
    url: '/teacher',
    method: 'post',
    data // 使用 POST 请求时，数据通常放在 data
  });
}

// 修改教师
export function updateTeacher(data) {
  return request({
    url: '/teacher',
    method: 'put',
    data // 使用 PUT 请求时，数据通常放在 data
  });
}

// 删除教师
export function deleteTeacher(id) {
  return request({
    url: `/teacher/${id}`,
    method: 'delete'
  });
}

// 批量删除教师
export function deleteTeachersBatch(ids) {
  // 将 ID 列表转换为逗号分隔的字符串，或者根据后端 @PathVariable 的处理方式调整
  // Spring MVC 通常能自动处理 List<Long> 到路径变量的映射，如果不行再改为逗号分隔
  const idString = ids.join(','); 
  return request({
    // url: `/teacher/batch/${ids}`, // 直接传递数组可能也行
    url: `/teacher/batch/${idString}`,
    method: 'delete'
  });
}

// 导出教师数据 - 注意：导出通常不是一个标准的 API 请求返回 JSON
// 它会触发文件下载。可以直接构造 URL 或使用 request 实例处理 blob 数据
// 这里提供一个构造 URL 的示例，可能需要根据实际情况调整
export function exportTeacherDataUrl() {
  // 假设后端基础 URL 已在 request 中配置
  // 注意：导出通常用 POST 请求携带查询条件，但浏览器直接打开 URL 是 GET
  // 如果后端 export 接口是 POST，需要换一种方式触发下载（例如 form 提交或 fetch blob）
  // 假设后端是 POST，这里返回基础路径，让前端组件处理下载逻辑
  return '/teacher/export'; // 返回基础路径，让调用方处理参数和请求
  // 或者返回完整 GET URL (如果后端支持 GET 导出): `/teacher/export?${queryString}`;
}


// 导入教师数据 - 需要使用 FormData
export function importTeacherData(file) {
  const formData = new FormData();
  formData.append('file', file);

  return request({
    url: '/teacher/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data' // 重要：设置正确的 Content-Type
    }
  });
}

// (可选) 获取所有科目列表，用于教师表单中的科目选择
// export function getAllSubjects() {
//   return request({
//     url: '/subject/all', // 假设有这样一个接口
//     method: 'get'
//   });
// }

// 获取所有有效教师列表 (用于下拉)
export function listValidTeachers() {
  return request({
    url: '/teacher/valid', // Assuming a backend endpoint /teacher/valid
    method: 'get'
  })
} 