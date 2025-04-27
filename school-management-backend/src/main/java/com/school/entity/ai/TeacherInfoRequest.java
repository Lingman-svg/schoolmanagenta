package com.school.entity.ai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeacherInfoRequest {

    @JsonPropertyDescription("The name of the teacher to get information about") // Description for AI
    private String teacherName;
} 