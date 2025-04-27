import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus' // 引入 Element Plus 消息提示
import { useUserStore } from '@/stores/user'; // 导入 Pinia store
// import { getToken } from '@/utils/auth' // 假设有获取 token 的工具函数

// 1. 创建 Axios 实例
const service = axios.create({
  // baseURL: process.env.VUE_APP_BASE_API, // .env 文件中配置的基础 URL
  baseURL: 'http://localhost:8080', // 暂时硬编码后端地址，后续应改为配置方式
  timeout: 10000, // 请求超时时间 (毫秒)
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
})

// 2. 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    const userStore = useUserStore(); // 在拦截器内部获取 store 实例
    const hasToken = userStore.token;

    if (hasToken) {
      // 让每个请求携带自定义token 请根据实际情况自行修改
      config.headers['Authorization'] = 'Bearer ' + hasToken;
    }

    // get 请求映射 params 参数，防止缓存
    if (config.method === 'get' && config.params) {
      let url = config.url + '?' + tansParams(config.params)
      url = url.slice(0, -1)
      config.params = {}
      config.url = url
    }

    return config
  },
  (error) => {
    // 对请求错误做些什么
    console.error('Request Error:', error) // for debug
    return Promise.reject(error)
  },
)

// 3. 响应拦截器
service.interceptors.response.use(
  (response) => {
    // 打印 Content-Type 方便调试
    console.log('Response Content-Type:', response.headers['content-type']);
    console.log('Request ResponseType:', response.request?.responseType);

    // 检查是否是文件下载响应
    const isBlobRequest = response.request?.responseType === 'blob';
    const contentType = response.headers['content-type']?.toLowerCase() || '';
    const isFileDownload = isBlobRequest || 
                           contentType.includes('application/octet-stream') ||
                           contentType.includes('spreadsheetml') || // .xlsx
                           contentType.includes('ms-excel'); // .xls

    if (isFileDownload) {
        console.log('Detected as file download, returning full response.');
        // 如果是文件下载，直接返回整个 response 对象
        return response; 
    }

    // --- 不是文件下载，按标准 JSON 处理 ---
    const res = response.data; 

    // 增加健壮性：检查 res 是否是有效对象且有 code 属性
    if (typeof res !== 'object' || res === null || typeof res.code === 'undefined') {
         console.error("Received non-standard JSON response:", res);
         ElMessage({ message: '服务器响应格式错误', type: 'error', duration: 5 * 1000 });
         return Promise.reject(new Error('服务器响应格式错误'));
    }

    // 如果 code 不是 200，判断为错误
    if (res.code !== 200) {
      ElMessage({
        message: res.msg || 'Error',
        type: 'error',
        duration: 5 * 1000,
      });

      // 50008: 非法 token; 50012: 其他客户端登录; 50014: Token 过期;
      // 可以根据后端的特定错误码处理，例如 token 失效时重新登录
      if (
        res.code === 401 /* 假设 401 为未授权 */ ||
        res.code === 50008 ||
        res.code === 50012 ||
        res.code === 50014
      ) {
        // 重新登录
        ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          // store.dispatch('user/resetToken').then(() => {
          //   location.reload() // 为了重新实例化 vue-router 对象 避免 bug
          // })
          console.log('Token expired, redirect to login...')
          // 这里应跳转到登录页
          // router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
        })
      }
      // 返回被拒绝的 Promise
      return Promise.reject(new Error(res.msg || 'Error'));
    } else {
      // code 为 200，返回响应体 { code, msg, data }
      // 这里保持返回 res，而不是 res.data，让调用方根据需要解构
      return res; 
    }
  },
  (error) => {
    console.error('Response Error:', error) // for debug
    let message = error.message
    if (message == 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
      message = '系统接口' + message.substr(message.length - 3) + '异常'
    }

    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000,
    })
    return Promise.reject(error)
  },
)

/**
 * 参数处理
 * @param {*} params  参数
 */
function tansParams(params) {
  let result = ''
  for (const propName of Object.keys(params)) {
    const value = params[propName]
    var part = encodeURIComponent(propName) + '='
    if (value !== null && value !== '' && typeof value !== 'undefined') {
      if (typeof value === 'object') {
        for (const key of Object.keys(value)) {
          if (value[key] !== null && value[key] !== '' && typeof value[key] !== 'undefined') {
            let params = propName + '[' + key + ']'
            var subPart = encodeURIComponent(params) + '='
            result += subPart + encodeURIComponent(value[key]) + '&'
          }
        }
      } else {
        result += part + encodeURIComponent(value) + '&'
      }
    }
  }
  return result
}

// 4. 导出 Axios 实例
export default service
