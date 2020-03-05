package com.permission.utils.router;

import com.permission.security.entity.SystemButton;
import com.permission.security.entity.SystemMenu;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-05   *
 * * Time: 10:34        *
 * * to: lz&xm          *
 * **********************
 * vue路由器工具类
 **/
public class RouterUtil {
    //创建路由对象; 可做优化!
    public static List<Router> createRouter(List<SystemMenu> menuList) {
        //所有父级菜单
        List<SystemMenu> topMenuList = menuList.stream()
                .filter(s -> s.getPid() == 0)
                .collect(Collectors.toList());
        //转换成子级菜单
        menuList.removeAll(topMenuList);
        //父页面集合
        List<Router> routerList = new ArrayList<>();

        //页面对象
        Router router;
        for (SystemMenu topMenu : topMenuList) {
            router = Router.of(
                    topMenu.getPath(),
                    topMenu.getName(),
                    topMenu.getComponent(),
                    topMenu.isNavigationShow(),
                    topMenu.getSort(),
                    getButtonList(topMenu.getButtonList())
            );
            //子界面集合
            List<Router> childrenRouterList = new ArrayList<>();
            for (SystemMenu childrenMenu : menuList) {
                if (topMenu.getId().equals(childrenMenu.getPid())) {
                    childrenRouterList.add(
                            Router.of(
                                    childrenMenu.getPath(),
                                    childrenMenu.getName(),
                                    childrenMenu.getComponent(),
                                    childrenMenu.isNavigationShow(),
                                    childrenMenu.getSort(),
                                    getButtonList(childrenMenu.getButtonList())
                            )
                    );
                }
            }
            routerList.add(router.of(childrenRouterList));
        }
        routerList.sort(Comparator.comparing(Router::getSort));
        return routerList;
    }

    //获取按钮集合
    private static List<Meta> getButtonList(List<SystemButton> buttonList) {
        List<Meta> metaList = new ArrayList<>();
        for (SystemButton button : buttonList) {
            metaList.add(Meta.of(
                    button.getId(),
                    button.getName(),
                    button.isShow()));
        }
        return metaList;
    }


    //路由对象
    @Data
    public static class Router implements Serializable {
        private String path;
        private String name;
        private String component;
        private boolean navigationShow;
        private Integer sort;
        private List<Meta> meta;
        private List<Router> children;

        public static Router of(String path, String name, String component, boolean navigationShow, Integer sort, List<Meta> meta) {
            Router r = new Router();
            r.path = path;
            r.name = name;
            r.component = component;
            r.navigationShow = navigationShow;
            r.sort = sort;
            r.meta = meta;
            return r;
        }

        public Router of(List<Router> children) {
            this.children = children;
            return this;
        }
    }

    //路由参数;相当于按钮
    @Data
    public static class Meta implements Serializable {
        private Long id;
        private String name;
        private boolean isShow;

        public static Meta of(Long id, String name, boolean isShow) {
            Meta m = new Meta();
            m.id = id;
            m.name = name;
            m.isShow = isShow;
            return m;
        }
    }
}
