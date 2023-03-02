package org.ligson.fw;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.ligson.fw.annotation.*;
import org.ligson.fw.util.ClazzUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class FwBooter {
    private static final BootApplication bootApplication = BootApplication.getInstance();
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void run(Class<?> clazz, String[] args) {

        BootApp bootApp = clazz.getAnnotation(BootApp.class);
        if (bootApp != null) {
            String[] pkgs = bootApp.packages();

            //Reflections reflections = new Reflections((Object[]) pkgs);
            //Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(pkgs).addScanners(new SubTypesScanner()).addScanners(new FieldAnnotationsScanner()));
            //Set<String> types = reflections.getAll(Scanners.SubTypes);
            Set<String> types = new HashSet<>();
            for (String pkg : pkgs) {
                types.addAll(ClazzUtils.getClazzName(pkg, true));
            }


            Set<Class> projectClasses = new HashSet<>();
            for (String type : types) {
                boolean match = false;
                for (String pkg : pkgs) {
                    if (type.startsWith(pkg)) {
                        match = true;
                        break;
                    }
                }
                if (match) {
                    try {
                        projectClasses.add(Class.forName(type));
                    } catch (ClassNotFoundException e) {
                        log.error("加载类型:{}失败:{},stack:{}", type, e.getMessage(), ExceptionUtils.getStackTrace(e));
                        throw new RuntimeException(e);
                    }
                }
            }

            //scan config
            Set<Class<?>> configClasses = new HashSet<>();
            Set<Class<?>> serviceClasses = new HashSet<>();

            for (Class<?> projectClass : projectClasses) {
                BootConfig bootConfig = projectClass.getAnnotation(BootConfig.class);
                if (bootConfig != null) {
                    configClasses.add(projectClass);
                }

                BootService bootService = projectClass.getAnnotation(BootService.class);
                if (bootService != null) {
                    serviceClasses.add(projectClass);
                }

            }
            //config bean
            for (Class<?> configClass : configClasses) {
                Object configInstance;
                try {
                    configInstance = configClass.getConstructor().newInstance();
                } catch (Exception e) {
                    log.error("配置类:{}实例化失败:{},stack:{}", configClass.getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                    throw new RuntimeException(e);
                }
                List<Method> methods = MethodUtils.getMethodsListWithAnnotation(configClass, BootBean.class);
                for (Method method : methods) {
                    Object beanInstance;
                    try {
                        beanInstance = method.invoke(configInstance);
                    } catch (Exception e) {
                        log.error("配置类:{}调用方法:{}失败:{},stack:{}", configClass.getName(), method.toGenericString(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                        throw new RuntimeException(e);
                    }
                    BeanModel beanModel = new BeanModel();
                    beanModel.setBeanInstance(beanInstance);
                    beanModel.setBeanType(beanInstance.getClass());
                    beanModel.setBeanName(method.getName());
                    bootApplication.putBean(beanModel.getBeanName(), beanModel);
                }
            }
            //service
            for (Class<?> serviceClass : serviceClasses) {
                BeanModel bm = loadService(serviceClass, serviceClasses);
                log.debug("bean加载成功，类型:{},名称:{}", serviceClass.getSimpleName(), bm.getBeanName());
            }
            //service init
            for (Class<?> serviceClass : serviceClasses) {
                BootService bootService = serviceClass.getAnnotation(BootService.class);
                if (StringUtils.isNoneBlank(bootService.initMethod())) {
                    executorService.submit(() -> {
                        BeanModel bm = bootApplication.getOneBeanByClass(serviceClass);
                        try {
                            MethodUtils.invokeMethod(bm.getBeanInstance(), bootService.initMethod());
                            log.debug("服务类:{}调用初始化方法:{}调用成功", serviceClass.getName(), bootService.initMethod());
                        } catch (Exception e) {
                            log.error("服务类:{}调用初始化方法:{}失败:{},stack:{}", serviceClass.getName(), bootService.initMethod(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
            log.info("load service success");

        } else {
            log.error("{} need BootApp annotation", clazz.getSimpleName());
        }
    }

    private static BeanModel loadService(Class<?> serviceClass, Set<Class<?>> serviceClasses) {
        BeanModel bm1 = bootApplication.getOneBeanByClass(serviceClass);
        if (bm1 != null) {
            return bm1;
        }

        Class<?> realServiceClass = serviceClass;
        if (serviceClass.isInterface()) {
            for (Class<?> aClass : serviceClasses) {
                log.debug("类型:{}----{}00--{}---{}", serviceClass.getName(), aClass.getName(), serviceClass.isAssignableFrom(aClass), aClass.isAssignableFrom(serviceClass));
                if (serviceClass.isAssignableFrom(aClass)) {
                    BeanModel bm2 = bootApplication.getOneBeanByClass(aClass);
                    if (bm2 != null) {
                        return bm2;
                    } else {
                        realServiceClass = aClass;
                        break;
                    }
                }
            }
            log.debug("类型:{}是个接口，使用具体实现:{}", serviceClass.getName(), realServiceClass.getSimpleName());
        }
        Object serviceInstance;
        try {
            serviceInstance = realServiceClass.getConstructor().newInstance();
        } catch (Exception e) {
            log.error("服务类:{}实例化失败:{},stack:{}", realServiceClass.getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        Field[] fields = FieldUtils.getFieldsWithAnnotation(realServiceClass, BootAutowired.class);
        for (Field field : fields) {
            BeanModel bm3 = null;
            BootAutowired bootAutowired = field.getAnnotation(BootAutowired.class);
            if (bootAutowired.byType()) {
                bm3 = bootApplication.getOneBeanByClass(field.getType());
            } else {
                if (StringUtils.isNoneBlank(bootAutowired.name())) {
                    bm3 = bootApplication.getBeanModelByName(bootAutowired.name());
                } else {
                    bm3 = bootApplication.getBeanModelByName(field.getName());
                }
            }
            if (bm3 == null) {
                bm3 = loadService(field.getType(), serviceClasses);
            }
            try {
                FieldUtils.writeField(field, serviceInstance, bm3.getBeanInstance(), true);
            } catch (IllegalAccessException e) {
                log.error("服务类:{}注入字段:{},失败:{},stack:{}", realServiceClass.getName(), field.getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        }
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

        bootApplication.putBean(beanModel.getBeanName(), beanModel);
        return beanModel;
    }
}
