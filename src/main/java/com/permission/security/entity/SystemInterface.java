package com.permission.security.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 14:54        *
 * * to: lz&xm          *
 * **********************
 * 用户拥有接口
 **/
@Data
public class SystemInterface implements GrantedAuthority {

    /**
     * 接口路径
     */
    private String url;


    public static SystemInterface of(String url) {
        SystemInterface s = new SystemInterface();
        s.url = url;
        return s;
    }

    @Override
    public String getAuthority() {
        return this.url;
    }
}
