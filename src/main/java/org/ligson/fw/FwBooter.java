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
                BeanModel bm = loadService(serviceClass);
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

        } else {
            log.error("{} need BootApp annotation", clazz.getSimpleName());
        }
    }

    private static BeanModel loadService(Class<?> serviceClass) {
        Object serviceInstance;
        try {
            serviceInstance = serviceClass.getConstructor().newInstance();
        } catch (Exception e) {
            log.error("服务类:{}实例化失败:{},stack:{}", serviceClass.getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        Field[] fields = FieldUtils.getFieldsWithAnnotation(serviceClass, BootAutowired.class);
        for (Field field : fields) {
            BeanModel bm = bootApplication.getBeanModelByName(field.getName());
            if (bm == null) {
                bm = loadService(field.getType());
            }
            try {
                FieldUtils.writeField(field, serviceInstance, bm.getBeanInstance(), true);
            } catch (IllegalAccessException e) {
                log.error("服务类:{}注入字段:{},失败:{},stack:{}", serviceClass.getName(), field.getName(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        }
        BeanModel beanModel = new BeanModel();
        beanModel.setBeanInstance(serviceInstance);
        beanModel.setBeanType(serviceInstance.getClass());
        String name = Character.toLowerCase(serviceInstance.getClass().getSimpleName().charAt(0)) + serviceInstance.getClass().getSimpleName().substring(1);
        beanModel.setBeanName(name);
        bootApplication.putBean(beanModel.getBeanName(), beanModel);
        return beanModel;
    }
}
