package com.permission.utils.baseinterface;


import com.permission.utils.global.exception.GlobalException;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-09-05   *
 * * Time: 17:30        *
 * * to: lz&xm          *
 * **********************
 **/
public abstract class BaseServiceImpl<T, ID, DAO extends BaseJpaRepository<T, ID>> implements BaseService<T, ID> {


    public abstract DAO getDao();

    @Override
    public T add(T t) {
        return getDao().save(t);
    }

    @Override
    public List<T> addAll(Iterable<T> ts) {
        return getDao().saveAll(ts);
    }

    @Override
    public T update(T t) {
        return getDao().update(t);
    }

    @Override
    public List<T> updateAll(Iterable<T> ts) {
        return getDao().updateAll(ts);
    }

    @Override
    public void delete(ID id) {
        getDao().deleteById(id);
    }

    @Override
    public T getOne(ID id) {
        return getDao().findById(id).orElseThrow(GlobalException::new);
    }
}
