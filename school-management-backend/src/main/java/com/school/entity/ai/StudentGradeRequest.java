package com.school.entity.ai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentGradeRequest {

    @JsonPropertyDescription("The name or student number of the student")
    private String studentIdentifier;

    @JsonPropertyDescription("The name of the subject to get the grade for")
    private String subjectName;

    // Optional: Add fields for term or exam name if needed for more specific queries
    // @JsonPropertyDescription("The term or academic period (e.g., '2024上学期')")
    // private String term;
}
