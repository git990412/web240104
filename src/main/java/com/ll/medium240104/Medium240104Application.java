package com.ll.medium240104;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Medium240104Application {

    public static void main(String[] args) {
        SpringApplication.run(Medium240104Application.class, args);
    }

}
