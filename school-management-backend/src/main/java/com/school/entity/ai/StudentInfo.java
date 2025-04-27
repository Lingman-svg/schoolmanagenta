/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 21:24:12
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 21:24:29
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\ai\StudentInfo.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfo {
    private String studentName;
    private String studentNumber;
    // Add other relevant fields if needed, e.g., gender
} 