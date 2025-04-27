package com.school.entity.ai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourseScheduleRequest {

    @JsonPropertyDescription("The name of the class (e.g., '一年级一班') to get the schedule for")
    private String className;

    @JsonPropertyDescription("The day of the week (e.g., '星期一', '周二', 'Wednesday', 'Friday') for the schedule")
    private String dayOfWeek;
} 