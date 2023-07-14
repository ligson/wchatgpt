package org.ligson.ichat.fw.simplecrud.service;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@SuppressWarnings("unchecked")
public class GenericUtils {
//    public static GenericRepository<? extends BaseEntity> findRepository(Class<? extends CrudService> clz) {
//        Class testServiceClz = (Class) clz.getGenericInterfaces()[0];
//        Type type = testServiceClz.getGenericInterfaces()[0];
//        Class<? extends BaseEntity> entityClz = (Class<? extends BaseEntity>) ((ParameterizedType) type).getActualTypeArguments()[0];
//        return ServiceLocator.getService(GenericRepositoryManager.class).repository(entityClz);
//    }
//
//    public static GenericRepository<? extends BaseEntity> findRepository(String entityName) {
//        Set<EntityType<?>> entities = ServiceLocator.getService(EntityManager.class).getMetamodel().getEntities();
//        Class entityClazz = null;
//        for (EntityType entityType : entities) {
//            if (entityType.getName().equalsIgnoreCase(entityName)) {
//                entityClazz = entityType.getJavaType();
//                break;
//            }
//        }
//        if (entityClazz == null) {
//            throw new InnerException(String.format("根据实体名称:%s不能找到对应的实体", entityName));
//        }
//        return ServiceLocator.getService(GenericRepositoryManager.class).repository(entityClazz);
//    }
}
