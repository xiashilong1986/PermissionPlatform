package com.permission.security.controller;

import com.permission.security.accesslimit.AccessLimit;
import com.permission.security.accesslimit.LimitType;
import com.permission.security.entity.RoleMenuEntity;
import com.permission.security.service.SystemRoleService;
import com.permission.utils.global.result.GlobalResult;
import com.permission.utils.global.result.GlobalResultUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-06   *
 * * Time: 13:51        *
 * * to: lz&xm          *
 * **********************
 **/
@RestController
@RequestMapping(value = "/developer/systemRole")
public class SystemRoleController {

    private final SystemRoleService service;

    @Autowired
    public SystemRoleController(SystemRoleService service) {
        this.service = service;
    }

    /**
     * 创建
     *
     * @param name      角色名
     * @param creatorId 创建者id
     * @param roleType  前后端页面标识(0后端,1前端)
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/add/{creatorId}/{roleType}")
    public GlobalResult add(@NonNull String name, @PathVariable Long creatorId, @PathVariable Integer roleType) {
        service.add(name, creatorId, roleType);
        return GlobalResultUtil.success();
    }

    /**
     * 修改
     *
     * @param id   主键
     * @param name 角色名
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/update/{id}")
    public GlobalResult update(@PathVariable Long id, @NonNull String name) {
        service.update(id, name);
        return GlobalResultUtil.success();
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @GetMapping(value = "/delete/{id}")
    public GlobalResult delete(@PathVariable Long id) {
        service.delete(id);
        return GlobalResultUtil.success();
    }

    /**
     * 获取所有角色
     *
     * @param page      页码
     * @param size      每页数量
     * @param name      角色名模糊查询
     * @param creatorId 创建者id
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @PostMapping(value = "/getAll/{page}/{size}/{creatorId}")
    public GlobalResult getAll(@PathVariable Integer page, @PathVariable Integer size, String name, @PathVariable Long creatorId) {
        return GlobalResultUtil.success(service.getAll(page, size, name, creatorId));
    }

    /**
     * 获取所有角色(下拉框)
     *
     * @param creatorId 创建者id
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @GetMapping(value = "/getAll/{creatorId}")
    public GlobalResult getAll(@PathVariable Long creatorId) {
        return GlobalResultUtil.success(service.getAll(creatorId));
    }

    /**
     * 为角色赋值权限
     *
     * @param roleMenuEntity <see>RoleMenuEntity</see>
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/givePermission")
    public GlobalResult givePermission(@RequestBody RoleMenuEntity roleMenuEntity) {
        service.givePermission(roleMenuEntity);
        return GlobalResultUtil.success();
    }
}
