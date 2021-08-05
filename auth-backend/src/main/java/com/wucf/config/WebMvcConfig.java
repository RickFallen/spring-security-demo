package com.wucf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //解决response返回json中文乱码问题
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .findFirst()
                .ifPresent(converter -> ((MappingJackson2HttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8));
    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/system/main").setViewName("main");

        //用户管理
        registry.addViewController("/system/user").setViewName("system/user/user");
        registry.addViewController("/system/user/add").setViewName("system/user/add");
        registry.addViewController("/system/user/edit").setViewName("system/user/edit");
        registry.addViewController("/system/user/resetPwd").setViewName("system/user/resetPwd");
        registry.addViewController("/system/user/authRole").setViewName("system/user/authRole");
        //用户资料
        registry.addViewController("/system/user/profile").setViewName("system/user/profile/profile");
        registry.addViewController("/system/user/avatar").setViewName("system/user/profile/avatar");
        registry.addViewController("/system/user/resetPwd").setViewName("system/user/profile/resetPwd");

        //角色管理
        registry.addViewController("/system/role").setViewName("system/role/role");
        registry.addViewController("/system/role/add").setViewName("system/role/add");
        registry.addViewController("/system/role/edit").setViewName("system/role/edit");
        registry.addViewController("/system/role/authUser/selectUser").setViewName("system/role/selectUser");
        registry.addViewController("/system/role/authDataScope").setViewName("system/role/dataScope");
        registry.addViewController("/system/role/authUser").setViewName("system/role/authUser");

        //菜单管理
        registry.addViewController("/system/menu").setViewName("system/menu/menu");
        registry.addViewController("/system/menu/add").setViewName("system/menu/add");
        registry.addViewController("/system/menu/edit").setViewName("system/menu/edit");
        registry.addViewController("/system/menu/tree").setViewName("system/menu/tree");
        registry.addViewController("/system/menu/selectMenuTree").setViewName("system/menu/icon");

        //岗位管理
        registry.addViewController("/system/post").setViewName("system/post/post");
        registry.addViewController("/system/add").setViewName("system/post/add");
        registry.addViewController("/system/edit").setViewName("system/post/edit");

        //部门管理
        registry.addViewController("/system/dept").setViewName("system/dept/dept");
        registry.addViewController("/system/dept/add").setViewName("system/dept/add");
        registry.addViewController("/system/dept/edit").setViewName("system/dept/edit");
        registry.addViewController("/system/dept/selectDeptTree/{deptId:\\w+}").setViewName("system/dept/tree");
        registry.addViewController("/system/dept/selectDeptTree/{deptId:\\w+}/{excludeId:\\w+}").setViewName("system/dept/tree");
    }
}
