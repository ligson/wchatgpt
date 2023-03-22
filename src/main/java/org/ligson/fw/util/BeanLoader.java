package org.ligson.fw.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.ligson.fw.BeanModel;
import org.ligson.fw.BootApplicationContext;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class BeanLoader {
    private final BootApplicationContext bootApplicationContext;
    private final Set<Class<?>> beanClasses;

    public BeanLoader(BootApplicationContext bootApplicationContext, Set<Class<?>> beanClasses) {
        this.bootApplicationContext = bootApplicationContext;
        this.beanClasses = beanClasses;
    }

    private Class<?> findRealClass(Class<?> serviceClass) {
        Class<?> realServiceClass = serviceClass;
        if (serviceClass.isInterface()) {
            for (Class<?> aClass : beanClasses) {
                if (serviceClass.isAssignableFrom(aClass)&&(serviceClass!=aClass)) {
                    realServiceClass = aClass;
                    break;
                }
            }
            if (realServiceClass == serviceClass) {
                log.error("根据接口:{}找不到对应的实现类", serviceClass.getName());
                return null;
            }
            log.debug("类型:{}是个接口，使用具体实现:{}", serviceClass.getName(), realServiceClass.getSimpleName());
        }
        return realServiceClass;
    }

    public Object instanceClass(Class<?> realServiceClass) {
        Object serviceInstance;
        Constructor<?>[] constructors = realServiceClass.getConstructors();
        if (constructors.length == 0) {
            try {
                serviceInstance = realServiceClass.getConstructor().newInstance();
            } catch (Exception e) {
                log.error("服务类:{}实例化失败:{},stack:{}", realServiceClass.getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        } else {
            //find none param constructor
            Constructor<?> noneParamConstructor = null;
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    noneParamConstructor = constructor;
                    break;
                }
            }
            if (noneParamConstructor != null) {
                try {
                    serviceInstance = noneParamConstructor.newInstance();
                } catch (Exception e) {
                    log.error("服务类:{}实例化失败:{},stack:{}", realServiceClass.getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                    throw new RuntimeException(e);
                }
            } else {
                Constructor<?> con = constructors[0];
                Class<?>[] paramTypes = con.getParameterTypes();
                Object[] param = new Object[paramTypes.length];
                int i = 0;
                for (Class<?> paramType : paramTypes) {
                    BeanModel bm = loadBean(paramType);
                    if (bm == null) {
                        throw new RuntimeException(String.format("根据类型%s找不到对应的Bean", paramType.getName()));
                    } else {
                        param[i++] = bm.getBeanInstance();
                    }
                }
                try {
                    serviceInstance = con.newInstance(param);
                } catch (Exception e) {
                    log.error("服务类:{}实例化失败:{},stack:{}", realServiceClass.getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                    throw new RuntimeException(e);
                }
            }
        }
        return serviceInstance;
    }

    private void injectField(Class<?> realServiceClass, Object serviceInstance) {
        Field[] fields = FieldUtils.getFieldsWithAnnotation(realServiceClass, BootAutowired.class);
        for (Field field : fields) {
            BeanModel bm3;
            BootAutowired bootAutowired = field.getAnnotation(BootAutowired.class);
            if (bootAutowired.byType()) {
                bm3 = bootApplicationContext.getOneBeanByClass(field.getType());
            } else {
                if (StringUtils.isNoneBlank(bootAutowired.name())) {
                    bm3 = bootApplicationContext.getBeanModelByName(bootAutowired.name());
                } else {
                    bm3 = bootApplicationContext.getBeanModelByName(field.getName());
                }
            }
            if (bm3 == null) {
                bm3 = loadBean(field.getType());
            }
            try {
                FieldUtils.writeField(field, serviceInstance, bm3.getBeanInstance(), true);
            } catch (IllegalAccessException e) {
                log.error("服务类:{}注入字段:{},失败:{},stack:{}", realServiceClass.getName(), field.getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        }
    }

    public List<BeanModel> loadBeansBySupperClass(Class<?> supperServiceClass){
        List<Class<?>> realServiceClasses = new ArrayList<>();
        for (Class<?> aClass : beanClasses) {
            if (supperServiceClass.isAssignableFrom(aClass)&&aClass!=supperServiceClass) {
                realServiceClasses.add(aClass);
            }
        }
        List<BeanModel> beanModels = new ArrayList<>();
        for (Class<?> realServiceClass : realServiceClasses) {
            beanModels.add(loadBean(realServiceClass));
        }
        return beanModels;
    }
    public BeanModel loadBean(Class<?> serviceClass) {
        BeanModel bm1 = bootApplicationContext.getOneBeanByClass(serviceClass);
        if (bm1 != null) {
            return bm1;
        }

        Class<?> realServiceClass = findRealClass(serviceClass);
        if (realServiceClass != null) {
            BeanModel bm2 = bootApplicationContext.getOneBeanByClass(serviceClass);
            if (bm2 != null) {
                return bm2;
            }
        } else {
            log.error("根据接口:{}找不到对应的实现类", serviceClass.getName());
            throw new RuntimeException(String.format("根据接口:%s找不到对应的实现类", serviceClass.getName()));
        }

        Object serviceInstance = instanceClass(realServiceClass);

        injectField(realServiceClass, serviceInstance);


        BootService bootService = realServiceClass.getAnnotation(BootService.class);
        BeanModel beanModel = new BeanModel();
        beanModel.setBeanInstance(serviceInstance);
        beanModel.setBeanType(serviceInstance.getClass());
        if (StringUtils.isNoneBlank(bootService.name())) {
            beanModel.setBeanName(bootService.name());
        } else {
            String name = Character.toLowerCase(serviceInstance.getClass().getSimpleName().charAt(0)) + serviceInstance.getClass().getSimpleName().substring(1);
            beanModel.setBeanName(name);
        }

        bootApplicationContext.putBean(beanModel.getBeanName(), beanModel);
        return beanModel;
    }
}
