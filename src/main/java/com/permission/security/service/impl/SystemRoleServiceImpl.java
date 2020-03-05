package com.permission.security.service.impl;

import com.permission.security.dao.SystemRoleDao;
import com.permission.security.dao.SystemUserDao;
import com.permission.security.entity.RoleMenuEntity;
import com.permission.security.entity.SystemRole;
import com.permission.security.entity.SystemUser;
import com.permission.security.service.SystemRoleService;
import com.permission.utils.global.exception.GlobalException;
import com.permission.utils.global.exception.NullException;
import com.permission.utils.global.result.ResultEnum;
import com.permission.utils.redis.RedisUtil;
import com.permission.utils.string.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-06   *
 * * Time: 13:47        *
 * * to: lz&xm          *
 * **********************
 **/
@Service
public class SystemRoleServiceImpl implements SystemRoleService {

    private final SystemRoleDao dao;

    //用户
    private final SystemUserDao systemUserService;

    @Autowired
    public SystemRoleServiceImpl(SystemRoleDao dao, SystemUserDao systemUserService) {
        this.dao = dao;
        this.systemUserService = systemUserService;
    }


    /**
     * 创建
     *
     * @param name      角色名
     * @param creatorId 创建者id
     * @param roleType  前后端页面标识(0后端,1前端)
     * @return SystemRole
     */
    @Override
    public SystemRole add(String name, Long creatorId, Integer roleType) {
        return dao.save(
                SystemRole.of(name, creatorId, roleType)
        );
    }

    /**
     * 修改
     *
     * @param id   主键
     * @param name 角色名
     * @return SystemRole
     */
    @Override
    public SystemRole update(Long id, String name) {
        return dao.update(
                SystemRole.of(id, name)
        );
    }

    /**
     * 批量更新
     *
     * @param list 集合
     */
    @Override
    public void update(List<SystemRole> list) {
        dao.updateAll(list);
    }

    /**
     * 删除
     *
     * @param id 主键
     */
    @Override
    public void delete(Long id) {
        if (systemUserService.existsByRoleId(id)) {
            throw new GlobalException(ResultEnum.UNABLE_TO_DELETE);//有绑定此角色的用户,不能删除
        }
        dao.deleteById(id);
    }

    /**
     * 获取角色信息
     *
     * @param id 主键
     * @return SystemRole
     */
    @Override
    public SystemRole getOne(Long id) {
        return dao.findById(id).orElseThrow(GlobalException::new);
    }

    /**
     * 获取所有角色
     *
     * @param page      页码
     * @param size      每页数量
     * @param name      角色名模糊查询
     * @param creatorId 创建者id
     * @return Page<SystemRole>
     */
    @Override
    public Page<SystemRole> getAll(Integer page, Integer size, String name, Long creatorId) {
        Page<SystemRole> all = dao.findAll((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("creatorId"), creatorId));
            if (!StringUtils.isEmpty(name)) {
                predicates.add(builder.like(root.get("name"), "%" + name + "%"));
            }
            query.where(builder.and(predicates.toArray(new Predicate[0])));
            return query.getRestriction();
        }, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime")));
        NullException.isEmpty(all.getContent());
        return all;
    }

    /**
     * 获取所有角色(下拉框)
     *
     * @param creatorId 创建者id
     * @return List<SystemRole>
     */
    @Override
    public List<SystemRole> getAll(Long creatorId) {
        List<SystemRole> list = dao.findByCreatorId(creatorId);
        NullException.isEmpty(list);
        return list;
    }

    /**
     * 根据id集合获取角色
     *
     * @param ids 角色id集合
     * @return List<SystemRole>
     */
    @Override
    public List<SystemRole> getAll(List<Long> ids) {
        return dao.findByIdIn(ids);
    }

    /**
     * 为角色赋值权限
     *
     * @param roleMenuEntity <see>RoleMenuEntity</see>
     */
    @Override
    public void givePermission(RoleMenuEntity roleMenuEntity) {
        List<SystemUser> userList = systemUserService.findByRoleId(roleMenuEntity.getRoleId());
        //清除redis
        if (!userList.isEmpty()) {
            RedisUtil.delete(
                    userList.stream()
                            .map(SystemUser::getUsername)
                            .collect(Collectors.toList())
            );
        }
        dao.findById(roleMenuEntity.getRoleId()).ifPresent(systemRole -> dao.save(
                systemRole
                        .ofMenuIdList(null != roleMenuEntity.getMenuIdList() && !roleMenuEntity.getMenuIdList().isEmpty() ? StringUtil.longListToString(roleMenuEntity.getMenuIdList()) : null)//更新菜单权限
                        .ofButtonIdList(null != roleMenuEntity.getButtonIdList() && !roleMenuEntity.getButtonIdList().isEmpty() ? StringUtil.longListToString(roleMenuEntity.getButtonIdList()) : null)//更新按钮权限
        ));

    }

    /**
     * 删除菜单和按钮时,更新角色的权限
     *
     * @param menuIdList   被删除的菜单id集合
     * @param buttonIdList 被删除的按钮id集合
     */
    @Override
    public void updateAllRolePermission(List<Long> menuIdList, List<Long> buttonIdList) {
        List<SystemRole> roleList = dao.findAll();
        List<SystemRole> updateRole = new ArrayList<>();
        for (SystemRole role : roleList) {
            SystemRole systemRole = SystemRole.of(role.getId());
            //按钮
            if (null != buttonIdList && !buttonIdList.isEmpty()) {
                String buttonStr = "";
                for (Long buttonId : buttonIdList) {
                    if (!StringUtils.isEmpty(role.getButtonIdList()) && StringUtil.stringsToLongList(role.getButtonIdList(), StringUtil.Delimiter.COMMA).contains(buttonId)) {
                        buttonStr = StringUtil.deleteChar(!"".equals(buttonStr) ? buttonStr : role.getButtonIdList(), buttonId.toString());
                    }
                }
                if (!"".equals(buttonStr)) {
                    systemRole.ofButtonIdList(buttonStr);
                }
            }
            //菜单
            if (null != menuIdList && !menuIdList.isEmpty()) {
                String menuStr = "";
                for (Long menuId : menuIdList) {
                    if (!StringUtils.isEmpty(role.getMenuIdList()) && StringUtil.stringsToLongList(role.getMenuIdList(), StringUtil.Delimiter.COMMA).contains(menuId)) {
                        menuStr = StringUtil.deleteChar(!"".equals(menuStr) ? menuStr : role.getMenuIdList(), menuId.toString());
                    }
                }
                if (!"".equals(menuStr)) {
                    systemRole.ofMenuIdList(menuStr);
                }
            }
            if ((null != systemRole.getButtonIdList() && !systemRole.getButtonIdList().isEmpty()) || (null != systemRole.getMenuIdList() && !systemRole.getMenuIdList().isEmpty())) {
                updateRole.add(systemRole);
            }
        }
        if (!updateRole.isEmpty()) {
            update(updateRole);
        }
    }
}
