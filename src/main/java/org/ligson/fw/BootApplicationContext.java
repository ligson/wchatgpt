package org.ligson.fw;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BootApplicationContext {
    private static BootApplicationContext bootApplicationContext;
    private final Map<String, BeanModel> beanContainer = new ConcurrentHashMap<>();

    public static BootApplicationContext getInstance() {
        if (bootApplicationContext == null) {
            bootApplicationContext = new BootApplicationContext();
        }
        return bootApplicationContext;
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
            if (clazz.isInterface()) {
                if (value.getBeanType().isAssignableFrom(clazz)) {
                    return value;
                }
            } else {
                if (value.getBeanType() == clazz) {
                    return value;
                }
            }
        }
        return null;
    }
}
