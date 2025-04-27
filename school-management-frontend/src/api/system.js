import request from '@/utils/request' // 假设 request 是从 utils 导入的 axios 实例

// 获取系统配置列表
export function listConfigs(params) {
  return request({
    url: '/system/config/list',
    method: 'get',
    params
  })
}

// 根据参数键名获取参数值
export function getConfigByKey(configKey) {
  return request({
    url: `/system/config/key/${configKey}`,
    method: 'get'
  })
}

// 根据ID获取配置详情
export function getConfigById(id) {
  return request({
    url: `/system/config/${id}`,
    method: 'get'
  })
}

// 新增系统配置
export function addConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    data: data
  })
}

// 修改系统配置
export function updateConfig(data) {
  return request({
    url: '/system/config',
    method: 'put',
    data: data
  })
}

// 删除系统配置
export function deleteConfig(id) {
  return request({
    url: `/system/config/${id}`,
    method: 'delete'
  })
} 