import request from '@/utils/request'

// 分页或根据条件查询课程安排列表
// 注意：后端当前 findCoursesByPage 实现可能需要调整以更好地支持日历视图 (如按周/月范围查询)
export function listCourses(params) {
  return request({
    url: '/api/courses',
    method: 'get',
    params: params // 可能包含 classId, teacherId, dayOfWeek, 或者 startDate, endDate 等
  })
}

// 查询课程安排详细 (如果需要单独获取)
export function getCourse(id) {
  return request({
    url: '/api/courses/' + id,
    method: 'get'
  })
}

// 新增课程安排
export function addCourse(data) {
  return request({
    url: '/api/courses',
    method: 'post',
    data: data
  })
}

// 修改课程安排
export function updateCourse(data) {
  return request({
    url: '/api/courses',
    method: 'put',
    data: data
  })
}

// 删除课程安排
export function deleteCourse(id) {
  return request({
    url: '/api/courses/' + id,
    method: 'delete'
  })
}

// 批量删除课程安排 (如果日历视图需要此功能)
export function deleteCourses(ids) {
    return request({
        url: '/api/courses/batch',
        method: 'delete',
        data: ids
    })
}

// --- 获取用于下拉选择的数据 ---
// (这些可能已在其他 api 文件中定义，按需引入或在这里重新定义)

// 获取所有有效班级列表 (用于下拉)
export function listValidClasses() {
    return request({
        url: '/clazz/valid', // 确认后端有此接口
        method: 'get'
    })
}

// 获取所有有效教师列表 (用于下拉)
export function listValidTeachers() {
    return request({
        url: '/teacher/valid', // 确认后端有此接口
        method: 'get'
    })
}

// 获取所有有效科目列表 (用于下拉)
export function listValidSubjects() {
    return request({
        url: '/api/subjects/valid', // 确认后端有此接口
        method: 'get'
    })
}

// 获取所有有效节课时间列表 (用于下拉)
export function listValidCourseTimes() {
    return request({
        // 假设后端提供 /api/course-times/valid 接口
        url: '/api/course-times/valid', // 确认后端有此接口
        method: 'get'
    })
}
