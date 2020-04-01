package com.permission.security.entity;

import com.permission.utils.abstractentity.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-31   *
 * * Time: 15:01        *
 * * to: lz&xm          *
 * **********************
 * 系统菜单
 **/
@Entity
@Table(name = "system_menu",
        indexes = {
                @Index(columnList = "menuType")
        }
)
@NamedEntityGraph(name = "SystemMenu.button",
        attributeNodes = @NamedAttributeNode("buttonList"))
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemMenu extends AbstractEntity {

    /**
     * 页面路径
     */
    @Column(unique = true, length = 200)
    private String path;

    /**
     * 页面名称
     */
    @Column(length = 20)
    private String name;

    /**
     * 路由变量名
     */
    @Column(length = 50)
    private String component;

    /**
     * 页面对应接口,逗号分隔多个接口
     */
    @Column(length = 200)
    private String menuInterface;

    /**
     * 菜单排序
     */
    @Column
    private Integer sort;

    /**
     * 父菜单id 如果为0则为顶级菜单
     */
    @Column(columnDefinition = "bigint(20) default 0")
    private Long pid;

    /**
     * 是否在导航栏显示(0不显示,1显示)
     */
    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean navigationShow;

    /**
     * 前后端页面标识(0后端,1前端)
     */
    @Column(columnDefinition = "int(1) default 0")
    private Integer menuType;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany
    @JoinColumn(name = "menuId", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    private List<SystemButton> buttonList;

    /**
     * 创建菜单
     *
     * @param path           路径
     * @param name           名称
     * @param component      路由变量名
     * @param menuInterface  页面对应接口,逗号分隔多个接口
     * @param sort           菜单排序
     * @param pid            父菜单id 如果为0则为顶级菜单
     * @param navigationShow 是否在导航栏显示(0不显示,1显示)
     * @param menuType       前后端页面标识(0后端,1前端)
     * @return SystemMenu
     */
    public static SystemMenu of(String path, String name, String component, String menuInterface, Integer sort, Long pid, boolean navigationShow, Integer menuType) {
        SystemMenu s = new SystemMenu();
        s.path = path;
        s.name = name;
        s.component = component;
        s.menuInterface = menuInterface;
        s.sort = sort;
        s.pid = pid;
        s.navigationShow = navigationShow;
        s.menuType = menuType;
        return s;
    }

    /**
     * 修改菜单
     *
     * @param id             主键
     * @param path           路径
     * @param name           名称
     * @param component      路由变量名
     * @param menuInterface  页面对应接口,逗号分隔多个接口
     * @param sort           菜单排序
     * @param pid            父菜单id 如果为0则为顶级菜单
     * @param navigationShow 是否在导航栏显示(0不显示,1显示)
     * @return SystemMenu
     */
    public static SystemMenu of(Long id, String path, String name, String component, String menuInterface, Integer sort, Long pid, boolean navigationShow) {
        SystemMenu s = new SystemMenu();
        s.setId(id);
        s.path = path;
        s.name = name;
        s.component = component;
        s.menuInterface = menuInterface;
        s.sort = sort;
        s.pid = pid;
        s.navigationShow = navigationShow;
        return s;
    }


}
