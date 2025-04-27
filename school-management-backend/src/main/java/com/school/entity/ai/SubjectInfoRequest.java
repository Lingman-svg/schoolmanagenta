package com.school.entity.ai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubjectInfoRequest {

    @JsonPropertyDescription("The name of the subject to get information about") // Description for the AI
    private String subjectName;
} 