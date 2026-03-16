package com.dailystar;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dailystar.dao")
public class DailyStarApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyStarApplication.class, args);
    }
}
