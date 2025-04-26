/**
 * 根据后端返回的 Blob 数据下载文件
 * @param {Blob} blobData Blob 数据
 * @param {string} defaultFileName 默认文件名 (如果响应头中没有找到)
 */
export function downloadFile(blobData, defaultFileName) {
  try {
    // Check if blobData is already a Blob
    if (!(blobData instanceof Blob)) {
      console.error('Invalid data provided to downloadFile. Expected a Blob.');
      throw new Error('下载数据格式错误');
    }
    
    // Directly use the provided Blob data
    const blob = blobData; // Use the blobData directly
    
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;

    // 尝试从 blobData 的 type 获取文件类型，或使用通用类型
    // 注意：如果后端 API 返回的是 AxiosResponse，需要从 response.headers 获取 content-disposition
    // 但这里假设调用者已经处理了 AxiosResponse，只传入了 Blob 数据
    let fileName = defaultFileName;
    // 如果 blobData 包含了文件名信息（例如通过 fetch API 的 Response 对象），可以尝试读取
    // if (blobData.headers && blobData.headers.get('content-disposition')) { ... }
    // 但标准 Blob 对象没有 headers，所以通常文件名需要调用者处理或传入

    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(downloadUrl);
  } catch (error) {
    console.error('Download file error:', error);
    // 可以考虑在这里抛出错误或使用 ElMessage 提示用户
    // import { ElMessage } from 'element-plus'; // 需要在组件外使用时特殊处理
    // ElMessage.error('文件下载失败');
    throw new Error('文件下载失败'); // 抛出错误让调用者处理
  }
}

/**
 * 从响应头解析文件名
 * @param {object} headers 响应头对象 (例如 Axios response.headers)
 * @param {string} defaultFileName 默认文件名
 * @returns {string} 解析出的文件名或默认文件名
 */
export function getFileNameFromHeaders(headers, defaultFileName) {
    let fileName = defaultFileName;
    const contentDisposition = headers['content-disposition'];
    if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename\*?=utf-8''([^;]+)|filename="?([^;]+)"?/i);
        if (fileNameMatch && (fileNameMatch[1] || fileNameMatch[2])) {
            fileName = decodeURIComponent(fileNameMatch[1] || fileNameMatch[2]);
        }
    }
    return fileName;
} 