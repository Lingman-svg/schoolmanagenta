package com.school.entity.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherInfo {
    private String teacherName;
    private String phone;
    private String gender;
    private List<String> teachingSubjects; // List of subject names
    private String message; // For notes like "Teacher not found" or other info
} 