package org.ligson.fw.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BootAutowired {
    boolean byType() default true;

    String name() default "";
}
