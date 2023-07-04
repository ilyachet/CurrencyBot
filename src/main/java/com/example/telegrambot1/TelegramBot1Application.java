package com.example.telegrambot1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TelegramBot1Application {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBot1Application.class, args);
    }

}
