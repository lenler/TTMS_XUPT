package com.hantang.ttms.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Customer;

public interface CustomerRepository {
    @Select("SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status FROM customers ORDER BY id")
    List<Customer> findAll();

    @Select("SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status FROM customers WHERE id = #{id}")
    Customer selectById(Long id);

    default Optional<Customer> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    @Select("SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status FROM customers WHERE username = #{username}")
    Customer selectByUsername(String username);

    default Optional<Customer> findByUsername(String username) {
        return Optional.ofNullable(selectByUsername(username));
    }

    @Select("""
        SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status
        FROM customers
        WHERE username LIKE CONCAT('%', #{username}, '%') OR name LIKE CONCAT('%', #{name}, '%')
        ORDER BY id
        """)
    List<Customer> findByUsernameContainingOrNameContaining(@Param("username") String username, @Param("name") String name);

    @Select("SELECT COUNT(*) FROM customers WHERE username = #{username}")
    long countByUsername(String username);

    default boolean existsByUsername(String username) {
        return countByUsername(username) > 0;
    }

    @Insert("""
        INSERT INTO customers (username, password_hash, name, phone, email, gender, payment_password, balance, status, created_at, updated_at)
        VALUES (#{username}, #{passwordHash}, #{name}, #{phone}, #{email}, #{gender}, #{paymentPassword}, #{balance}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Customer customer);

    @Update("""
        UPDATE customers
        SET username = #{username}, password_hash = #{passwordHash}, name = #{name}, phone = #{phone},
            email = #{email}, gender = #{gender}, payment_password = #{paymentPassword}, balance = #{balance}, status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Customer customer);

    default Customer save(Customer customer) {
        if (customer.getId() == null) {
            insert(customer);
        } else {
            update(customer);
        }
        return customer;
    }
}
