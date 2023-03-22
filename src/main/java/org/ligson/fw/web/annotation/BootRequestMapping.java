package org.ligson.fw.web.annotation;

import org.ligson.fw.http.enums.HttpMethod;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BootRequestMapping {
    HttpMethod method() default HttpMethod.GET;

    String value() default "";
}
