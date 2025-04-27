/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 19:21:18
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 19:27:18
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\dto\UserDto.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity.dto;

import com.school.entity.User; // 导入 User 实体
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 用户数据传输对象 (用于新增/修改)
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承 User 的字段
public class UserDto extends User {

    /**
     * 角色ID列表
     */
    private Long[] roleIds;

    // 可以在这里覆盖或添加特定的校验规则
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    @Override
    public String getUserName() {
        return super.getUserName();
    }

    // 如果密码字段在 User 中，可以添加校验
    // @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间")
    // public String getPassword() { ... }
} 