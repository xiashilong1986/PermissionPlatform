package com.permission.utils.baseinterface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2018-03-22   *
 * * Time: 14:54        *
 * * to: lz&xm          *
 * **********************
 **/
@NoRepositoryBean
public interface BaseJpaRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    T update(T entity);

    List<T> updateAll(Iterable<T> var1);
}
