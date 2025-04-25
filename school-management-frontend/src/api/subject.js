import request from '@/utils/request' // 引入封装的 axios 实例

// 分页查询科目列表
export function listSubjects(params) {
  return request({
    url: '/api/subjects',
    method: 'get',
    params: params // get 请求使用 params 传递查询参数
  })
}

// 查询所有有效科目 (用于下拉)
export function listValidSubjects() {
    return request({
        url: '/api/subjects/valid',
        method: 'get'
    })
}

// 查询科目详细
export function getSubject(id) {
  return request({
    url: '/api/subjects/' + id,
    method: 'get'
  })
}

// 新增科目
export function addSubject(data) {
  return request({
    url: '/api/subjects',
    method: 'post',
    data: data // post 请求使用 data 传递请求体
  })
}

// 修改科目
export function updateSubject(data) {
  return request({
    url: '/api/subjects',
    method: 'put',
    data: data // put 请求使用 data 传递请求体
  })
}

// 删除科目
export function deleteSubject(id) {
  return request({
    url: '/api/subjects/' + id,
    method: 'delete'
  })
}

// 批量删除科目
export function deleteSubjects(ids) {
    return request({
        url: '/api/subjects/batch',
        method: 'delete',
        data: ids // delete 请求也可以传递 data
    })
}


// 导出科目
export function exportSubjects(params) {
  return request({
    url: '/api/subjects/export',
    method: 'post',
    data: params, // post 请求传递查询条件
    responseType: 'blob' // 表明返回的是文件流
  }).then(res => {
    // 处理文件下载
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);

    // 尝试从 Content-Disposition 获取文件名
    // 注意：需要后端在 exportSubjects 方法中设置响应头 Access-Control-Expose-Headers
    // 并且 Content-Disposition 的 filename 需要是 urlencoded
    // let filename = '科目数据.xlsx'; // 默认文件名
    // const contentDisposition = response.headers['content-disposition'];
    // if (contentDisposition) {
    //     const filenameRegex = /filename[^;=\n]*=((['"])(.*?)\2|[^;\n]*)/;
    //     const matches = filenameRegex.exec(contentDisposition);
    //     if (matches != null && matches[3]) {
    //         filename = decodeURIComponent(matches[3].replace(/\+/g, '%20'));
    //     }
    // }

    link.download = '科目数据.xlsx'; // 设置下载的文件名
    link.click();
    window.URL.revokeObjectURL(link.href);
  })
}

// 导入科目
export function importSubjects(formData) {
    return request({
        url: '/api/subjects/import',
        method: 'post',
        data: formData, // 传递 FormData 对象
        headers: {
            'Content-Type': 'multipart/form-data' // 需要设置正确的 Content-Type
        }
    })
} 