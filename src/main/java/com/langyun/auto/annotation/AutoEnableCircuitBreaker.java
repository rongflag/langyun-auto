package com.langyun.auto.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * EnableCircuitBreaker
 *
 * @author langyun
 * @date 2021年06月09日 0:31
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoEnableCircuitBreaker {

}
