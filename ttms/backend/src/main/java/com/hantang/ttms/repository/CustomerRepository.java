package com.hantang.ttms.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Customer;

/**
 * 观众（客户）数据访问层（MyBatis Mapper）。
 *
 * <p>负责 customers 表的 CRUD 操作，提供按 ID、用户名查询，按用户名或姓名
 * 模糊搜索，以及用户名存在性检查等功能。支持观众余额字段的持久化。</p>
 *
 * @author XUPT
 */
public interface CustomerRepository {

    /**
     * 查询全部观众列表，按 ID 排序。
     *
     * @return 全部观众列表
     */
    @Select("SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status FROM customers ORDER BY id")
    List<Customer> findAll();

    /**
     * 根据观众 ID 查询单个观众。
     *
     * @param id 观众主键 ID
     * @return 观众实体，未找到返回 null
     */
    @Select("SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status FROM customers WHERE id = #{id}")
    Customer selectById(Long id);

    /**
     * 根据观众 ID 查询单个观众，返回 Optional 包装。
     *
     * @param id 观众主键 ID
     * @return 观众的 Optional 对象
     */
    default Optional<Customer> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    /**
     * 根据用户名查询观众（用于登录认证）。
     *
     * @param username 用户名
     * @return 观众实体，未找到返回 null
     */
    @Select("SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status FROM customers WHERE username = #{username}")
    Customer selectByUsername(String username);

    /**
     * 根据用户名查询观众，返回 Optional 包装。
     *
     * @param username 用户名
     * @return 观众的 Optional 对象
     */
    default Optional<Customer> findByUsername(String username) {
        return Optional.ofNullable(selectByUsername(username));
    }

    /**
     * 按用户名或姓名模糊搜索观众（支持管理端搜索）。
     *
     * <p>用户名和姓名均使用 LIKE 模糊匹配。如果某参数不需要作为条件，
     * 传入空字符串或 null 即可（CONCAT 会处理 null 情况）。</p>
     *
     * @param username 用户名关键词
     * @param name     姓名关键词
     * @return 匹配的观众列表
     */
    @Select("""
        SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status
        FROM customers
        WHERE username LIKE CONCAT('%', #{username}, '%') OR name LIKE CONCAT('%', #{name}, '%')
        ORDER BY id
        """)
    List<Customer> findByUsernameContainingOrNameContaining(@Param("username") String username, @Param("name") String name);

    /**
     * 统计指定用户名的观众数量（用于重名检查）。
     *
     * @param username 用户名
     * @return 该用户名的记录数
     */
    @Select("SELECT COUNT(*) FROM customers WHERE username = #{username}")
    long countByUsername(String username);

    /**
     * 检查指定用户名是否已存在。
     *
     * @param username 用户名
     * @return true 表示用户名已存在
     */
    default boolean existsByUsername(String username) {
        return countByUsername(username) > 0;
    }

    /**
     * 新增一名观众。
     *
     * <p>插入后通过 {@link Options#useGeneratedKeys} 自动回填主键 ID。</p>
     *
     * @param customer 观众实体
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO customers (username, password_hash, name, phone, email, gender, payment_password, balance, status, created_at, updated_at)
        VALUES (#{username}, #{passwordHash}, #{name}, #{phone}, #{email}, #{gender}, #{paymentPassword}, #{balance}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Customer customer);

    /**
     * 更新观众信息。
     *
     * @param customer 观众实体
     * @return 影响行数
     */
    @Update("""
        UPDATE customers
        SET username = #{username}, password_hash = #{passwordHash}, name = #{name}, phone = #{phone},
            email = #{email}, gender = #{gender}, payment_password = #{paymentPassword}, balance = #{balance}, status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Customer customer);

    /**
     * 保存观众记录（新增或更新）。
     *
     * <p>ID 为空时执行 insert，否则执行 update。</p>
     *
     * @param customer 观众实体
     * @return 保存后的观众实体（含回填的 ID）
     */
    default Customer save(Customer customer) {
        if (customer.getId() == null) {
            insert(customer);
        } else {
            update(customer);
        }
        return customer;
    }
}
