package com.bb.faq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FaqApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaqApplication.class, args);
    }


}
