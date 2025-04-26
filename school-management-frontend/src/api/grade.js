import request from '@/utils/request';

// 获取成绩分页列表 (返回 DTO)
export function getGradeList(params) {
  return request({
    url: '/grade/list',
    method: 'get',
    params
  });
}

// 获取成绩详情 (返回 Grade 实体)
export function getGradeInfo(id) {
  return request({
    url: `/grade/${id}`,
    method: 'get'
  });
}

// 新增成绩
export function addGrade(data) {
  return request({
    url: '/grade',
    method: 'post',
    data
  });
}

// 修改成绩
export function updateGrade(data) {
  return request({
    url: '/grade',
    method: 'put',
    data
  });
}

// 删除成绩
export function deleteGrade(id) {
  return request({
    url: `/grade/${id}`,
    method: 'delete'
  });
}

// 批量删除成绩
export function deleteGradesBatch(ids) {
  return request({
    url: '/grade/batch',
    method: 'delete',
    data: ids // 将 ID 列表放在请求体中
  });
}

// 获取成绩导入模板下载地址 (注意：这不是直接调用下载，而是获取 URL)
// 通常前端会直接使用 a 标签或 window.open 指向这个地址
export const getGradeTemplateUrl = () => {
    // 假设 request.defaults.baseURL 存储了基础 API 地址
    const baseUrl = request.defaults.baseURL || import.meta.env.VITE_APP_BASE_API;
    return `${baseUrl}/grade/template`;
};

// 获取成绩导入接口地址
export const getGradeImportUrl = () => {
    const baseUrl = request.defaults.baseURL || import.meta.env.VITE_APP_BASE_API;
    return `${baseUrl}/grade/import`;
};

// 导出成绩数据 (返回 Blob)
export function exportGrades(data) {
  return request({
    url: '/grade/export',
    method: 'post',
    data, // 查询条件放在请求体
    responseType: 'blob' // 重要：指定响应类型为 Blob
  });
} 