package com.chileme;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.chileme.mapper")
public class ChileMeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChileMeApplication.class, args);
    }
}
