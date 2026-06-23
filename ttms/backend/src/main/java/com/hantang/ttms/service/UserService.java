package com.hantang.ttms.service;

import java.util.List;

import com.hantang.ttms.dto.CustomerRequest;
import com.hantang.ttms.dto.EmployeeRequest;
import com.hantang.ttms.dto.UserResponse;

/**
 * 用户管理业务服务接口
 *
 * 负责管理端的员工管理和观众管理：
 *
 * 员工管理：
 * - 列表查询（按姓名/工号模糊搜索）
 * - 新增员工（工号唯一校验）
 * - 修改员工信息
 * - 禁用员工（逻辑删除）
 *
 * 客户管理：
 * - 列表查询（按用户名/姓名模糊搜索）
 * - 新增客户（用户名唯一校验）
 * - 修改客户信息
 * - 禁用客户（逻辑删除，禁用后不可登录购票）
 */
public interface UserService {
    /**
     * 查询员工列表
     *
     * @param keyword 搜索关键词（匹配姓名或工号），为空时返回全部
     * @return 员工列表
     */
    List<UserResponse> listEmployees(String keyword);

    /**
     * 新增员工
     *
     * 校验工号唯一性 → BCrypt 加密密码 → 创建员工记录
     *
     * @param request 员工创建请求（工号、姓名、职位、密码等）
     * @return 创建后的员工信息
     */
    UserResponse createEmployee(EmployeeRequest request);

    /**
     * 修改员工信息
     *
     * 可修改姓名、职位、电话、邮箱等字段
     *
     * @param id 员工 ID
     * @param request 修改请求
     * @return 更新后的员工信息
     */
    UserResponse updateEmployee(Long id, EmployeeRequest request);

    /**
     * 禁用员工（逻辑删除）
     *
     * 将状态设为 DISABLED，保留历史售票记录
     *
     * @param id 员工 ID
     */
    void disableEmployee(Long id);

    /**
     * 查询客户列表
     *
     * @param keyword 搜索关键词（匹配用户名或姓名），为空时返回全部
     * @return 客户列表
     */
    List<UserResponse> listCustomers(String keyword);

    /**
     * 新增客户（管理端代为创建）
     *
     * 校验用户名唯一性 → BCrypt 加密密码 → 创建客户记录
     *
     * @param request 客户创建请求
     * @return 创建后的客户信息
     */
    UserResponse createCustomer(CustomerRequest request);

    /**
     * 修改客户信息
     *
     * @param id 客户 ID
     * @param request 修改请求
     * @return 更新后的客户信息
     */
    UserResponse updateCustomer(Long id, CustomerRequest request);

    /**
     * 禁用客户（逻辑删除）
     *
     * 将状态设为 DISABLED，禁用后不可登录和购票
     *
     * @param id 客户 ID
     */
    void disableCustomer(Long id);
}
