package org.ligson.ichat.fw.simplecrud.service;

import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.ligson.ichat.fw.ex.InnerException;
import org.ligson.ichat.fw.simplecrud.dao.CrudDao;
import org.ligson.ichat.fw.simplecrud.dao.CrudDaoManager;
import org.ligson.ichat.fw.simplecrud.domain.BaseEntity;
import org.ligson.ichat.fw.simplecrud.querydsl.QueryDslUtil;
import org.ligson.ichat.fw.simplecrud.vo.GridListReq;
import org.ligson.ichat.fw.util.ServiceLocator;
import org.ligson.ichat.fw.simplecrud.vo.PageWebResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


public interface CrudService<E extends BaseEntity> {
    private static CrudDaoManager getCrudDaoManager(String entityName) {
        CrudDaoManager manager = ServiceLocator.getBean(CrudDaoManager.class);
        if (manager == null) {
            throw new InnerException("找不到CrudDaoManager");
        }
        return manager;
    }

    private static <E extends BaseEntity> CrudDao<E> getCrudDao(String entityName) {
        return getCrudDaoManager(entityName).getDaoByEntityName(entityName);
    }

    default PageWebResult gridList(String entityName, GridListReq gridListReq) {
        if (null == gridListReq) {
            gridListReq = new GridListReq();
        }
        Class<? extends BaseEntity> entityClazz = getCrudDaoManager(entityName).entityType(entityName);
        CrudDao<? extends BaseEntity> dao = getCrudDao(entityName);
        Predicate predicate = QueryDslUtil.createPredicate(gridListReq.getQueryCondition(), entityClazz);
        PageRequest pageRequest;
        if (StringUtils.isNotBlank(gridListReq.getSort())) {
            pageRequest = PageRequest.of(gridListReq.getPage(), gridListReq.getMax(), Sort.by(gridListReq.isOrder() ? Sort.Direction.ASC : Sort.Direction.DESC, gridListReq.getSort()));
        } else {
            pageRequest = PageRequest.of(gridListReq.getPage(), gridListReq.getMax());
        }
        Page<? extends BaseEntity> page = dao.findAll(predicate, pageRequest);
        return PageWebResult.newInstance( page.getContent(),(int)page.getTotalElements());
    }

    default void save(E e) {
        CrudDao<E> dao = getCrudDao(e.getClass().getSimpleName());
        dao.save(e);
    }

    default void update(E e) {
        getCrudDao(e.getClass().getSimpleName()).save(e);
    }

    default void delete(E e) {
        getCrudDao(e.getClass().getSimpleName()).delete(e);
        //dao.findAll()
    }

    default void findAll(Predicate predicate, Class<E> entityClazz) {
        CrudDao<E> dao = getCrudDao(entityClazz.getSimpleName());
        dao.findAll(predicate);
    }

    default E findById(String id, Class<E> entityClazz) {
        CrudDao<E> dao = getCrudDao(entityClazz.getSimpleName());
        return dao.findById(id).orElseThrow(() -> new InnerException(String.format("根据id:%s找不到模型:%s", id, entityClazz.getName())));
    }

}
