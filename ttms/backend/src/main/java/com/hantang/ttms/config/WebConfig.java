package com.hantang.ttms.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享（CORS）配置。
 *
 * <p>配置前端开发服务器（如 Vite 在 localhost:5173 或 localhost:3000）
 * 跨域访问后端 API 时的白名单策略。允许的来源通过
 * {@code ttms.cors.allowed-origins} 属性配置（默认允许 localhost:5173）。</p>
 *
 * <h3>已启用的 CORS 策略</h3>
 * <ul>
 *   <li>允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）</li>
 *   <li>允许所有请求头</li>
 *   <li>允许携带 Cookie / Authorization 等凭据</li>
 *   <li>对所有路径（/**）生效</li>
 * </ul>
 *
 * @author XUPT
 */
@Configuration
@ConfigurationProperties(prefix = "ttms.cors")
public class WebConfig {

    /** 允许跨域访问的前端来源地址列表，默认为 localhost:5173 */
    private List<String> allowedOrigins = List.of("http://localhost:5173");

    /**
     * 获取允许跨域访问的来源列表。
     *
     * @return 来源地址列表
     */
    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * 设置允许跨域访问的来源列表（由配置文件注入）。
     *
     * @param allowedOrigins 来源地址列表
     */
    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * 注册 CORS 过滤器 Bean。
     *
     * <p>Spring Boot 会优先使用此 CorsFilter 而非默认的 WebMvcConfigurer CORS 配置。</p>
     *
     * @return CorsFilter 实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
