package com.permission.security.dao;

import com.permission.security.entity.SystemUser;
import com.permission.utils.baseinterface.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 14:15        *
 * * to: lz&xm          *
 * **********************
 **/
public interface SystemUserDao extends BaseJpaRepository<SystemUser, Long> {

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return Optional<SystemUser>
     */
    @EntityGraph(value = "SystemUser.role", type = EntityGraph.EntityGraphType.LOAD)
    Optional<SystemUser> findByUsername(String username);

    /**
     * 是否有绑定此角色的用户
     *
     * @param roleId 角色id
     * @return boolean
     */
    boolean existsByRoleId(Long roleId);

    /**
     * 根据角色id查询
     *
     * @param roleId 角色id
     * @return List
     */
    List<SystemUser> findByRoleId(Long roleId);
}
