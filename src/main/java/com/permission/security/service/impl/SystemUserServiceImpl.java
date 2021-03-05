package com.permission.security.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.permission.security.accesslimit.AccessLimit;
import com.permission.security.dao.SystemUserDao;
import com.permission.security.entity.SystemButton;
import com.permission.security.entity.SystemMenu;
import com.permission.security.entity.SystemRole;
import com.permission.security.entity.SystemUser;
import com.permission.security.jwt.JwtTokenUtil;
import com.permission.security.service.SystemButtonService;
import com.permission.security.service.SystemMenuService;
import com.permission.security.service.SystemRoleService;
import com.permission.security.service.SystemUserService;
import com.permission.utils.global.exception.GlobalException;
import com.permission.utils.global.exception.NullException;
import com.permission.utils.global.result.ResultEnum;
import com.permission.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 16:30        *
 * * to: lz&xm          *
 * **********************
 **/
@Service
public class SystemUserServiceImpl implements UserDetailsService, SystemUserService {

    //用户
    private final SystemUserDao dao;

    //菜单
    private final SystemMenuService systemMenuService;

    //按钮
    private final SystemButtonService systemButtonService;

    //角色
    private final SystemRoleService systemRoleService;

    @Autowired
    public SystemUserServiceImpl(SystemUserDao dao, SystemMenuService systemMenuService, SystemButtonService systemButtonService, SystemRoleService systemRoleService) {
        this.dao = dao;
        this.systemMenuService = systemMenuService;
        this.systemButtonService = systemButtonService;
        this.systemRoleService = systemRoleService;
    }

    /**
     * 注册
     *
     * @param username  用户名
     * @param password  密码
     * @param creatorId 创建者id(商户id)
     * @param roleId    角色id
     */
    @Override
    @Transactional
    public Long registered(String username, String password, Long creatorId, Long roleId) {
        //创建前端用户
        return dao.save(
                SystemUser.of(
                        username, password, creatorId, 1
                ).of(roleId)
        ).getId();
    }

    /**
     * 修改用户密码
     *
     * @param id          主键
     * @param password    密码
     * @param newPassword 新密码
     */
    @Override
    public void modifyPassword(Long id, String password, String newPassword) {
        Optional<SystemUser> optional = dao.findById(id);
        if (!optional.isPresent()) {
            throw new GlobalException(ResultEnum.ACCOUNT_DOES_NOT_EXIST);//账号不存在
        }
        SystemUser user = optional.get();
        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            throw new GlobalException(ResultEnum.WRONG_PASSWORD);//原密码错误
        }
        //清除token;redis辅助
        RedisUtil.delete(user.getUsername());
        dao.update(
                SystemUser.of(id, newPassword)
        );
    }

    /**
     * 管理员修改用户密码
     *
     * @param id       主键
     * @param password 密码
     */
    @Override
    public void modifyPassword(Long id, String password) {
        cleanRedis(id);
        dao.update(
                SystemUser.of(id, password)
        );
    }

    /**
     * 删除
     *
     * @param id 主键
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        cleanRedis(id);
        dao.deleteById(id);
    }

    /**
     * 锁定账号
     *
     * @param id 主键
     */
    @Override
    public void locked(Long id) {
        cleanRedis(id);
        dao.update(
                SystemUser.of(id, 1)
        );
    }

    /**
     * 解锁账号
     *
     * @param id 主键
     */
    @Override
    public void unlocked(Long id) {
        dao.update(
                SystemUser.of(id, 0)
        );
    }

    /**
     * 创建账号
     *
     * @param username  用户名
     * @param password  密码
     * @param creatorId 创建者id
     * @return Long 主键
     */
    @Override
    public Long createUser(String username, String password, Long creatorId) {
        return dao.save(
                SystemUser.of(username, password, creatorId, 0)
        ).getId();
    }

    /**
     * 获取所有管理账户
     *
     * @param page          页码
     * @param size          每页数量
     * @param username      模糊查询用户名
     * @param accountLocked 锁定 (0否,1是)
     * @param creatorId     创建者id
     * @return Page<SystemUser>
     */
    @Override
    public Page<SystemUser> getAll(Integer page, Integer size, String username, Integer accountLocked, Long creatorId) {
        Page<SystemUser> all = dao.findAll((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("creatorId"), creatorId));
            predicates.add(builder.equal(root.get("userType"), 0));
            if (!StringUtils.isEmpty(username)) {
                predicates.add(builder.like(root.get("username"), "%" + username + "%"));
            }
            if (!StringUtils.isEmpty(accountLocked)) {
                predicates.add(builder.equal(root.get("accountLocked"), accountLocked));
            }
            query.where(builder.and(predicates.toArray(new Predicate[0])));
            return query.getRestriction();
        }, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime")));
        NullException.isEmpty(all.getContent());
        return all;
    }

    /**
     * 为用户赋值角色
     *
     * @param userId 用户id
     * @param roleId 角色id
     */
    @Override
    public void giveRole(Long userId, Long roleId) {
        cleanRedis(userId);
        dao.update(
                SystemUser.of(userId, roleId)
        );
    }

    /**
     * 根据角色id查询
     *
     * @param roleId 角色id
     * @return List<SystemUser>
     */
    @Override
    public List<SystemUser> getUserByRoleId(Long roleId) {
        return dao.findByRoleId(roleId);
    }

    /**
     * 获取用户登陆信息
     *
     * @param username 用户名
     * @return map
     */
    @Override
    public Map getUserLoginContext(String username) {
        String key = "web-view" + username;
        if (RedisUtil.hasKey(key)) {
            return JSONObject.parseObject((String) RedisUtil.get("web-view" + username), Map.class);
        }
        throw GlobalException.exception100("缓存中无用户信息");
    }

    /**
     * 授权登陆,适用于微信等第三方登陆后的系统授权
     *
     * @param roleId 授权的角色id
     * @param authId 授权id 微信的openId等
     * @param ip     登陆用户ip
     * @return Map -> token : 授权后的令牌
     * tokenExpireTime : 令牌有效时长
     * router : vue路由对象
     */
    @Override
    public Map<String, Object> authLogin(Long roleId, String authId, String ip) {
        //获取系统角色
        SystemRole role = systemRoleService.getOne(roleId);
        List<SystemMenu> menuList = new ArrayList<>();
        if (!StringUtils.isEmpty(role.getMenuIdList())) {
            menuList = systemMenuService.getAll(role.getMenuIdList());
        }
        List<SystemButton> buttonList = new ArrayList<>();
        if (!StringUtils.isEmpty(role.getButtonIdList())) {
            buttonList = systemButtonService.getAll(role.getButtonIdList());
        }
        //验证角色权限,并赋值
        SystemUser systemUser;
        if (!menuList.isEmpty() || !buttonList.isEmpty()) {
            systemUser = SystemUser.authInterfaceList(menuList, buttonList, authId);
        } else {
            throw GlobalException.exception100("授权登陆的角色无任何权限");
        }
        //获取token
        String token = JwtTokenUtil.tokenPrefix + JwtTokenUtil.generateAccessToken(systemUser, ip);
        //保存token到redis中,用户名是唯一的作为key, 登出,修改权限等操作用;设置的过期时间为token的过期时间
        RedisUtil.set(systemUser.getUsername(), token, JwtTokenUtil.tokenExpireTime);
        //返回对象
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("tokenExpireTime", JwtTokenUtil.tokenExpireTime);
//        map.put("router", systemUser.getRouter());
        map.put("user", systemUser);
        //为web-view保存信息
        RedisUtil.set("web-view" + systemUser.getUsername(), JSONObject.toJSONString(map), JwtTokenUtil.tokenExpireTime);
        return map;
    }

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return Optional
     */
    @Override
    public Optional<SystemUser> getByUsername(String username) {
        return dao.getByUsername(username);
    }


    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @AccessLimit
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SystemUser> optionalUser = dao.findByUsername(username);
        if (optionalUser.isPresent()) {
            SystemUser systemUser = optionalUser.get();
            List<SystemMenu> menuList = new ArrayList<>();
            if (!StringUtils.isEmpty(systemUser.getSystemRole().getMenuIdList())) {
                menuList = systemMenuService.getAll(systemUser.getSystemRole().getMenuIdList());
            }
            List<SystemButton> buttonList = new ArrayList<>();
            if (!StringUtils.isEmpty(systemUser.getSystemRole().getButtonIdList())) {
                buttonList = systemButtonService.getAll(systemUser.getSystemRole().getButtonIdList());
            }
//            if (menuList.isEmpty() && buttonList.isEmpty()) {
//                return systemUser;
//            }
//            return systemUser.ofInterfaceList(menuList, buttonList);
            return menuList.isEmpty() ? systemUser : systemUser.builder(menuList).ofInterfaceList(menuList, buttonList);
        }
        //账号不存在
        throw new UsernameNotFoundException(ResultEnum.ACCOUNT_DOES_NOT_EXIST.getMsg());
    }

    //清除用户redis
    private void cleanRedis(Long id) {
        Optional<SystemUser> optional = dao.findById(id);
        optional.ifPresent(u -> RedisUtil.delete(u.getUsername()));
    }

    private String getOriginalPassword(String openId) {
        try {
            return URLEncoder.encode(openId.toLowerCase(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
