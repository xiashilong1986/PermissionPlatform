package com.permission.security.entity;

import com.permission.utils.abstractentity.AbstractEntity;
import lombok.*;

import javax.persistence.*;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-31   *
 * * Time: 16:54        *
 * * to: lz&xm          *
 * **********************
 * 系统按钮
 **/
@Entity
@Table(name = "system_button",
        indexes = {
                @Index(columnList = "menuId")
        }
)
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemButton extends AbstractEntity {

    /**
     * 名称
     */
    @Column(unique = true, length = 50)
    private String name;

    /**
     * 按钮对应接口,逗号分隔多个接口
     */
    @Column(length = 200)
    private String buttonInterface;

    /**
     * 菜单id
     */
    @Column
    private Long menuId;

    //是否显示
    @Transient
    private boolean isShow;

    /**
     * 创建
     *
     * @param name            名称
     * @param buttonInterface 按钮对应接口,逗号分隔多个接口
     * @param menuId          菜单id
     * @return SystemButton
     */
    public static SystemButton of(String name, String buttonInterface, Long menuId) {
        SystemButton s = new SystemButton();
        s.name = name;
        s.buttonInterface = buttonInterface;
        s.menuId = menuId;
        return s;
    }

    /**
     * 修改
     *
     * @param name            名称
     * @param buttonInterface 按钮对应接口,逗号分隔多个接口
     * @param menuId          菜单id
     * @return SystemButton
     */
    public static SystemButton of(Long id, String name, String buttonInterface, Long menuId) {
        SystemButton s = new SystemButton();
        s.setId(id);
        s.name = name;
        s.buttonInterface = buttonInterface;
        s.menuId = menuId;
        return s;
    }

    //设置显隐
    public SystemButton of(boolean isShow) {
        this.isShow = isShow;
        return this;
    }
}
