package com.hantang.ttms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 汉唐剧院票务管理系统（TTMS）— Spring Boot 应用入口。
 *
 * <p>通过 {@code @SpringBootApplication} 启用自动配置、组件扫描，
 * 通过 {@code @MapperScan} 扫描 MyBatis Mapper 接口（位于 repository 包）。</p>
 *
 * <h3>启动方式</h3>
 * <pre>{@code
 * # 开发环境
 * mvn spring-boot:run
 *
 * # 指定端口
 * mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8080"
 * }</pre>
 *
 * <p>启动后访问 {@code http://localhost:8080/}，健康检查端点：
 * {@code http://localhost:8080/actuator/health}</p>
 *
 * @author XUPT
 */
@SpringBootApplication
@MapperScan("com.hantang.ttms.repository")
public class TtmsApplication {

    /**
     * 应用主入口方法。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TtmsApplication.class, args);
    }
}
