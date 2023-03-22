package org.ligson.fw;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.ligson.fw.annotation.BootApp;
import org.ligson.fw.annotation.BootBean;
import org.ligson.fw.annotation.BootConfig;
import org.ligson.fw.annotation.BootService;
import org.ligson.fw.util.BeanLoader;
import org.ligson.fw.util.ClazzUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class FwBooter {
    private static final BootApplicationContext BOOT_APPLICATION_CONTEXT = BootApplicationContext.getInstance();
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


            Set<Class<?>> projectClasses = new HashSet<>();
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
            BeanLoader beanLoader = new BeanLoader(BOOT_APPLICATION_CONTEXT, projectClasses);

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
                Object configInstance = beanLoader.instanceClass(configClass);
                List<Method> methods = MethodUtils.getMethodsListWithAnnotation(configClass, BootBean.class);
                for (Method method : methods) {
                    Object beanInstance;
                    Type[] paramTypes = method.getGenericParameterTypes();
                    if (paramTypes.length == 0) {
                        try {
                            beanInstance = method.invoke(configInstance);
                        } catch (Exception e) {
                            log.error("配置类:{}调用方法:{}失败:{},stack:{}", configClass.getName(), method.toGenericString(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                            throw new RuntimeException(e);
                        }
                    } else {
                        Object[] paramValue = new Object[paramTypes.length];
                        for (int i = 0; i < paramTypes.length; i++) {
                            Type paramType = paramTypes[i];
                            if (paramType instanceof ParameterizedType parameterizedType) {
                                if (parameterizedType.getRawType() == List.class) {
                                    Class<?> actualType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                                    paramValue[i] = beanLoader.loadBeansBySupperClass(actualType).stream().map(BeanModel::getBeanInstance).collect(Collectors.toList());
                                } else {
                                    paramValue[i] = beanLoader.loadBean(parameterizedType.getRawType().getClass()).getBeanInstance();
                                }
                            }else{
                                paramValue[i] = beanLoader.loadBean(paramType.getClass());
                            }
                        }
                        try {
                            beanInstance = method.invoke(configInstance, paramValue);
                        } catch (Exception e) {
                            log.error("配置类:{}调用方法:{}失败:{},stack:{}", configClass.getName(), method.toGenericString(), e.getMessage(), ExceptionUtils.getStackTrace(e));
                            throw new RuntimeException(e);
                        }
                    }

                    BeanModel beanModel = new BeanModel();
                    beanModel.setBeanInstance(beanInstance);
                    beanModel.setBeanType(beanInstance.getClass());
                    beanModel.setBeanName(method.getName());
                    BOOT_APPLICATION_CONTEXT.putBean(beanModel.getBeanName(), beanModel);
                }
            }
            //service
            for (Class<?> serviceClass : serviceClasses) {
                BeanModel bm = beanLoader.loadBean(serviceClass);
                log.debug("bean加载成功，类型:{},名称:{}", serviceClass.getSimpleName(), bm.getBeanName());
            }
            //service init
            for (Class<?> serviceClass : serviceClasses) {
                BootService bootService = serviceClass.getAnnotation(BootService.class);
                if (StringUtils.isNoneBlank(bootService.initMethod())) {
                    executorService.submit(() -> {
                        BeanModel bm = BOOT_APPLICATION_CONTEXT.getOneBeanByClass(serviceClass);
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


}
