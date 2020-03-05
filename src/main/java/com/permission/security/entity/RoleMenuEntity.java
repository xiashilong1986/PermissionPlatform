package com.permission.security.entity;

import lombok.Data;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-10   *
 * * Time: 10:19        *
 * * to: lz&xm          *
 * **********************
 * 角色菜单映射实体
 **/
@Data
public class RoleMenuEntity {

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 菜单id集合
     */
    private List<Long> menuIdList;

    /**
     * 按钮id集合
     */
    private List<Long> buttonIdList;
}
