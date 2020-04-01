package com.permission.utils.baseinterface;


import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-09-05   *
 * * Time: 17:19        *
 * * to: lz&xm          *
 * **********************
 **/
public interface BaseService<T, ID> {

    T add(T t);

    List<T> addAll(Iterable<T> ts);

    T update(T t);

    List<T> updateAll(Iterable<T> ts);

    void delete(ID id);

    T getOne(ID id);

}
