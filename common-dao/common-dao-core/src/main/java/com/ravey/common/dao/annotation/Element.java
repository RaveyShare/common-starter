package com.ravey.common.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Element {
    String authType() default "SHOP";

    String column() default "";

    String tableAlias() default "";

    boolean ignoreNull() default false;

    String dataSourceType() default "MYSQL";
}
