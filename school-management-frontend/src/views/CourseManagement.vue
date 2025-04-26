<template>
  <div class="course-schedule-container">
    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filters" ref="filterFormRef">
        <el-form-item label="班级" prop="classId">
           <el-select v-model="filters.classId" placeholder="请选择班级" clearable filterable @change="handleFilterChange">
             <el-option v-for="item in selectOptions.classes" :key="item.id" :label="item.className" :value="item.id" />
           </el-select>
        </el-form-item>
         <el-form-item label="教师" prop="teacherId">
           <el-select v-model="filters.teacherId" placeholder="请选择教师" clearable filterable @change="handleFilterChange">
             <el-option v-for="item in selectOptions.teachers" :key="item.id" :label="item.teacherName" :value="item.id" />
           </el-select>
        </el-form-item>
        <!-- 可以添加更多筛选，如按科目 -->
        <el-form-item>
          <el-button @click="resetFilters" :icon="Refresh">重置筛选</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="operate-card" shadow="never">
       <el-row :gutter="10">
         <el-col :span="1.5">
           <el-button type="primary" plain @click="handleAdd" :icon="Plus">新增课程</el-button>
         </el-col>
         <!-- 可以添加其他按钮，例如批量操作 -->
       </el-row>
    </el-card>

    <!-- 日历区域 -->
    <el-card class="calendar-card" shadow="never">
       <FullCalendar ref="fullCalendarRef" :options="calendarOptions" />
    </el-card>

    <!-- 新增/修改对话框 (占位) -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="600px"
      append-to-body
      :close-on-click-modal="false"
      @close="resetForm"
    >
       <el-form :model="dialog.formData" :rules="rules" ref="courseFormRef" label-width="100px">
         <!-- 表单项将在后续步骤中填充 -->
         <el-form-item label="班级" prop="classId">
            <el-select v-model="dialog.formData.classId" placeholder="请选择班级" filterable :disabled="dialog.isEdit"> <!-- 编辑时通常不允许改班级？-->
                 <el-option v-for="item in selectOptions.classes" :key="item.id" :label="item.className" :value="item.id" />
            </el-select>
         </el-form-item>
         <el-form-item label="科目" prop="subjectId">
             <el-select v-model="dialog.formData.subjectId" placeholder="请选择科目" filterable>
                  <el-option v-for="item in selectOptions.subjects" :key="item.id" :label="item.subjectName" :value="item.id" />
             </el-select>
         </el-form-item>
          <el-form-item label="教师" prop="teacherId">
             <el-select v-model="dialog.formData.teacherId" placeholder="请选择教师" filterable>
                  <el-option v-for="item in selectOptions.teachers" :key="item.id" :label="item.teacherName" :value="item.id" />
             </el-select>
         </el-form-item>
          <el-form-item label="星期" prop="dayOfWeek">
             <el-select v-model="dialog.formData.dayOfWeek" placeholder="请选择星期" :disabled="dialog.fromCalendar"> <!-- 从日历点击时禁用 -->
                  <el-option v-for="(day, index) in weekDays" :key="index+1" :label="day" :value="index+1" />
             </el-select>
         </el-form-item>
         <el-form-item label="节次" prop="courseTimeId">
             <el-select v-model="dialog.formData.courseTimeId" placeholder="请选择节次" :disabled="dialog.fromCalendar"> <!-- 从日历点击时禁用 -->
                 <el-option
                    v-for="item in selectOptions.courseTimes"
                    :key="item.id"
                    :label="`${item.periodName} (${item.startTime}-${item.endTime})`"
                    :value="item.id"
                  />
             </el-select>
         </el-form-item>
          <el-form-item label="上课地点" prop="location">
             <el-input v-model="dialog.formData.location" placeholder="请输入上课地点" />
         </el-form-item>
          <el-form-item label="课程介绍" prop="introduction">
             <el-input v-model="dialog.formData.introduction" type="textarea" placeholder="请输入课程介绍" />
         </el-form-item>
          <el-form-item label="状态" prop="isValid">
             <el-radio-group v-model="dialog.formData.isValid">
                 <el-radio :label="1">有效</el-radio>
                 <el-radio :label="0">无效</el-radio>
             </el-radio-group>
         </el-form-item>
         <!-- remark 继承自 BaseEntity，通常不需要在表单显示 -->
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Plus } from '@element-plus/icons-vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import listPlugin from '@fullcalendar/list'
import {
    listCourses, addCourse, updateCourse, deleteCourse,
    listValidClasses, listValidTeachers, listValidSubjects, listValidCourseTimes
} from '@/api/course'

// --- refs ---
const filterFormRef = ref(null)
const courseFormRef = ref(null)
const fullCalendarRef = ref(null) // 获取 FullCalendar 组件实例

// --- state ---
const loading = ref(false) // 用于日历加载状态
const filters = reactive({ // 筛选条件
    classId: null,
    teacherId: null,
})
const selectOptions = reactive({ // 下拉选项数据
    classes: [],
    teachers: [],
    subjects: [],
    courseTimes: [],
})
const weekDays = ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'];

// 对话框初始数据
const initialFormData = () => ({
    id: null,
    classId: null,
    teacherId: null,
    subjectId: null,
    courseTimeId: null,
    dayOfWeek: null,
    introduction: '',
    location: '',
    isValid: 1,
});

// 对话框状态
const dialog = reactive({
    visible: false,
    title: '',
    isEdit: false,
    fromCalendar: false, // 标记是否通过点击日历触发
    formData: initialFormData()
})

// 表单校验规则
const rules = reactive({
    classId: [{ required: true, message: '班级不能为空', trigger: 'change' }],
    subjectId: [{ required: true, message: '科目不能为空', trigger: 'change' }],
    teacherId: [{ required: true, message: '教师不能为空', trigger: 'change' }],
    dayOfWeek: [{ required: true, message: '星期不能为空', trigger: 'change' }],
    courseTimeId: [{ required: true, message: '节次不能为空', trigger: 'change' }],
    // location 和 introduction 非必填
})
// 点击已有课程事件 (用于编辑/删除)
const handleEventClick = (clickInfo) => {
  console.log('Event clicked:', clickInfo.event);
  const courseData = clickInfo.event.extendedProps; // 获取原始课程数据

  // TODO: 弹出菜单或直接弹出编辑对话框
  ElMessageBox.confirm(`操作课程：${courseData.subjectName} (${courseData.teacherName})`, '课程操作', {
    confirmButtonText: '编辑',
    cancelButtonText: '删除',
    distinguishCancelAndClose: true,
    type: 'info',
  })
    .then(() => { // 点击 编辑
      handleUpdate(courseData);
    })
    .catch((action) => { // 点击 删除 或 关闭
      if (action === 'cancel') {
        handleDelete(courseData);
      }
    });
};
// 自定义事件渲染函数
function renderEventContent(arg) {
    const props = arg.event.extendedProps;
    console.log('renderEventContent 调用:', JSON.stringify(props));
    console.log('renderEventContent arg 调用:', arg);
    //表格视图类型
    const viewType = arg.view.type;
    let htmlContent = '';

    // 根据视图类型决定显示内容
    if (viewType === 'timeGridWeek' || viewType === 'timeGridDay') {
        // 在周/日视图，空间有限，只显示科目和教师
        htmlContent = `
            <span><b>${props.subjectName || '-'}</b></span>
            <span style="font-size: 0.9em;"><small>教师: ${props.teacherName || '-'}</small></span>
        `;
    } else {
        // 在月/列表等其他视图，空间较多，可以显示地点
        htmlContent = `
            <span><b>${props.subjectName || '-'}</b></span><br/>
            <span style="font-size: 0.9em;"><small>教师: ${props.teacherName || '-'}</small></span><br/>
            <span style="font-size: 0.9em;"><small>地点: ${props.location || '-'}</small></span>
        `;
    }

    if (!props.isValid) {
        // 无效标记追加到最后
        htmlContent += `<br/><span style="color: red; font-size: 0.9em;"><small>(无效)</small></span>`;
    }
    return { html: htmlContent };
}

// 处理日历日期范围变化
const handleDatesSet = (dateInfo) => {
  console.log('Dates changed, re-fetching events for:', dateInfo);
  fetchCalendarEvents();
};
// FullCalendar 配置项
const calendarOptions = reactive({
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin, listPlugin],
    initialView: 'timeGridWeek', // 初始视图为周视图
    headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek' // 提供视图切换按钮
    },
    locale: 'zh-cn', // 设置中文
    firstDay: 1, // 设置周一为一周的开始
    slotMinTime: '06:00:00', // 可选：设置时间轴最早时间
    slotMaxTime: '19:00:00', // 可选：设置时间轴最晚时间
    allDaySlot: false, // 不显示全天事件栏
    events: [], // 课程数据将填充这里
    eventContent: renderEventContent, // 自定义事件渲染
    // dateClick: handleDateClick, // 点击日期/时间格子
    eventClick: handleEventClick, // 点击已有事件
    datesSet: handleDatesSet, // 添加 datesSet 回调，当日期范围变化时触发
    // selectable: true, // 允许选择时间范围
    // select: handleDateSelect, // 选择时间范围后的回调
    // eventDrop: handleEventDrop, // 拖拽事件后的回调 (需要 editable: true)
    // eventResize: handleEventResize, // 调整事件大小后的回调 (需要 editable: true)
    // editable: false, // 是否允许拖拽和调整大小
    // eventSources: [ // 可以使用 eventSources 加载事件
    //     {
    //         events: fetchCalendarEvents,
    //     }
    // ],
    // loading: handleCalendarLoading, // 处理日历加载状态的回调
    // aspectRatio: 3, // 调整宽高比，值越小，日历越高。尝试 1.8 或 2
    height: 800, // 或者可以设置固定高度
  // contentHeight: 300, // 设置内容高度
  // handleWindowResize: true, // 自动调整窗口大小
  // allDayText: '全天', // 全日事件的文本
  // eventStartEditable: true, // 允许拖拽事件
  eventMinHeight: 10, // 事件最小高度
  // eventShortHeight: 100, // 事件最小高度
  // slotLabelInterval: '00:30:00', // 时间轴标签间隔
});

// --- methods ---



// 获取所有下拉选项数据
const fetchSelectOptions = async () => {
    try {
        const [classRes, teacherRes, subjectRes, courseTimeRes] = await Promise.all([
            listValidClasses(),
            listValidTeachers(),
            listValidSubjects(),
            listValidCourseTimes()
        ]);
        selectOptions.classes = classRes.data || [];
        selectOptions.teachers = teacherRes.data || [];
        selectOptions.subjects = subjectRes.data || [];
        selectOptions.courseTimes = courseTimeRes.data || [];
        console.log('下拉选项加载成功', JSON.stringify(selectOptions));
    } catch (error) {
        console.error("加载下拉选项失败:", error);
        ElMessage.error('加载筛选/表单选项失败');
    }
};

// 获取并格式化日历事件数据
const fetchCalendarEvents = async () => {
    loading.value = true;
    try {
        const params = { ...filters }; // 加入筛选条件
        // TODO: 可能需要根据日历当前视图的 start/end 调整查询参数
        const response = await listCourses(params);
        const courses = response.data?.records || response.data || []; // 兼容分页和非分页返回
        console.log('原始课程数据 (response.data):', JSON.stringify(response.data));
        console.log('待处理课程数组 (courses):', JSON.stringify(courses));
        if (!Array.isArray(courses)) {
            console.error('从后端获取的课程数据不是数组!', courses);
            calendarOptions.events = [];
            return;
        }
        if (selectOptions.subjects.length === 0 || selectOptions.teachers.length === 0 || selectOptions.courseTimes.length === 0) {
            console.warn('下拉选项数据尚未完全加载，课程事件可能无法正确显示标题或时间。');
        }

        // 将后端数据转换为 FullCalendar 事件格式
        calendarOptions.events = courses.map(course => {
            console.log(`处理课程 ID: ${course.id}`, JSON.stringify(course));
            const subject = selectOptions.subjects.find(s=>s.id === course.subjectId);
            const teacher = selectOptions.teachers.find(t=>t.id === course.teacherId);
            const clazz = selectOptions.classes.find(c=>c.id === course.classId);
            const courseTime = selectOptions.courseTimes.find(ct => ct.id === course.courseTimeId);

            if (!subject) console.warn(`课程 ${course.id}: 找不到科目 ID ${course.subjectId}`);
            if (!teacher) console.warn(`课程 ${course.id}: 找不到教师 ID ${course.teacherId}`);
            if (!clazz) console.warn(`课程 ${course.id}: 找不到班级 ID ${course.classId}`);
            if (!courseTime) console.warn(`课程 ${course.id}: 找不到节次 ID ${course.courseTimeId}`);

            const startTimeStr = courseTime?.startTime;
            const endTimeStr = courseTime?.endTime;

            const startDateTime = calculateDateTime(course.dayOfWeek, startTimeStr);
            const endDateTime = calculateDateTime(course.dayOfWeek, endTimeStr);
            console.log(`课程 ${course.id}: 计算得到 start=${startDateTime}, end=${endDateTime}`);

            return {
                id: course.id,
                title: `${subject?.subjectName || '未知科目'} - ${teacher?.teacherName || '未知教师'}`,
                start: startDateTime,
                end: endDateTime,
                extendedProps: {
                    ...course,
                    className: clazz?.className || '未知班级',
                    teacherName: teacher?.teacherName || '未知教师',
                    subjectName: subject?.subjectName || '未知科目',
                    timePeriod: courseTime?.periodName || '未知节次',
                },
                 color: course.isValid ? undefined : 'grey', // 无效课程灰色显示
            };
        }).filter(event => event.start && event.end); // 过滤掉没有有效开始或结束时间的事件
        console.log('转换后的日历事件:', calendarOptions.events);

    } catch (error) {
        console.error("获取课程数据失败:", error);
        ElMessage.error('加载课程安排失败');
    } finally {
        loading.value = false;
    }
};

// 辅助函数：根据星期和时间字符串计算一个"虚拟"的日期时间
function calculateDateTime(dayOfWeek, timeStr) {
    console.log(`calculateDateTime 调用: dayOfWeek=${dayOfWeek}, timeStr=${timeStr}`);
    if (!timeStr || typeof timeStr !== 'string' || !/^\d{2}:\d{2}:\d{2}$/.test(timeStr)) {
        console.warn(`无效的 timeStr: ${timeStr}, 无法计算日期时间。`);
        return null;
    }
    const calendarApi = fullCalendarRef.value?.getApi();
    if (!calendarApi) {
        console.error('calculateDateTime: FullCalendar API not available.');
        return null;
    }
    const view = calendarApi.view;
    const currentViewDate = new Date(view.currentStart); // 日历当前视图的某个日期

    // --- 计算包含 currentViewDate 的周的起始日 --- 
    const firstDaySetting = calendarOptions.firstDay || 0; // 0=Sun, 1=Mon
    const currentDayOfWeekJS = currentViewDate.getDay(); // JS: 0=Sun, 1=Mon, ..., 6=Sat
    let diff = currentDayOfWeekJS - firstDaySetting;
    if (diff < 0) diff += 7; // 处理周日(0) < 周一(1) 的情况

    const weekStartDate = new Date(currentViewDate);
    weekStartDate.setDate(currentViewDate.getDate() - diff);
    weekStartDate.setHours(0, 0, 0, 0); // 标准化到周起始日的零点
    // --- 周起始日计算完毕 --- 

    console.log(`Current View Date: ${currentViewDate.toISOString()}, Calculated Week Start: ${weekStartDate.toISOString()}`);

    // --- 计算目标日期 --- 
    // 将后端的 dayOfWeek (1=Mon, ..., 7=Sun) 转为 JS 的 getDay (0=Sun, ..., 6=Sat)
    const jsDayOfWeek = dayOfWeek === 7 ? 0 : dayOfWeek; 

    // 计算目标星期几相对于周起始日的偏移天数
    let daysOffset = jsDayOfWeek - weekStartDate.getDay(); // weekStartDate.getDay() 应该是 firstDaySetting (除了跨年等边界情况？)
    // 为确保计算正确，直接用 dayOfWeek 和 firstDaySetting 计算偏移量
    daysOffset = dayOfWeek - firstDaySetting;
    if (daysOffset < 0) daysOffset += 7; 

    let targetDate = new Date(weekStartDate);
    targetDate.setDate(weekStartDate.getDate() + daysOffset);
    // --- 目标日期计算完毕 --- 

    // 组合日期和时间
    const [hours, minutes, seconds] = timeStr.split(':');
    targetDate.setHours(parseInt(hours), parseInt(minutes), parseInt(seconds || '0'));

    const isoString = targetDate.toISOString();
    console.log(`calculateDateTime Result: dayOfWeek=${dayOfWeek}, timeStr=${timeStr} => ${isoString}`);
    return isoString; // 返回 ISO 格式字符串
}



// 筛选条件变化时重新获取数据
const handleFilterChange = () => {
    fetchCalendarEvents();
};

// 重置筛选条件
const resetFilters = () => {
    filterFormRef.value?.resetFields();
    fetchCalendarEvents(); // 重新加载所有数据
};

// 点击日历格子事件 (用于添加)
// const handleDateClick = (arg) => {
//     console.log('Date clicked:', arg);
//     // TODO: 实现点击空白处添加课程
//     // 需要解析 arg.date (日期时间), arg.dateStr
//     // 可能需要反向查找对应的 星期几 和 节课时间段
//     // 弹出新增对话框，并预填时间和星期
// };



// 重置表单
const resetForm = () => {
    dialog.formData = initialFormData();
    courseFormRef.value?.resetFields();
};

// 处理新增 (可以由按钮触发，或后续由 dateClick 触发)
const handleAdd = () => {
    resetForm();
    dialog.isEdit = false;
    dialog.fromCalendar = false;
    dialog.title = '新增课程安排';
    dialog.visible = true;
};

// 处理更新 (由 eventClick 触发)
const handleUpdate = (courseData) => {
    resetForm();
    dialog.formData = { ...courseData }; // 填充表单
    dialog.isEdit = true;
    dialog.fromCalendar = true; // 来自日历，星期和节次可能不允许修改
    dialog.title = '修改课程安排';
    dialog.visible = true;
};

// 处理删除 (由 eventClick 触发)
const handleDelete = (courseData) => {
    ElMessageBox.confirm(`确定要删除课程安排 [${courseData.subjectName} - ${courseData.teacherName}] 吗？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await deleteCourse(courseData.id);
            ElMessage.success('删除成功');
            fetchCalendarEvents(); // 刷新日历
        } catch (error) {
            console.error("删除课程失败:", error);
            ElMessage.error('删除失败');
        }
    }).catch(() => {});
};

// 提交表单 (新增/修改)
const submitForm = async () => {
    if (!courseFormRef.value) return;
    try { // 唯一的 try 块开始
        // 1. 表单校验
        await courseFormRef.value.validate();

        // 2. 准备提交的数据
        const dataToSubmit = { ...dialog.formData };

        // 3. 调用 API (如果失败会进入下面的 catch)
        if (dialog.isEdit) {
            await updateCourse(dataToSubmit);
            ElMessage.success('修改成功');
        } else {
            await addCourse(dataToSubmit);
            ElMessage.success('新增成功');
        }

        // 4. 关闭对话框并刷新
        dialog.visible = false;
        fetchCalendarEvents(); // 刷新日历

    } catch (error) { // <--- 唯一的 catch 块，捕获校验失败或 API 错误
        // 检查错误类型，区分是校验错误还是 API 错误 (可选)
        if (error && Array.isArray(error)) {
            // Element Plus validate 返回错误数组
            console.log('表单校验失败:', error);
            // 通常不需要额外提示 ElMessage.error，组件会自动标红
        } else {
            // 假设其他错误是 API 错误
            console.error("保存课程失败:", error);
            ElMessage.error(error?.msg || (dialog.isEdit ? '修改失败' : '新增失败'));
        }
    }
    // finally 如果需要，可以放在这里
};


// --- lifecycle hooks ---
onMounted(async () => {
    await fetchSelectOptions(); // 先加载下拉选项
    fetchCalendarEvents(); // 再加载课程数据
});

</script>

<style>
/* 引入 FullCalendar 的核心样式 */
/* @import '@fullcalendar/core/main.css'; */ /* MIGRATION: use main.js or index.js? */
/* @import '@fullcalendar/daygrid/main.css'; */
/* @import '@fullcalendar/timegrid/main.css'; */
/* @import '@fullcalendar/list/main.css'; */

.course-schedule-container {
    display: flex;
    flex-direction: column;
    gap: 15px;
    padding: 20px; /* 添加一些内边距 */
}

.filter-card,
.calendar-card {
    border-radius: 4px;
}

/* 自定义 FullCalendar 的一些样式 */
.calendar-card :deep(.fc-header-toolbar) {
    margin-bottom: 1.0em !important; /* 调整 header 和内容的间距 */
}
.calendar-card :deep(.fc-event) {
    cursor: pointer;
    font-size: 0.87em; /* 再次微调字体大小 */
    padding: 2px 4px; /* 减少一点内边距 */
    line-height: 1.4; /* 调回之前的行高试试 */
    border: 1px solid #a1caf1; /* 保留边框 */
    overflow: hidden; /* 保留溢出隐藏 */
    text-overflow: ellipsis;
}

/* 鼠标悬停时可以加点效果 */
.calendar-card :deep(.fc-event:hover) {
    border-color: #409EFF; /* Element Plus 主题蓝 */
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
}

/* 无效课程的样式 (如果使用 classNames) */
/* 保留注释，除非用户确认要启用
.calendar-card :deep(.invalid-course) {
    background-color: #f3f3f3 !important;
    border-color: #dcdcdc !important;
    color: #a0a0a0 !important;
}
.calendar-card :deep(.invalid-course .fc-event-title) {
     text-decoration: line-through;
}
*/

/* 调整对话框内 Select 宽度 */
.el-dialog .el-select {
    width: 100%;
}

</style>
