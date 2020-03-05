package com.permission.security.service;

import com.permission.security.entity.RoleMenuEntity;
import com.permission.security.entity.SystemRole;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-06   *
 * * Time: 13:43        *
 * * to: lz&xm          *
 * **********************
 **/
public interface SystemRoleService {

    /**
     * 创建
     *
     * @param name      角色名
     * @param creatorId 创建者id
     * @param roleType  前后端页面标识(0后端,1前端)
     * @return SystemRole
     */
    SystemRole add(String name, Long creatorId, Integer roleType);

    /**
     * 修改
     *
     * @param id   主键
     * @param name 角色名
     * @return SystemRole
     */
    SystemRole update(Long id, String name);

    /**
     * 批量更新
     *
     * @param list 集合
     */
    void update(List<SystemRole> list);

    /**
     * 删除
     *
     * @param id 主键
     */
    void delete(Long id);

    /**
     * 获取角色信息
     *
     * @param id 主键
     * @return SystemRole
     */
    SystemRole getOne(Long id);

    /**
     * 获取所有角色
     *
     * @param page      页码
     * @param size      每页数量
     * @param name      角色名模糊查询
     * @param creatorId 创建者id
     * @return Page<SystemRole>
     */
    Page<SystemRole> getAll(Integer page, Integer size, String name, Long creatorId);

    /**
     * 获取所有角色(下拉框)
     *
     * @param creatorId 创建者id
     * @return List<SystemRole>
     */
    List<SystemRole> getAll(Long creatorId);

    /**
     * 根据id集合获取角色
     *
     * @param ids 角色id集合
     * @return List<SystemRole>
     */
    List<SystemRole> getAll(List<Long> ids);

    /**
     * 为角色赋值权限
     *
     * @param roleMenuEntity <see>RoleMenuEntity</see>
     */
    void givePermission(RoleMenuEntity roleMenuEntity);

    /**
     * 删除菜单和按钮时,更新角色的权限
     *
     * @param menuIdList   被删除的菜单id集合
     * @param buttonIdList 被删除的按钮id集合
     */
    void updateAllRolePermission(List<Long> menuIdList, List<Long> buttonIdList);

}
