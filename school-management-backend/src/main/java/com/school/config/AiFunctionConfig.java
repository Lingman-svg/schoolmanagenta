/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 21:16:29
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:01:13
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\config\AiFunctionConfig.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.config;

import com.school.entity.Subject;
import com.school.entity.ai.SubjectInfoRequest;
import com.school.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;
import com.school.entity.Clazz;
import com.school.entity.Student;
import com.school.entity.ai.StudentInfo;
import com.school.entity.ai.StudentListByClassRequest;
import com.school.service.ClazzService;
import com.school.service.StudentService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.school.entity.Teacher;
import com.school.entity.ai.TeacherInfoRequest;
import com.school.service.TeacherService;
import com.school.entity.Course;
import com.school.entity.CourseTime;
import com.school.entity.ai.CourseInfo;
import com.school.entity.ai.CourseScheduleRequest;
import com.school.service.CourseService;
import com.school.service.CourseTimeService;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.school.entity.Grade;
import com.school.entity.ai.StudentGradeInfo;
import com.school.entity.ai.StudentGradeRequest;
import com.school.service.GradeService;
import java.math.BigDecimal;
import com.school.entity.ai.TeacherInfo;
import com.school.entity.TeacherSubject;
import com.school.mapper.SubjectMapper;
import com.school.mapper.TeacherSubjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Configuration
@Slf4j
public class AiFunctionConfig {

    @Bean
    @Description("Get details about a specific school subject by its name") // Description for the AI
    public Function<SubjectInfoRequest, Subject> getSubjectInfoByNameFunction(SubjectService subjectService) {
        return request -> {
            log.info("AI Function Call: getSubjectInfoByName for subject '{}'", request.getSubjectName());
            Subject subject = subjectService.lambdaQuery()
                    .eq(Subject::getSubjectName, request.getSubjectName())
                    .one(); // Find subject by name

            if (subject == null) {
                log.warn("Subject '{}' not found for AI function call.", request.getSubjectName());
                // You might return null, an empty Subject, or throw an exception.
                // Returning null might confuse the AI less, let's return a placeholder/empty object
                 Subject notFound = new Subject();
                 notFound.setSubjectName(request.getSubjectName());
                 notFound.setRemark("Subject not found in the system."); // Add a note
                 return notFound;
            }
            log.info("Subject found: {}", subject);
            return subject;
        };
    }

    // Add more @Bean definitions here for other functions
    @Bean
    @Description("Get a list of students (name and student number) belonging to a specific class by the class name") // Description for AI
    public Function<StudentListByClassRequest, List<StudentInfo>> getStudentsByClassNameFunction(
            ClazzService clazzService, StudentService studentService) {
        return request -> {
            String className = request.getClassName();
            log.info("AI Function Call: getStudentsByClassName for class '{}'", className);

            // 1. Find the class by name
            Clazz clazz = clazzService.lambdaQuery()
                    .eq(Clazz::getClassName, className) // Assuming Clazz has getName()
                    .one();

            if (clazz == null) {
                log.warn("Class '{}' not found for AI function call.", className);
                // Return empty list if class not found
                return Collections.emptyList();
            }

            // 2. Find students by class ID
            List<Student> students = studentService.lambdaQuery()
                    .eq(Student::getCurrentClazzId, clazz.getId()) // Assuming Student has getClazzId() and Clazz has getId()
                    .list();

            // 3. Convert to simplified DTO for AI response
            List<StudentInfo> studentInfos = students.stream()
                    .map(student -> new StudentInfo(student.getStudentName(), student.getStudentNumber())) // Assuming getters
                    .collect(Collectors.toList());

            log.info("Found {} students in class '{}'.", studentInfos.size(), className);
            return studentInfos;
        };
    }

    @Bean
    @Description("Get details (name, phone, gender, teaching subjects) about a specific teacher by their name") // Updated description
    public Function<TeacherInfoRequest, TeacherInfo> getTeacherInfoByNameFunction(
            TeacherService teacherService,
            TeacherSubjectMapper teacherSubjectMapper, // Inject needed Mappers/Services
            SubjectMapper subjectMapper) {
        return request -> {
            String teacherName = request.getTeacherName();
            log.info("AI Function Call: getTeacherInfoByNameFunction for teacher '{}'", teacherName);

            Teacher teacher = teacherService.lambdaQuery()
                    .eq(Teacher::getTeacherName, teacherName) // Correct getter method reference
                    .one();

            if (teacher == null) {
                log.warn("Teacher '{}' not found for AI function call.", teacherName);
                return new TeacherInfo(teacherName, null, null, null, "Teacher not found in the system."); // Return DTO with message
            }

            // Fetch associated subject names
            List<String> subjectNames = Collections.emptyList();
            List<TeacherSubject> relations = teacherSubjectMapper.selectList(
                    new LambdaQueryWrapper<TeacherSubject>().eq(TeacherSubject::getTeacherId, teacher.getId())
            );
            if (!relations.isEmpty()) {
                List<Long> subjectIds = relations.stream().map(TeacherSubject::getSubjectId).collect(Collectors.toList());
                List<Subject> subjects = subjectMapper.selectBatchIds(subjectIds);
                subjectNames = subjects.stream().map(Subject::getSubjectName).collect(Collectors.toList());
            }

            log.info("Teacher found: {}. Subjects: {}", teacher.getTeacherName(), subjectNames);
            return new TeacherInfo(
                    teacher.getTeacherName(),
                    teacher.getPhone(),
                    teacher.getGender(),
                    subjectNames,
                    "Teacher details retrieved successfully."
            );
        };
    }

    @Bean
    @Description("Get the course schedule for a specific class on a given day of the week (e.g., Monday, Tuesday, 星期一, 星期二)")
    public Function<CourseScheduleRequest, List<CourseInfo>> getCourseScheduleByClassAndDayFunction(
            ClazzService clazzService,
            CourseService courseService,
            CourseTimeService courseTimeService,
            TeacherService teacherService,
            SubjectService subjectService) {
        return request -> {
            String className = request.getClassName();
            String dayOfWeekStr = request.getDayOfWeek();
            log.info("AI Function Call: getCourseSchedule for class '{}' on '{}'", className, dayOfWeekStr);

            // 1. Find the class by name
            Clazz clazz = clazzService.lambdaQuery().eq(Clazz::getClassName, className).one();
            if (clazz == null) {
                log.warn("Class '{}' not found for AI function call.", className);
                return Collections.singletonList(new CourseInfo("班级未找到", null, null, null)); // Indicate class not found
            }

            // 2. Convert day of week string to integer (1-7, assuming 1=Monday)
            Integer dayOfWeekInt = convertDayOfWeekToInt(dayOfWeekStr);
            if (dayOfWeekInt == null) {
                log.warn("Invalid day of week provided: '{}'", dayOfWeekStr);
                 return Collections.singletonList(new CourseInfo("无效的星期", null, null, null)); // Indicate invalid day
            }

            // 3. Find courses for the class on that day
            List<Course> courses = courseService.lambdaQuery()
                    .eq(Course::getClassId, clazz.getId())
                    .eq(Course::getDayOfWeek, dayOfWeekInt)
                    .orderByAsc(Course::getCourseTimeId) // Order by time slot
                    .list();

            if (courses.isEmpty()) {
                log.info("No courses found for class '{}' on day {}", className, dayOfWeekInt);
                return Collections.emptyList(); // No courses scheduled
            }

            // 4. Fetch related details and map to DTO
            List<CourseInfo> schedule = new ArrayList<>();
            for (Course course : courses) {
                Subject subject = course.getSubjectId() != null ? subjectService.getById(course.getSubjectId()) : null;
                Teacher teacher = course.getTeacherId() != null ? teacherService.getById(course.getTeacherId()) : null;
                CourseTime courseTime = course.getCourseTimeId() != null ? courseTimeService.getById(course.getCourseTimeId()) : null;

                String timeDesc = courseTime != null ?
                     String.format("%s (%s-%s)", courseTime.getPeriodName(), courseTime.getStartTime(), courseTime.getEndTime()) : "未知时间";

                schedule.add(new CourseInfo(
                        subject != null ? subject.getSubjectName() : "未知科目",
                        teacher != null ? teacher.getTeacherName() : "未知教师",
                        timeDesc,
                        course.getLocation() != null ? course.getLocation() : "未知地点"
                ));
            }

            log.info("Found {} courses for class '{}' on day {}", schedule.size(), className, dayOfWeekInt);
            return schedule;
        };
    }

    // Helper method to convert various day representations to integer (1-7)
    private Integer convertDayOfWeekToInt(String dayOfWeekStr) {
        if (dayOfWeekStr == null || dayOfWeekStr.isBlank()) {
            return null;
        }
        String lowerDay = dayOfWeekStr.toLowerCase().replace("星期", "").replace("周", "");
        Map<String, Integer> dayMap = new HashMap<>();
        dayMap.put("一", 1); dayMap.put("1", 1); dayMap.put("mon", 1); dayMap.put("monday", 1);
        dayMap.put("二", 2); dayMap.put("2", 2); dayMap.put("tue", 2); dayMap.put("tuesday", 2);
        dayMap.put("三", 3); dayMap.put("3", 3); dayMap.put("wed", 3); dayMap.put("wednesday", 3);
        dayMap.put("四", 4); dayMap.put("4", 4); dayMap.put("thu", 4); dayMap.put("thursday", 4);
        dayMap.put("五", 5); dayMap.put("5", 5); dayMap.put("fri", 5); dayMap.put("friday", 5);
        dayMap.put("六", 6); dayMap.put("6", 6); dayMap.put("sat", 6); dayMap.put("saturday", 6);
        dayMap.put("日", 7); dayMap.put("天", 7); dayMap.put("7", 7); dayMap.put("sun", 7); dayMap.put("sunday", 7);

        return dayMap.get(lowerDay);
    }

    @Bean
    @Description("Get a student's grade for a specific subject. You need to provide the student's name or student number, and the subject name.")
    public Function<StudentGradeRequest, StudentGradeInfo> getStudentGradeFunction(
            StudentService studentService,
            SubjectService subjectService,
            GradeService gradeService) {
        return request -> {
            String studentIdentifier = request.getStudentIdentifier();
            String subjectName = request.getSubjectName();
            log.info("AI Function Call: getStudentGradeFunction for student '{}' and subject '{}'", studentIdentifier, subjectName);

            // 1. Find the student (try by number first, then by name)
            Student student = studentService.lambdaQuery()
                    .eq(Student::getStudentNumber, studentIdentifier) // Check by number
                    .one();
            if (student == null) {
                student = studentService.lambdaQuery()
                        .eq(Student::getStudentName, studentIdentifier) // Check by name
                        .one(); // For simplicity, take the first match if names are not unique
            }

            if (student == null) {
                log.warn("Student '{}' not found for AI function call.", studentIdentifier);
                return new StudentGradeInfo(studentIdentifier, null, subjectName, null, "Student not found.");
            }

            // 2. Find the subject by name
            Subject subject = subjectService.lambdaQuery()
                    .eq(Subject::getSubjectName, subjectName)
                    .one();

            if (subject == null) {
                log.warn("Subject '{}' not found for AI function call.", subjectName);
                return new StudentGradeInfo(student.getStudentName(), student.getStudentNumber(), subjectName, null, "Subject not found.");
            }

            // 3. Find the grade
            // Assuming one grade per student per subject (might need refinement for terms/exams)
            Grade grade = gradeService.lambdaQuery()
                    .eq(Grade::getStudentId, student.getId())
                    .eq(Grade::getSubjectId, subject.getId())
                    .one(); // Get the single grade entry

            if (grade == null || grade.getScore() == null) {
                log.info("Grade not found for student '{}' in subject '{}'.", student.getStudentName(), subjectName);
                return new StudentGradeInfo(student.getStudentName(), student.getStudentNumber(), subjectName, null, "Grade not found for this subject.");
            }

            log.info("Grade found: Student='{}', Subject='{}', Score={}", student.getStudentName(), subjectName, grade.getScore());
            return new StudentGradeInfo(
                    student.getStudentName(),
                    student.getStudentNumber(),
                    subjectName,
                    grade.getScore(), // Assuming Grade has getScore() returning BigDecimal
                    "Grade retrieved successfully."
            );
        };
    }
} 