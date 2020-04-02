package com.permission.security.service;

import com.permission.security.entity.SystemUser;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 16:27        *
 * * to: lz&xm          *
 * **********************
 **/
public interface SystemUserService {

    /**
     * 注册
     *
     * @param username  用户名
     * @param password  密码
     * @param creatorId 创建者id(商户id)
     * @param roleId    角色id
     */
    Long registered(String username, String password, Long creatorId, Long roleId);

    /**
     * 修改用户密码
     *
     * @param id          主键
     * @param password    密码
     * @param newPassword 新密码
     */
    void modifyPassword(Long id, String password, String newPassword);

    /**
     * 管理员修改用户密码
     *
     * @param id       主键
     * @param password 密码
     */
    void modifyPassword(Long id, String password);

    /**
     * 删除
     *
     * @param id 主键
     */
    void deleteUser(Long id);

    /**
     * 锁定账号
     *
     * @param id 主键
     */
    void locked(Long id);

    /**
     * 解锁账号
     *
     * @param id 主键
     */
    void unlocked(Long id);

    /**
     * 创建账号
     *
     * @param username  用户名
     * @param password  密码
     * @param creatorId 创建者id
     * @return Long 主键
     */
    Long createUser(String username, String password, Long creatorId);

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
    Page<SystemUser> getAll(Integer page, Integer size, String username, Integer accountLocked, Long creatorId);

    /**
     * 为用户赋值角色
     *
     * @param userId 用户id
     * @param roleId 角色id
     */
    void giveRole(Long userId, Long roleId);

    /**
     * 根据角色id查询
     *
     * @param roleId 角色id
     * @return List<SystemUser>
     */
    List<SystemUser> getUserByRoleId(Long roleId);

    /**
     * 获取用户登陆信息
     *
     * @param username 用户名
     * @return map
     */
    Map getUserLoginContext(String username);

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
    Map<String, Object> authLogin(Long roleId, String authId, String ip);
}
