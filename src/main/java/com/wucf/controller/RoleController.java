package com.wucf.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoleController {
    /**
     * 如果有ADMIN角色就返回hello页面
     * @return
     */
    @GetMapping("/testAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public String testAdmin(){
        return "hello";
    }

    /**
     * 如果有DEV角色就返回hello页面
     * 由于在{@link com.wucf.config.WebSecurityConfig#userDetailsService}中只定义了ADMIN和USER角色
     * 因此此链接必定返回403错误码
     *
     * @return
     */
    @GetMapping("/testDev")
    @PreAuthorize("hasRole('DEV')")
    public String testDev(){
        return "hello";
    }

    /**
     * 以API的形式定义某个链接的权限
     * WebSecurityConfig antMatchers("/testHR").hasAnyRole("HR")
     *
     * 如果有HR角色就返回hello页面
     * 由于在{@link com.wucf.config.WebSecurityConfig#userDetailsService}中只定义了ADMIN和USER角色
     * 因此此链接必定返回403错误码
     *
     * @return
     */
    @GetMapping("/testHR")
    public String testHR(){
        return "hello";
    }
}
