package com.nvminh162.jobhunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

// Tắt Spring Security
/* @SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
}) */
@SpringBootApplication
@EnableAsync // bật tính năng async
@EnableScheduling // enable cron job
public class JobhunterBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobhunterBackendApplication.class, args);
    }
}
