package com.ravey.common.service.feign;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({FeignAutoConfig.class})
public @interface EnableFeignConfig {
}
