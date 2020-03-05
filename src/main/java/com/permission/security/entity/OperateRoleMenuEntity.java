package com.permission.security.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-12   *
 * * Time: 17:00        *
 * * to: lz&xm          *
 * **********************
 * 角色操作对象
 **/
@Data
@AllArgsConstructor
public class OperateRoleMenuEntity {
    /**
     * 主键
     */
    private Long id;

    /**
     * 页面路径
     */
    private String path;

    /**
     * 页面名称
     */
    private String name;

    /**
     * 路由变量名
     */
    private String component;

    /**
     * 页面对应接口,逗号分隔多个接口
     */
    private String menuInterface;

    /**
     * 菜单排序
     */
    private Integer sort;

    /**
     * 父菜单id 如果为0则为顶级菜单
     */
    private Long pid;

    /**
     * 是否选中
     */
    private boolean selected;

    /**
     * 按钮集合
     */
    private List<OperateRoleButtonEntity> buttonList;


    public static OperateRoleMenuEntity of(Long id, String path, String name, String component, String menuInterface, Integer sort, Long pid, boolean selected, List<OperateRoleButtonEntity> buttonList) {
        return new OperateRoleMenuEntity(id, path, name, component, menuInterface, sort, pid, selected, buttonList);
    }

    @Data
    @AllArgsConstructor
    public static class OperateRoleButtonEntity {

        private Long id;

        /**
         * 名称
         */
        private String name;

        /**
         * 按钮对应接口,逗号分隔多个接口
         */
        private String buttonInterface;

        /**
         * 菜单id
         */
        private Long menuId;


        /**
         * 是否选中
         */
        private boolean selected;

        public static OperateRoleButtonEntity of(Long id, String name, String buttonInterface, Long menuId, boolean selected) {
            return new OperateRoleButtonEntity(id, name, buttonInterface, menuId, selected);
        }
    }
}
