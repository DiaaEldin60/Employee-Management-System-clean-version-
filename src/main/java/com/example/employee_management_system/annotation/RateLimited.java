package com.example.employee_management_system.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/// by me
/// creating custom annotation for rate limiting
/// @Target(ElementType.METHOD) - annotation can be applied to methods
/// @Retention(RetentionPolicy.RUNTIME) - annotation is retained at runtime
/// @interface RateLimited - custom annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    int limit() default 100;
    int period() default 60; // in seconds
}
