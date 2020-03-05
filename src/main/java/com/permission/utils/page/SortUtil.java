package com.permission.utils.page;

import org.springframework.data.domain.Sort;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2020-01-11   *
 * * Time: 15:14        *
 * * to: xm             *
 * **********************
 * 排序工具类
 **/
public class SortUtil {

    //默认排序字段
    private final static String PROPERTIES = "createTime";

    /**
     * 默认排序
     * 创建时间降序
     *
     * @return Sort
     */
    public static Sort defaultSort() {
        return sort(Sort.Direction.DESC, PROPERTIES);
    }

    /**
     * 自定义升序排序
     *
     * @param properties 排序的字段
     * @return Sort
     */
    public static Sort sortAsc(String... properties) {
        return sort(Sort.Direction.ASC, properties);
    }

    /**
     * 自定义降序排序
     *
     * @param properties 排序的字段
     * @return Sort
     */
    public static Sort sortDesc(String... properties) {
        return sort(Sort.Direction.DESC, properties);
    }

    private static Sort sort(Sort.Direction direction, String... properties) {
        return Sort.by(direction, properties);
    }

}
