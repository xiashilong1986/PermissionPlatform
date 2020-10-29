package com.permission.security.service.impl;

import com.permission.security.dao.SystemMenuDao;
import com.permission.security.entity.OperateRoleMenuEntity;
import com.permission.security.entity.SystemButton;
import com.permission.security.entity.SystemMenu;
import com.permission.security.entity.SystemRole;
import com.permission.security.service.SystemButtonService;
import com.permission.security.service.SystemMenuService;
import com.permission.security.service.SystemRoleService;
import com.permission.utils.global.exception.GlobalException;
import com.permission.utils.global.exception.NullException;
import com.permission.utils.string.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-02   *
 * * Time: 11:27        *
 * * to: lz&xm          *
 * **********************
 **/
@Service
public class SystemMenuServiceImpl implements SystemMenuService {

    private final SystemMenuDao dao;

    //角色
    private final SystemRoleService systemRoleService;

    //按钮
    private final SystemButtonService systemButtonService;

    @Autowired
    public SystemMenuServiceImpl(SystemMenuDao dao, SystemRoleService systemRoleService, SystemButtonService systemButtonService) {
        this.dao = dao;
        this.systemRoleService = systemRoleService;
        this.systemButtonService = systemButtonService;
    }

    /**
     * 创建菜单
     *
     * @param systemMenu <see>SystemMenu</see>
     * @return SystemMenu
     */
    @Override
    public SystemMenu add(SystemMenu systemMenu) {
        return dao.save(systemMenu);
    }

    /**
     * 修改菜单
     *
     * @param systemMenu <see>SystemMenu</see>
     * @return SystemMenu
     */
    @Override
    public SystemMenu update(SystemMenu systemMenu) {
        return dao.update(systemMenu);
    }

    /**
     * 删除主菜单
     *
     * @param id 主键
     */
    @Override
    @Transactional
    public void deletePrimary(Long id) {
        //删除菜单
        List<SystemMenu> childMenuList = dao.findByPid(id);
        List<Long> menuIdList = childMenuList.stream()
                .map(SystemMenu::getId)
                .collect(Collectors.toList());
        menuIdList.add(id);
        //删除菜单下所有按钮
        List<Long> buttonIdList = systemButtonService.deleteAll(menuIdList);
        systemRoleService.updateAllRolePermission(menuIdList, buttonIdList);
        dao.deleteByIdIn(menuIdList);
    }

    /**
     * 删除子菜单
     *
     * @param id 主键
     */
    @Override
    @Transactional
    public void deleteChild(Long id) {
        List<SystemMenu> list = dao.findByPid(id);
        if (!list.isEmpty()) {
            throw GlobalException.exception100("该菜单包含子菜单,无法删除!");
        }
        //删除菜单下所有按钮
        List<Long> buttonIdList = systemButtonService.deleteAll(id);
        systemRoleService.updateAllRolePermission(Collections.singletonList(id), buttonIdList);
        //删除菜单
        dao.deleteById(id);
    }

    /**
     * 获取app所有菜单及菜单下所有按钮
     *
     * @return List<SystemMenu>
     */
    @Override
    public List<SystemMenu> getAllAppMenu() {
        return getMenuByType(1);
    }

    /**
     * 获取管理端所有菜单及菜单下所有按钮
     *
     * @return List<SystemMenu>
     */
    @Override
    public List<SystemMenu> getAllManagementMenu() {
        return getMenuByType(0);
    }


    /**
     * 获取角色下所有菜单及按钮(赋予权限页面)
     *
     * @param operateRoleId 操作角色id
     * @param grantRoleId   被操作角色id
     * @return List<OperateRoleMenuEntity> <see>OperateRoleMenuEntity</see>
     */
    @Override
    public List<OperateRoleMenuEntity> getRoleMenu(Long operateRoleId, Long grantRoleId) {
        //获取两个角色的数据
        List<SystemRole> allRole = systemRoleService.getAll(Arrays.asList(operateRoleId, grantRoleId));
        //获取被操作者角色
        SystemRole grantRole = allRole.stream()
                .filter(r -> r.getId().equals(grantRoleId))
                .findAny()
                .get();
        //菜单id集合
        Set<Long> menuIdList = getIds(allRole.stream()
                .map(SystemRole::getMenuIdList));
        //按钮id集合
        Set<Long> buttonIdList = getIds(allRole.stream()
                .map(SystemRole::getButtonIdList));

        //获取所有菜单
        List<SystemMenu> menuList = dao.getByIdIn(menuIdList);
        //获取所有按钮
        List<SystemButton> buttonList = systemButtonService.getAll(StringUtil.longListToString(new ArrayList<>(buttonIdList)));

        //操作者的菜单集合
        List<SystemMenu> operateMenuList = getRoleMenu(menuList, allRole, operateRoleId);
        //被操作者的菜单集合
        List<SystemMenu> grantMenuList = !StringUtils.isEmpty(grantRole.getMenuIdList()) ? getRoleMenu(menuList, allRole, grantRoleId) : null;

        //操作者的按钮集合
        List<SystemButton> operateButtonList = getRoleButton(buttonList, allRole, operateRoleId);
        //被操作者的按钮集合
        List<SystemButton> grantButtonList = !StringUtils.isEmpty(grantRole.getButtonIdList()) ? getRoleButton(buttonList, allRole, grantRoleId) : null;
        //封装数据
        //按钮封装
        List<OperateRoleMenuEntity.OperateRoleButtonEntity> roleButtonList = operateButtonList.stream()
                .map(b ->
                        OperateRoleMenuEntity.OperateRoleButtonEntity.of(
                                b.getId(),
                                b.getName(),
                                b.getButtonInterface(),
                                b.getMenuId(),
                                null != grantButtonList && grantButtonList.contains(b)
                        )
                )
                .collect(Collectors.toList());
        //封装菜单
        return operateMenuList.stream()
                .map(m ->
                        OperateRoleMenuEntity.of(
                                m.getId(),
                                m.getPath(),
                                m.getName(),
                                m.getComponent(),
                                m.getMenuInterface(),
                                m.getSort(),
                                m.getPid(),
                                null != grantMenuList && grantMenuList.contains(m),
                                roleButtonList.stream()
                                        .filter(b -> b.getMenuId().equals(m.getId()))
                                        .collect(Collectors.toList()))
                ).collect(Collectors.toList());
    }

    /**
     * 获取开发者下所有菜单及按钮(赋予权限页面)
     *
     * @param grantRoleId 被操作角色id
     * @return List<OperateRoleMenuEntity> <see>OperateRoleMenuEntity</see>
     */
    @Override
    public List<OperateRoleMenuEntity> getDeveloperRoleMenu(Long grantRoleId) {
        //得到角色
        SystemRole role = systemRoleService.getOne(grantRoleId);
        //得到前端or后端所有菜单
        List<SystemMenu> menuList = role.getRoleType().equals(0) ? getAllManagementMenu() : getAllAppMenu();
        return menuList.stream()
                .map(
                        m ->
                                OperateRoleMenuEntity.of(
                                        m.getId(),
                                        m.getPath(),
                                        m.getName(),
                                        m.getComponent(),
                                        m.getMenuInterface(),
                                        m.getSort(),
                                        m.getPid(),
                                        !StringUtils.isEmpty(role.getMenuIdList()) && Arrays.asList(role.getMenuIdList().split(",")).contains(m.getId().toString()),
                                        m.getButtonList().stream()
                                                .map(b ->
                                                        OperateRoleMenuEntity.OperateRoleButtonEntity.of(
                                                                b.getId(),
                                                                b.getName(),
                                                                b.getButtonInterface(),
                                                                b.getMenuId(),
                                                                !StringUtils.isEmpty(role.getButtonIdList()) && Arrays.asList(role.getButtonIdList().split(",")).contains(b.getId().toString())
                                                        )
                                                )
                                                .collect(Collectors.toList()))
                ).collect(Collectors.toList());
    }

    /**
     * 获取所有app菜单(列表)
     *
     * @param page 页码
     * @param size 每页数量
     * @param pid  父级菜单id
     * @param name 模糊查询名称
     * @return Page<SystemMenu>
     */
    @Override
    public Page<SystemMenu> getAllAppMenu(Integer page, Integer size, Long pid, String name) {
        return getMenuList(page, size, pid, name, 1);
    }

    /**
     * 获取所有管理端菜单(列表)
     *
     * @param page 页码
     * @param size 每页数量
     * @param pid  父级菜单id
     * @param name 模糊查询名称
     * @return Page<SystemMenu>
     */
    @Override
    public Page<SystemMenu> getAllManagementMenu(Integer page, Integer size, Long pid, String name) {
        return getMenuList(page, size, pid, name, 0);
    }

    @Override
    public List<SystemMenu> getAll(String ids) {
        return dao.findByIdIn(StringUtil.stringsToLongList(ids, StringUtil.Delimiter.COMMA));
    }


    /**
     * 根据菜单类型查询
     *
     * @param menuType 前后端页面标识(0后端,1前端)
     * @return List<SystemMenu>
     */
    private List<SystemMenu> getMenuByType(Integer menuType) {
        List<SystemMenu> list = dao.findByMenuType(menuType, Sort.by(Sort.Order.asc("pid"), Sort.Order.asc("sort")));
        NullException.isEmpty(list);
        return list;
    }

    /**
     * 获取所有(列表)
     *
     * @param page     页码
     * @param size     每页数量
     * @param pid      父级菜单id
     * @param name     模糊查询名称
     * @param menuType 前后端页面标识(0后端,1前端)
     * @return Page<SystemMenu>
     */
    private Page<SystemMenu> getMenuList(Integer page, Integer size, Long pid, String name, Integer menuType) {
        Page<SystemMenu> all = dao.findAll((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("pid"), pid));
            predicates.add(builder.equal(root.get("menuType"), menuType));
            if (!StringUtils.isEmpty(name)) {
                predicates.add(builder.like(root.get("name"), "%" + name + "%"));
            }
            query.where(builder.and(predicates.toArray(new Predicate[0])));
            return query.getRestriction();
        }, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sort")));
        NullException.isEmpty(all.getContent());
        return all;
    }

    //string id 转集合
    private Set<Long> getIds(Stream<String> stringStream) {
        return stringStream.flatMap(s -> {
                    if (!StringUtils.isEmpty(s)) {
                        return Arrays.stream(s.split(StringUtil.Delimiter.COMMA.getName()));
                    } else {
                        return null;
                    }
                }
        )
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    //获取角色菜单
    private List<SystemMenu> getRoleMenu(List<SystemMenu> menuList, List<SystemRole> allRole, Long roleId) {
        return allRole.stream()
                .filter(r -> r.getId().equals(roleId))
                .flatMap(id -> menuList.stream()
                        .filter(m -> StringUtil.stringsToLongList(id.getMenuIdList(), StringUtil.Delimiter.COMMA).contains(m.getId()))
                ).collect(Collectors.toList());
    }

    //获取角色按钮
    private List<SystemButton> getRoleButton(List<SystemButton> buttonList, List<SystemRole> allRole, Long roleId) {
        return allRole.stream()
                .filter(r -> r.getId().equals(roleId))
                .flatMap(id -> {
                            if (!StringUtils.isEmpty(id.getButtonIdList())) {
                                return buttonList.stream()
                                        .filter(b -> StringUtil.stringsToLongList(id.getButtonIdList(), StringUtil.Delimiter.COMMA).contains(b.getId()));
                            } else {
                                return null;
                            }
                        }
                ).collect(Collectors.toList());
    }
}
