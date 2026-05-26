package com.ttms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TTMS 后端服务启动入口。
 */
@SpringBootApplication
public class TtmsBackendApplication {

    /**
     * 启动 TTMS 后端 Spring Boot 应用。
     *
     * @param args 命令行启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TtmsBackendApplication.class, args);
    }
}
