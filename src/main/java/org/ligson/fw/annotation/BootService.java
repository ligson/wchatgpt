package org.ligson.fw.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BootService {
    String name() default "";

    String initMethod() default "";

    String destoryMethod() default "";
}
