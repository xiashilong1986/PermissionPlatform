package com.permission.security.controller;

import com.permission.security.accesslimit.AccessLimit;
import com.permission.security.accesslimit.LimitType;
import com.permission.security.service.SystemUserService;
import com.permission.utils.global.result.GlobalResult;
import com.permission.utils.global.result.GlobalResultUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 13:47        *
 * * to: lz&xm          *
 * **********************
 **/
@RestController
@RequestMapping(value = "/developer/systemUser")
public class SystemUserController {

    private final SystemUserService service;

    @Autowired
    public SystemUserController(SystemUserService service) {
        this.service = service;
    }

    /**
     * 注册
     *
     * @param username  用户名
     * @param password  密码
     * @param creatorId 创建者id(商户id)
     * @param roleId    角色id
     * @return GlobalResult <see>GlobalResult</see>
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/auth/registered/{creatorId}/{roleId}")
    public GlobalResult registered(@NonNull String username, @NonNull String password, @PathVariable Long creatorId, @PathVariable Long roleId) {
        return GlobalResultUtil.success(service.registered(username, password, creatorId, roleId));
    }

    /**
     * 创建用户
     *
     * @param username  用户名
     * @param password  密码
     * @param creatorId 创建者id
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/createUser/{creatorId}")
    public GlobalResult createUser(@NonNull String username, @NonNull String password, @PathVariable Long creatorId) {
        return GlobalResultUtil.success(service.createUser(username, password, creatorId));
    }

    /**
     * 修改用户密码
     *
     * @param id          主键
     * @param password    密码
     * @param newPassword 新密码
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/modifyPassword/{id}")
    public GlobalResult modifyPassword(@PathVariable Long id, @NonNull String password, @NonNull String newPassword) {
        service.modifyPassword(id, password, newPassword);
        return GlobalResultUtil.success();
    }

    /**
     * 管理员修改用户密码
     *
     * @param id       主键
     * @param password 密码
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/modifyPassword")
    public GlobalResult modifyPassword(@NonNull Long id, @NonNull String password) {
        service.modifyPassword(id, password);
        return GlobalResultUtil.success();
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @GetMapping(value = "/deleteUser/{id}")
    public GlobalResult deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return GlobalResultUtil.success();
    }

    /**
     * 锁定账号
     *
     * @param id 主键
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @GetMapping(value = "/locked/{id}")
    public GlobalResult locked(@PathVariable Long id) {
        service.locked(id);
        return GlobalResultUtil.success();
    }

    /**
     * 解锁账号
     *
     * @param id 主键
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @GetMapping(value = "/unlocked/{id}")
    public GlobalResult unlocked(@PathVariable Long id) {
        service.unlocked(id);
        return GlobalResultUtil.success();
    }

    /**
     * 获取所有管理账户
     *
     * @param page          页码
     * @param size          每页数量
     * @param username      模糊查询用户名
     * @param accountLocked 锁定 (0否,1是)
     * @param creatorId     创建者id
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @PostMapping(value = "/getAll/{page}/{size}/{creatorId}")
    public GlobalResult getAll(@PathVariable Integer page, @PathVariable Integer size, String username, Integer accountLocked, @PathVariable Long creatorId) {
        return GlobalResultUtil.success(service.getAll(page, size, username, accountLocked, creatorId));
    }


    /**
     * 为用户赋值角色
     *
     * @param userId 用户id
     * @param roleId 角色id
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @GetMapping(value = "/giveRole/{userId}/{roleId}")
    public GlobalResult giveRole(@PathVariable Long userId, @PathVariable Long roleId) {
        service.giveRole(userId, roleId);
        return GlobalResultUtil.success();
    }

    /**
     * 获取用户登陆信息
     *
     * @param username 用户名
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @GetMapping(value = "/auth/getUserLoginContext/{username}")
    public GlobalResult getUserLoginContext(@PathVariable String username) {
        return GlobalResultUtil.success(service.getUserLoginContext(username));
    }


    /**
     * 修改用户密码(开放接口)
     *
     * @param id          主键
     * @param password    密码
     * @param newPassword 新密码
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/auth/updatePassword/{id}")
    public GlobalResult updatePassword(@PathVariable Long id, @NonNull String password, @NonNull String newPassword) {
        service.modifyPassword(id, password, newPassword);
        return GlobalResultUtil.success();
    }

    /**
     * 修改用户密码(开放接口)
     *
     * @param id       主键
     * @param password 密码
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/auth/updatePassword")
    public GlobalResult updatePassword(@NonNull Long id, @NonNull String password) {
        service.modifyPassword(id, password);
        return GlobalResultUtil.success();
    }
}
