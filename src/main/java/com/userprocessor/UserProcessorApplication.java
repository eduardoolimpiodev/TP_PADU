package com.userprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class UserProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProcessorApplication.class, args);
    }
}
