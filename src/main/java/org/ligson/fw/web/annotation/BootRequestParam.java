package org.ligson.fw.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BootRequestParam {
    String name() default "";

    String defaultValue() default "";

    boolean required() default false;
}
