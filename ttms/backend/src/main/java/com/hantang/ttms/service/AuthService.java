package com.hantang.ttms.service;

import com.hantang.ttms.dto.AuthRequest;
import com.hantang.ttms.dto.AuthResponse;
import com.hantang.ttms.dto.RegisterCustomerRequest;
import com.hantang.ttms.dto.UserResponse;

/**
 * 认证业务服务接口
 *
 * 处理管理端和观众端的登录认证与注册：
 * - 管理端：员工通过工号 + 密码登录
 * - 观众端：客户通过用户名 + 密码登录
 * - 观众注册：新客户自助注册
 *
 * 认证成功后返回用户信息和 JWT Token（或 Session）
 */
public interface AuthService {
    /**
     * 统一登录入口
     *
     * 根据请求中的角色标识自动识别管理端（员工）或观众端（客户）登录：
     * - 管理端登录：校验工号 + BCrypt 密码匹配
     * - 观众端登录：校验用户名 + BCrypt 密码匹配
     * - 校验账户状态（ACTIVE 才可登录）
     *
     * @param request 登录请求（账号、密码、角色标识）
     * @return 认证响应（用户信息、Token、菜单权限等）
     */
    AuthResponse login(AuthRequest request);

    /**
     * 观众自助注册
     *
     * 校验用户名唯一性 → BCrypt 加密密码 → 创建客户记录
     * 初始余额为 0，状态为 ACTIVE
     *
     * @param request 注册请求（用户名、密码、姓名、电话等）
     * @return 注册成功后的用户信息
     */
    UserResponse registerCustomer(RegisterCustomerRequest request);
}
