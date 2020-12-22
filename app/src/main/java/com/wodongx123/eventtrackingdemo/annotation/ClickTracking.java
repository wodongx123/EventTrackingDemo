package com.wodongx123.eventtrackingdemo.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于记录点击事件的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@BaseTracking(type = "clickEvent", id = "40000")
public @interface ClickTracking {
    String value() default "";
}
