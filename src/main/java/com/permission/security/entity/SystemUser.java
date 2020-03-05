package com.permission.security.entity;

import com.permission.utils.abstractentity.AbstractEntity;
import com.permission.utils.router.RouterUtil;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 11:20        *
 * * to: lz&xm          *
 * **********************
 * 系统账号
 **/
@Entity
@Table(name = "system_user",
        indexes = {
                @Index(columnList = "creatorId"),
                @Index(columnList = "roleId")
        }
)
@NamedEntityGraph(name = "SystemUser.role",
        attributeNodes = @NamedAttributeNode("systemRole"))
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemUser extends AbstractEntity implements Serializable, UserDetails {

    private static final long serialVersionUID = 1649063607905998663L;

    /**
     * 账号
     */
    @Column(unique = true, length = 50)
    private String username;

    /**
     * 密码
     */
    @Column(length = 100)
    private String password;

    /**
     * 锁定 (0否,1是)
     */
    @Column(insertable = false, columnDefinition = "int(1) default 0")
    private Integer accountLocked;

    /**
     * 创建时间
     */
    @Column
    private LocalDateTime createTime;

    /**
     * 创建者id(前端用户为商户id)
     */
    @Column
    private Long creatorId;

    /**
     * 前后端页面标识(0后端,1前端)
     */
    @Column(columnDefinition = "int(1) default 0")
    private Integer userType;

    /**
     * 用户唯一标识
     */
    @Column(length = 64)
    private String uuId;

    /**
     * 角色id
     */
    @Column
    private Long roleId;

    /**
     * 用户角色
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumn(name = "roleId", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    private SystemRole systemRole;

    /**
     * 接口权限集合
     */
    @Transient
    private Set<SystemInterface> systemInterfaceList;

    /**
     * 路由字符串
     */
    @Transient
    private List<RouterUtil.Router> router;

    //为用户赋值权限
    public SystemUser ofInterfaceList(List<SystemMenu> menuList, List<SystemButton> buttonList) {
        this.systemInterfaceList = new HashSet<>();
        //赋值页面权限
        if (!menuList.isEmpty()) {
            for (SystemMenu systemMenu : menuList) {
                if (!StringUtils.isEmpty(systemMenu.getMenuInterface())) {
                    this.systemInterfaceList.addAll(Arrays.stream(systemMenu.getMenuInterface().split(","))
                            .map(SystemInterface::of)
                            .collect(Collectors.toList())
                    );
                }
                //筛选按钮权限
                if (null != systemMenu.getButtonList() && !systemMenu.getButtonList().isEmpty()) {
                    for (SystemButton button : systemMenu.getButtonList()) {
                        for (SystemButton b : buttonList) {
                            if (button.getId().equals(b.getId())) {
                                button.of(button.getId().equals(b.getId()));
                                if (!StringUtils.isEmpty(b.getButtonInterface())) {
                                    this.systemInterfaceList.addAll(Arrays.stream(b.getButtonInterface().split(","))
                                            .map(SystemInterface::of)
                                            .collect(Collectors.toList()));
                                }
                                break;
                            }
                        }
                    }
                }
            }
            //转换路由
            this.router = RouterUtil.createRouter(menuList);
        }
        return this;
    }

    //注册
    public static SystemUser of(String username, String password, Long creatorId, Integer userType) {
        SystemUser s = new SystemUser();
        s.username = username;
        s.password = new BCryptPasswordEncoder().encode(password);
        s.createTime = LocalDateTime.now();
        s.creatorId = creatorId;
        s.userType = userType;
        s.uuId = UUID.randomUUID().toString();
        return s;
    }

    //修改密码
    public static SystemUser of(Long id, String password) {
        SystemUser s = new SystemUser();
        s.setId(id);
        s.password = new BCryptPasswordEncoder().encode(password);
        return s;
    }

    //锁定
    public static SystemUser of(Long id, Integer accountLocked) {
        SystemUser s = new SystemUser();
        s.setId(id);
        s.accountLocked = accountLocked;
        return s;
    }

    //赋予角色
    public static SystemUser of(Long id, Long roleId) {
        SystemUser s = new SystemUser();
        s.setId(id);
        s.roleId = roleId;
        return s;
    }

    //返回给用户端
    public static SystemUser of(Long id, String username, Long roleId, Integer accountLocked) {
        SystemUser s = new SystemUser();
        s.setId(id);
        s.username = username;
        s.roleId = roleId;
        s.accountLocked = accountLocked;
        return s;
    }

    public SystemUser of(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return systemInterfaceList;
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.accountLocked == 0;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
