/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 22:18:04
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:22:02
 * @FilePath: \schoolmanagenta\school-management-frontend\src\api\system\log.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import request from '@/utils/request'

// 查询操作日志列表
export function listLog(query) {
  return request({
    url: '/system/log/list',
    method: 'get',
    params: query
  })
}

// 删除操作日志 (如果需要)
// export function delLog(logId) {
//   return request({
//     url: '/system/log/' + logId,
//     method: 'delete'
//   })
// }

// 清空操作日志 (如果需要)
// export function cleanLog() {
//   return request({
//     url: '/system/log/clean',
//     method: 'delete'
//   })
// } 