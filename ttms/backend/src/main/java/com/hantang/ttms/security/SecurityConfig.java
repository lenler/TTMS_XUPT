package com.hantang.ttms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置。
 *
 * <p>配置安全过滤链和密码加密策略。当前为开发/联调阶段采用放宽策略：
 * 禁用 CSRF 保护，所有请求均放行（permitAll）。
 * 认证和授权逻辑在 Controller 层通过自定义的 Mock 鉴权拦截器或
 * Service 层的手动校验实现。</p>
 *
 * <h3>配置要点</h3>
 * <ul>
 *   <li>禁用 CSRF（前后端分离 + RESTful API 不需要）</li>
 *   <li>明确放行 /auth/**、/public/**、/actuator/health 路径</li>
 *   <li>所有请求均放行，不启用 Spring Security 内置认证</li>
 *   <li>密码编码器使用 BCrypt 算法，强度为默认 10</li>
 * </ul>
 *
 * @author XUPT
 */
@Configuration
public class SecurityConfig {

    /**
     * 配置安全过滤链。
     *
     * <p>定义一个 SecurityFilterChain Bean，覆盖 Spring Boot 默认的安全自动配置。</p>
     *
     * @param http HttpSecurity 构建器
     * @return SecurityFilterChain 实例
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/public/**", "/actuator/health").permitAll()
                .anyRequest().permitAll())
            .build();
    }

    /**
     * 密码编码器 Bean。
     *
     * <p>采用 BCrypt 算法，用于用户密码的哈希存储和验证。</p>
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
