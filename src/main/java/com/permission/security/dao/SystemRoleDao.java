package com.permission.security.dao;

import com.permission.security.entity.SystemRole;
import com.permission.utils.baseinterface.BaseJpaRepository;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 14:35        *
 * * to: lz&xm          *
 * **********************
 **/
public interface SystemRoleDao extends BaseJpaRepository<SystemRole, Long> {

    /**
     * 根据创建者id查询
     *
     * @param creatorId 创建者id
     * @return List<SystemRole>
     */
    List<SystemRole> findByCreatorId(Long creatorId);

    /**
     * 根据id集合查询
     *
     * @param ids id集合
     * @return List<SystemRole>
     */
    List<SystemRole> findByIdIn(List<Long> ids);
}
