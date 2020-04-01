package com.permission.security.controller;

import com.permission.security.accesslimit.AccessLimit;
import com.permission.security.accesslimit.LimitType;
import com.permission.security.service.SystemButtonService;
import com.permission.utils.global.result.GlobalResult;
import com.permission.utils.global.result.GlobalResultUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-06   *
 * * Time: 13:34        *
 * * to: lz&xm          *
 * **********************
 **/
@RestController
@RequestMapping(value = "/developer/systemButton")
public class SystemButtonController {

    private final SystemButtonService service;

    @Autowired
    public SystemButtonController(SystemButtonService service) {
        this.service = service;
    }

    /**
     * 创建
     *
     * @param name            名称
     * @param buttonInterface 按钮对应接口,逗号分隔多个接口
     * @param menuId          菜单id
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/add")
    public GlobalResult add(@NonNull String name, @NonNull String buttonInterface, @NonNull Long menuId) {
        service.add(name, buttonInterface, menuId);
        return GlobalResultUtil.success();
    }

    /**
     * 修改
     *
     * @param id              主键
     * @param name            名称
     * @param buttonInterface 按钮对应接口,逗号分隔多个接口
     * @param menuId          菜单id
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/update/{id}")
    public GlobalResult update(@PathVariable Long id, @NonNull String name, @NonNull String buttonInterface, @NonNull Long menuId) {
        service.update(id, name, buttonInterface, menuId);
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
     * 获取菜单下所有按钮
     *
     * @param page   页码
     * @param size   每页数量
     * @param menuId 菜单id
     * @param name   模糊查询名称
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @PostMapping(value = "/getAll/{page}/{size}/{menuId}")
    public GlobalResult getAll(@PathVariable Integer page, @PathVariable Integer size, @PathVariable Long menuId, String name) {
        return GlobalResultUtil.success(service.getAll(page, size, menuId, name));
    }
}
