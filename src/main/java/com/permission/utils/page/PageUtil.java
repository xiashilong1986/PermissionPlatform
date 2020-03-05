package com.permission.utils.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2019-12-05   *
 * * Time: 14:46        *
 * * to: xm             *
 * **********************
 * 分页工具类
 **/
public class PageUtil {

    /**
     * 默认分页
     *
     * @param page 页码
     * @param size 每页数量
     * @param sort 排序对象
     * @return PageRequest
     */
    public static PageRequest page(int page, int size, Sort sort) {
        return PageRequest.of(page, size, sort);
    }

    /**
     * 无排序分页
     *
     * @param page 页码
     * @param size 每页数量
     * @return PageRequest
     */
    public static PageRequest page(int page, int size) {
        return PageRequest.of(page, size);
    }

}
