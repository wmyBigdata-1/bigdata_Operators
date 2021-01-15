package com.wmy.ct.common.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/12
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String familiy() default "info"; // 默认值，这个属性叫familiy，值为info
    String column() default "";
}
