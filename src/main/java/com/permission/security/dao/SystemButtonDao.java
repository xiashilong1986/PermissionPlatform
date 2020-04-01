package com.permission.security.dao;

import com.permission.security.entity.SystemButton;
import com.permission.utils.baseinterface.BaseJpaRepository;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-02   *
 * * Time: 14:12        *
 * * to: lz&xm          *
 * **********************
 **/
public interface SystemButtonDao extends BaseJpaRepository<SystemButton, Long> {

    /**
     * 根据菜单id删除
     *
     * @param menuId 菜单id
     */
    void deleteByMenuId(Long menuId);

    /**
     * 批量删除菜单下按钮
     *
     * @param menuIds 菜单id集合
     */
    void deleteByMenuIdIn(List<Long> menuIds);

    /**
     * 获取菜单下所有按钮
     *
     * @param menuId 菜单id
     * @return List<SystemButton>
     */
    List<SystemButton> findByMenuId(Long menuId);

    /**
     * 根据菜单id集合查询
     *
     * @param menuIds 菜单id集合
     * @return List<SystemButton>
     */
    List<SystemButton> findByMenuIdIn(List<Long> menuIds);

    /**
     * 根据id集合查询
     *
     * @param ids id集合
     * @return List<SystemButton>
     */
    List<SystemButton> findByIdIn(List<Long> ids);
}
