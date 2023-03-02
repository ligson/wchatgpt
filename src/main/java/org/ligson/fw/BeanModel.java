package org.ligson.fw;

import lombok.Data;

@Data
public class BeanModel {
    private Class<?> beanType;
    private Object beanInstance;
    private String beanName;
}
