package com.permission.security.dao;

import com.permission.security.entity.SystemMenu;
import com.permission.utils.baseinterface.BaseJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Collection;
import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-02   *
 * * Time: 11:14        *
 * * to: lz&xm          *
 * **********************
 **/
public interface SystemMenuDao extends BaseJpaRepository<SystemMenu, Long> {

    /**
     * 根据id集合删除
     *
     * @param ids id集合
     */
    void deleteByIdIn(List<Long> ids);

    /**
     * 根据pid查询
     *
     * @param pid 父级菜单id
     * @return List<SystemMenu>
     */
    List<SystemMenu> findByPid(Long pid);

    /**
     * 根据菜单类型查询
     *
     * @param menuType 前后端页面标识(0后端,1前端)
     * @param sort     排序对象
     * @return List<SystemMenu>
     */
    @EntityGraph(value = "SystemMenu.button", type = EntityGraph.EntityGraphType.LOAD)
    List<SystemMenu> findByMenuType(Integer menuType, Sort sort);

    /**
     * 获取所有菜单及所属按钮
     *
     * @param ids 菜单id集合
     * @return List<SystemMenu>
     */
    @EntityGraph(value = "SystemMenu.button", type = EntityGraph.EntityGraphType.LOAD)
    List<SystemMenu> findByIdIn(List<Long> ids);

    /**
     * 获取所有菜单
     *
     * @param ids 菜单id集合
     * @return List<SystemMenu>
     */
    List<SystemMenu> getByIdIn(Collection<Long> ids);
}
