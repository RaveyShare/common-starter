package com.ravey.common.service.web.autoconfigure;


import com.ravey.common.service.web.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalExceptionHandlerAutoConfig {
    public GlobalExceptionHandlerAutoConfig() {
    }

    @Bean
    public GlobalExceptionHandler exceptionHandler() {
        return new GlobalExceptionHandler();
    }
}

