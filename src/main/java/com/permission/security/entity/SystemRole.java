package com.permission.security.entity;

import com.permission.utils.abstractentity.AbstractEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 14:31        *
 * * to: lz&xm          *
 * **********************
 * 系统角色
 **/
@Entity
@Table(name = "system_role",
        indexes = @Index(columnList = "creatorId")
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class SystemRole extends AbstractEntity {

    /**
     * 角色名
     */
    @Column(unique = true, length = 20)
    private String name;


    /**
     * 创建者id
     */
    @Column
    private Long creatorId;

    /**
     * 前后端页面标识(0后端,1前端)
     */
    @Column(columnDefinition = "int(1) default 0")
    private Integer roleType;

    /**
     * 菜单id集合(逗号分隔)
     */
    @Column(length = 2000)
    private String menuIdList;

    /**
     * 按钮id集合(逗号分隔)
     */
    @Column(length = 2000)
    private String buttonIdList;


    /**
     * 创建
     *
     * @param name      角色名
     * @param creatorId 创建者id
     * @param roleType  前后端页面标识(0后端,1前端)
     * @return SystemRole
     */
    public static SystemRole of(String name, Long creatorId, Integer roleType) {
        SystemRole s = new SystemRole();
        s.name = name;
        s.creatorId = creatorId;
        s.roleType = roleType;
        return s;
    }

    /**
     * 修改
     *
     * @param id   主键
     * @param name 角色名
     * @return SystemRole
     */
    public static SystemRole of(Long id, String name) {
        SystemRole s = new SystemRole();
        s.setId(id);
        s.name = name;
        return s;
    }

    public static SystemRole of(Long id) {
        SystemRole s = new SystemRole();
        s.setId(id);
        return s;
    }

    /**
     * 更新角色菜单
     *
     * @param menuIdList 菜单id集合
     */
    public SystemRole ofMenuIdList(String menuIdList) {
        this.menuIdList = menuIdList;
        return this;
    }

    /**
     * 更新角色按钮
     *
     * @param buttonIdList 按钮id集合
     */
    public SystemRole ofButtonIdList(String buttonIdList) {
        this.buttonIdList = buttonIdList;
        return this;
    }
}
