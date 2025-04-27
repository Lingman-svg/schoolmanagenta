import request from '@/utils/request'; // 引入封装的 Axios

/**
 * 发送聊天消息给后端 AI 接口
 * @param {string} message 用户消息
 * @returns Promise<AxiosResponse<R<string>>> R 是后端统一响应体
 */
export function sendChatMessage(message) {
  return request({
    url: '/ai/chat', // 对应后端的 @RequestMapping("/ai") 和 @PostMapping("/chat")
    method: 'post',
    data: { message } // 后端 @RequestBody Map<String, String> 需要这个结构
  });
} 