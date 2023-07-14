package org.ligson.ichat.fw.simplecrud.service;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableRestCrud {
    Class<? extends CrudService> value();
}
