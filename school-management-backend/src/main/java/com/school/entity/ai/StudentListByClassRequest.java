package com.school.entity.ai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentListByClassRequest {

    @JsonPropertyDescription("The name of the class (e.g., '一年级一班') to list students from") // Description for AI
    private String className;
} 