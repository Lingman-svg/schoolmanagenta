/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 19:53:38
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 19:54:52
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\AuthService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录认证
     *
     * @param username 用户名
     * @param password 密码
     * @return 认证成功后的凭证 (例如 JWT Token)，如果认证失败则抛出异常
     * @throws Exception 认证失败时抛出异常 (例如 AuthenticationException)
     */
    String login(String username, String password) throws Exception;

} 