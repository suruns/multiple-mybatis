package com.suruns.multiple.mybatis.annotation;

import java.lang.annotation.*;

/**
 * 更换数据源为读书源的注解.
 * @author suruns
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ReadOnly {
}
