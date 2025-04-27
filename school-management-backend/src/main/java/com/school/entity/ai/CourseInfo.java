package com.school.entity.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseInfo {
    private String subjectName;
    private String teacherName;
    private String courseTimeDescription; // e.g., "第一节 (08:00-08:45)"
    private String location;
    // Consider adding dayOfWeek if the response list might contain multiple days
} 