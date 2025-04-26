import request from '@/utils/request'

// 分页查询节课时间列表
export function listCourseTimes(params) {
  return request({
    url: '/api/course-times', // 与 CourseTimeController 的 @RequestMapping 匹配
    method: 'get',
    params: params // get 请求传递查询参数 (CourseTimeQuery)
  })
}

// 查询节课时间详细 (如果需要)
export function getCourseTime(id) {
  return request({
    url: '/api/course-times/' + id,
    method: 'get'
  })
}

// 新增节课时间
export function addCourseTime(data) {
  return request({
    url: '/api/course-times',
    method: 'post',
    data: data // post 请求传递 CourseTime 对象
  })
}

// 修改节课时间
export function updateCourseTime(data) {
  return request({
    url: '/api/course-times',
    method: 'put',
    data: data // put 请求传递 CourseTime 对象
  })
}

// 删除节课时间
export function deleteCourseTime(id) {
  return request({
    url: '/api/course-times/' + id,
    method: 'delete'
  })
}

// 批量删除节课时间
export function deleteCourseTimes(ids) {
    return request({
        url: '/api/course-times/batch',
        method: 'delete',
        data: ids // delete 请求传递 ID 列表
    })
}

// 查询所有有效的节课时间 (如果需要，例如用于下拉选择)
// export function listValidCourseTimes() {
//     return request({
//         url: '/api/course-times/valid', // 假设后端有此接口
//         method: 'get'
//     })
// } 