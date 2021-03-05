package com.permission.security.controller;

import com.permission.security.accesslimit.AccessLimit;
import com.permission.security.accesslimit.LimitType;
import com.permission.security.entity.SystemMenu;
import com.permission.security.service.SystemMenuService;
import com.permission.utils.global.result.GlobalResult;
import com.permission.utils.global.result.GlobalResultUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-02   *
 * * Time: 14:30        *
 * * to: lz&xm          *
 * **********************
 **/
@RestController
@RequestMapping(value = "/developer/systemMenu")
public class SystemMenuController {

    private final SystemMenuService service;

    @Autowired
    public SystemMenuController(SystemMenuService service) {
        this.service = service;
    }

    /**
     * 创建菜单
     *
     * @param systemMenu <see>SystemMenu</see>
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/add")
    public GlobalResult add(SystemMenu systemMenu) {
        return GlobalResultUtil.success(service.add(systemMenu));
    }

    /**
     * 修改菜单
     *
     * @param systemMenu <see>SystemMenu</see>
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/update")
    public GlobalResult update(SystemMenu systemMenu) {
        return GlobalResultUtil.success(service.update(systemMenu));
    }

    /**
     * 删除主菜单
     *
     * @param id 主键
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @GetMapping(value = "/deletePrimary/{id}")
    public GlobalResult deletePrimary(@PathVariable Long id) {
        service.deletePrimary(id);
        return GlobalResultUtil.success();
    }

    /**
     * 删除子菜单
     *
     * @param id 主键
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @GetMapping(value = "/deleteChild/{id}")
    public GlobalResult deleteChild(@PathVariable Long id) {
        service.deleteChild(id);
        return GlobalResultUtil.success();
    }

    /**
     * 获取app所有菜单及菜单下所有按钮
     *
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @GetMapping(value = "/getAllAppMenu")
    public GlobalResult getAllAppMenu() {
        return GlobalResultUtil.success(service.getAllAppMenu());
    }

    /**
     * 获取管理端所有菜单及菜单下所有按钮
     *
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @GetMapping(value = "/getAllManagementMenu")
    public GlobalResult getAllManagementMenu() {
        return GlobalResultUtil.success(service.getAllManagementMenu());
    }


    /**
     * 获取角色下所有菜单及按钮(赋予权限页面)
     *
     * @param operateRoleId 操作角色id
     * @param grantRoleId   被操作角色id
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @GetMapping(value = "/getRoleMenu/{operateRoleId}/{grantRoleId}")
    public GlobalResult getRoleMenu(@PathVariable Long operateRoleId, @PathVariable Long grantRoleId) {
        return GlobalResultUtil.success(service.getRoleMenu(operateRoleId, grantRoleId));
    }

    /**
     * 获取开发者下所有菜单及按钮(赋予权限页面)
     *
     * @param grantRoleId 被操作角色id
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @GetMapping(value = "/getDeveloperRoleMenu/{grantRoleId}")
    public GlobalResult getDeveloperRoleMenu(@PathVariable Long grantRoleId) {
        return GlobalResultUtil.success(service.getDeveloperRoleMenu(grantRoleId));
    }

    /**
     * 获取所有app菜单(列表)
     *
     * @param page 页码
     * @param size 每页数量
     * @param pid  父级菜单id
     * @param name 模糊查询名称
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @PostMapping(value = "/getAllAppMenu/{page}/{size}")
    public GlobalResult getAllAppMenu(@PathVariable Integer page, @PathVariable Integer size, @NonNull Long pid, String name) {
        return GlobalResultUtil.success(service.getAllAppMenu(page, size, pid, name));
    }

    /**
     * 获取所有管理端菜单(列表)
     *
     * @param page 页码
     * @param size 每页数量
     * @param pid  父级菜单id
     * @param name 模糊查询名称
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @PostMapping(value = "/getAllManagementMenu/{page}/{size}")
    public GlobalResult getAllManagementMenu(@PathVariable Integer page, @PathVariable Integer size, @NonNull Long pid, String name) {
        return GlobalResultUtil.success(service.getAllManagementMenu(page, size, pid, name));
    }

}
