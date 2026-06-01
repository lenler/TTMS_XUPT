package com.hantang.ttms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.hantang.ttms.repository")
public class TtmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TtmsApplication.class, args);
    }
}
