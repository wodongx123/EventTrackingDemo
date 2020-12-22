package com.wodongx123.eventtrackingdemo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 区分操作类型的注解的基类
 * 标记不同类型的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface BaseTracking {
    // 类型
    String type();
    // 类型对应的Id
    String id();
}
