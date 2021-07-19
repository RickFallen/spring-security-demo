package com.wucf.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {
    /**
     * 如果有ADMIN角色就返回字符串
     * @return
     */
    @GetMapping("/testAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public String testAdmin(){
        return "role:admin";
    }

    /**
     * 由于在{@link com.wucf.config.WebSecurityConfig#userDetailsService}中只定义了ADMIN和USER角色
     * 因此此链接必定返回403错误码
     *
     * @return
     */
    @GetMapping("/testUser")
    @PreAuthorize("hasRole('USER')")
    public String testDev(){
        return "role:user";
    }

    /**
     * 如果有HR角色就返回hello页面
     * 由于在{@link com.wucf.config.WebSecurityConfig#userDetailsService}中只定义了ADMIN和USER角色
     * 因此此链接必定返回403错误码
     *
     * @return
     */
    @GetMapping("/testHR")
    public String testHR(){
        return "role:hr";
    }
}
