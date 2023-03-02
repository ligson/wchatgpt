package org.ligson.fw;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BootApplication {
    private static BootApplication bootApplication;
    private Map<String, BeanModel> beanContainer = new HashMap<>();

    public static BootApplication getInstance() {
        if (bootApplication == null) {
            bootApplication = new BootApplication();
        }
        return bootApplication;
    }

    public void putBean(String name, BeanModel beanModel) {
        log.debug("bean(name:{},type:{},instanceHash={})加载成功", name, beanModel.getBeanType().getSimpleName(), beanModel.getBeanInstance().hashCode());
        beanContainer.put(name, beanModel);
    }

    public BeanModel getBeanModelByName(String name) {
        return beanContainer.get(name);
    }

    public BeanModel getOneBeanByClass(Class<?> clazz) {
        for (BeanModel value : beanContainer.values()) {
            if (value.getBeanType() == clazz) {
                return value;
            }
        }
        return null;
    }
}
