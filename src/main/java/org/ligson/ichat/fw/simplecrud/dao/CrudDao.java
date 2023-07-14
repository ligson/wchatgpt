package org.ligson.ichat.fw.simplecrud.dao;

import org.ligson.ichat.fw.simplecrud.domain.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CrudDao<E extends BaseEntity> extends JpaRepository<E, String>, QuerydslPredicateExecutor<E> {
}
