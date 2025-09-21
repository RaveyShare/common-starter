package com.ravey.common.service.web.annotation;

import com.ravey.common.service.web.autoconfigure.GlobalExceptionHandlerAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({GlobalExceptionHandlerAutoConfig.class})
public @interface EnableGlobalExceptionHandler {
}
