package com.permission.security.service;

import com.permission.security.entity.SystemButton;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-02   *
 * * Time: 14:13        *
 * * to: lz&xm          *
 * **********************
 **/
public interface SystemButtonService {

    /**
     * 创建
     *
     * @param name            名称
     * @param buttonInterface 按钮对应接口,逗号分隔多个接口
     * @param menuId          菜单id
     * @return SystemButton
     */
    SystemButton add(String name, String buttonInterface, Long menuId);

    /**
     * 修改
     *
     * @param id              主键
     * @param name            名称
     * @param buttonInterface 按钮对应接口,逗号分隔多个接口
     * @param menuId          菜单id
     * @return SystemButton
     */
    SystemButton update(Long id, String name, String buttonInterface, Long menuId);

    /**
     * 删除
     *
     * @param id 主键
     */
    void delete(Long id);

    /**
     * 删除菜单下所有按钮
     *
     * @param menuId 菜单id
     * @return 被删除id集合
     */
    List<Long> deleteAll(Long menuId);

    /**
     * 批量删除菜单下按钮
     *
     * @param menuIds 菜单id集合
     * @return 被删除id集合
     */
    List<Long> deleteAll(List<Long> menuIds);

    /**
     * 获取菜单下所有按钮
     *
     * @param page   页码
     * @param size   每页数量
     * @param menuId 菜单id
     * @param name   模糊查询名称
     * @return Page<SystemButton>
     */
    Page<SystemButton> getAll(Integer page, Integer size, Long menuId, String name);

    /**
     * 获取所有按钮
     *
     * @param ids 字符串id集合
     * @return List<SystemButton>
     */
    List<SystemButton> getAll(String ids);
}
