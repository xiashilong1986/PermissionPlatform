package com.permission.security.service.impl;

import com.permission.security.dao.SystemButtonDao;
import com.permission.security.entity.SystemButton;
import com.permission.security.service.SystemButtonService;
import com.permission.security.service.SystemRoleService;
import com.permission.utils.global.exception.NullException;
import com.permission.utils.string.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-02   *
 * * Time: 14:16        *
 * * to: lz&xm          *
 * **********************
 **/
@Service
public class SystemButtonServiceImpl implements SystemButtonService {

    private final SystemButtonDao dao;

    //角色
    private final SystemRoleService systemRoleService;

    @Autowired
    public SystemButtonServiceImpl(SystemButtonDao dao, SystemRoleService systemRoleService) {
        this.dao = dao;
        this.systemRoleService = systemRoleService;
    }

    /**
     * 创建
     *
     * @param name            名称
     * @param buttonInterface 按钮对应接口,逗号分隔多个接口
     * @param menuId          菜单id
     * @return SystemButton
     */
    @Override
    public SystemButton add(String name, String buttonInterface, Long menuId) {
        return dao.save(
                SystemButton.of(
                        name, buttonInterface, menuId)
        );
    }

    /**
     * 修改
     *
     * @param id              主键
     * @param name            名称
     * @param buttonInterface 按钮对应接口,逗号分隔多个接口
     * @param menuId          菜单id
     * @return SystemButton
     */
    @Override
    public SystemButton update(Long id, String name, String buttonInterface, Long menuId) {
        return dao.update(
                SystemButton.of(
                        id, name, buttonInterface, menuId)
        );
    }

    /**
     * 删除
     *
     * @param id 主键
     */
    @Override
    public void delete(Long id) {
        systemRoleService.updateAllRolePermission(null, Collections.singletonList(id));
        dao.deleteById(id);
    }

    /**
     * 删除菜单下所有按钮
     *
     * @param menuId 菜单id
     * @return 被删除id集合
     */
    @Override
    public List<Long> deleteAll(Long menuId) {
        List<SystemButton> list = dao.findByMenuId(menuId);
        dao.deleteByMenuId(menuId);
        return list.stream().map(SystemButton::getId).collect(Collectors.toList());
    }

    /**
     * 批量删除菜单下按钮
     *
     * @param menuIds 菜单id集合
     * @return 被删除id集合
     */
    @Override
    public List<Long> deleteAll(List<Long> menuIds) {
        List<SystemButton> list = dao.findByMenuIdIn(menuIds);
        dao.deleteByMenuIdIn(menuIds);
        return list.stream().map(SystemButton::getId).collect(Collectors.toList());
    }

    /**
     * 获取菜单下所有按钮
     *
     * @param page   页码
     * @param size   每页数量
     * @param menuId 菜单id
     * @param name   模糊查询名称
     * @return Page<SystemButton>
     */
    @Override
    public Page<SystemButton> getAll(Integer page, Integer size, Long menuId, String name) {
        Page<SystemButton> all = dao.findAll((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("menuId"), menuId));
            if (!StringUtils.isEmpty(name)) {
                predicates.add(builder.like(root.get("name"), "%" + name + "%"));
            }
            query.where(builder.and(predicates.toArray(new Predicate[0])));
            return query.getRestriction();
        }, PageRequest.of(page, size));
        NullException.isEmpty(all.getContent());
        return all;
    }

    /**
     * 获取所有按钮
     *
     * @param ids 字符串id集合
     * @return List<SystemButton>
     */
    @Override
    public List<SystemButton> getAll(String ids) {
        return dao.findByIdIn(StringUtil.stringsToLongList(ids, StringUtil.Delimiter.COMMA));
    }
}
