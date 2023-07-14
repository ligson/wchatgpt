package org.ligson.ichat.fw.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

public class ServiceLocator {
    private static BeanFactory beanFactory;

    public static <T> T getBean(Class<T> requiredType) {
        if (beanFactory == null) {
            return null;
        }
        return beanFactory.getBean(requiredType);
    }

    public static <T> T getServiceByName(String name) {
        if (beanFactory == null) {
            return null;
        }
        return (T) beanFactory.getBean(name);
    }

    @Component
    @Lazy(value = false)
    static class Aware implements BeanFactoryAware {

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            ServiceLocator.beanFactory = beanFactory;
        }
    }
}
