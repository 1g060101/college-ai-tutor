package com.aistudy.tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.aistudy.tutor")
public class AiTutorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiTutorApplication.class, args);
    }
}