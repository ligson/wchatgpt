package org.ligson.ichat.fw.simplecrud.dao;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.ligson.ichat.fw.ex.InnerException;
import org.ligson.ichat.fw.simplecrud.domain.BaseEntity;
import org.ligson.ichat.fw.simplecrud.service.CrudService;
import org.ligson.ichat.fw.util.ServiceLocator;
import org.ligson.ichat.user.User;
import org.reflections.Reflections;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Lazy(value = false)
@Slf4j
public class CrudDaoManager {
    private final Map<String, Class<? extends BaseEntity>> entityClazzMap = new ConcurrentHashMap<>();
    private final Map<String, CrudDao<? extends BaseEntity>> entityDaoMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        Reflections reflections = new Reflections("org.ligson");
        log.debug("store size:{}", reflections.getStore().size());
        reflections.getSubTypesOf(BaseEntity.class).forEach(entityType -> entityClazzMap.put(entityType.getSimpleName().toUpperCase(), entityType));
        log.debug("扫描到继承BaseEntity的模型{}个,分别是:{}", entityClazzMap.size(), entityClazzMap.keySet());
        if (entityClazzMap.size() == 0) {
            entityClazzMap.put(User.class.getSimpleName().toUpperCase(), User.class);
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends BaseEntity> CrudDao<E> getDaoByEntityName(String name) {
        Class<? extends BaseEntity> clazz = entityType(name);
        CrudDao<? extends BaseEntity> dao = entityDaoMap.get(name.toUpperCase());
        if (dao == null) {
            Class<?> serviceClazz = null;
            try {
                serviceClazz = Class.forName(clazz.getPackageName() + "." + clazz.getSimpleName() + "Dao");
            } catch (ClassNotFoundException e) {
                throw new InnerException(e);
            }
            dao = (CrudDao<E>) ServiceLocator.getBean(serviceClazz);
            entityDaoMap.put(name.toUpperCase(), dao);
        }
        return (CrudDao<E>) dao;
    }

    public Class<? extends BaseEntity> entityType(String entityName) {
        return entityClazzMap.get(entityName.toUpperCase());
    }

    @SuppressWarnings("unchecked")
    public <E extends BaseEntity> CrudService<E> getCrudServiceByEntityName(String name) {
        Class<? extends BaseEntity> clazz = entityType(name);
        Class<?> serviceClazz = null;
        try {
            serviceClazz = Class.forName(clazz.getPackageName() + "." + clazz.getSimpleName() + "Service");
        } catch (ClassNotFoundException e) {
            throw new InnerException(e);
        }
        return (CrudService<E>) ServiceLocator.getBean(serviceClazz);
    }
}
