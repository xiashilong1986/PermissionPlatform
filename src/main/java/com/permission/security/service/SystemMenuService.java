package com.permission.security.service;

import com.permission.security.entity.OperateRoleMenuEntity;
import com.permission.security.entity.SystemMenu;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-02   *
 * * Time: 11:23        *
 * * to: lz&xm          *
 * **********************
 **/
public interface SystemMenuService {

    /**
     * 创建菜单
     *
     * @param systemMenu <see>SystemMenu</see>
     * @return SystemMenu
     */
    SystemMenu add(SystemMenu systemMenu);

    /**
     * 修改菜单
     *
     * @param systemMenu <see>SystemMenu</see>
     * @return SystemMenu
     */
    SystemMenu update(SystemMenu systemMenu);

    /**
     * 删除主菜单
     *
     * @param id 主键
     */
    void deletePrimary(Long id);

    /**
     * 删除子菜单
     *
     * @param id 主键
     */
    void deleteChild(Long id);

    /**
     * 获取app所有菜单及菜单下所有按钮
     *
     * @return List<SystemMenu>
     */
    List<SystemMenu> getAllAppMenu();

    /**
     * 获取管理端所有菜单及菜单下所有按钮
     *
     * @return List<SystemMenu>
     */
    List<SystemMenu> getAllManagementMenu();

    /**
     * 获取角色下所有菜单及按钮(赋予权限页面)
     *
     * @param operateRoleId 操作角色id
     * @param grantRoleId   被操作角色id
     * @return List<OperateRoleMenuEntity> <see>OperateRoleMenuEntity</see>
     */
    List<OperateRoleMenuEntity> getRoleMenu(Long operateRoleId, Long grantRoleId);

    /**
     * 获取开发者下所有菜单及按钮(赋予权限页面)
     *
     * @param grantRoleId 被操作角色id
     * @return List<OperateRoleMenuEntity> <see>OperateRoleMenuEntity</see>
     */
    List<OperateRoleMenuEntity> getDeveloperRoleMenu(Long grantRoleId);


    /**
     * 获取所有app菜单(列表)
     *
     * @param page 页码
     * @param size 每页数量
     * @param pid  父级菜单id
     * @param name 模糊查询名称
     * @return Page<SystemMenu>
     */
    Page<SystemMenu> getAllAppMenu(Integer page, Integer size, Long pid, String name);

    /**
     * 获取所有管理端菜单(列表)
     *
     * @param page 页码
     * @param size 每页数量
     * @param pid  父级菜单id
     * @param name 模糊查询名称
     * @return Page<SystemMenu>
     */
    Page<SystemMenu> getAllManagementMenu(Integer page, Integer size, Long pid, String name);

    /**
     * 获取所有菜单
     *
     * @param ids 字符串id集合
     * @return List<SystemMenu>
     */
    List<SystemMenu> getAll(String ids);
}