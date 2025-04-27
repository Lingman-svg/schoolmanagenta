package com.school.entity.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeInfo {
    private String studentName;
    private String studentNumber;
    private String subjectName;
    private BigDecimal score; // Use BigDecimal for scores
    private String message; // Optional field for errors or notes like "Grade not found"
} 