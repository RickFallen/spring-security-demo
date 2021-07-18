package com.wucf.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()//开启请求拦截
            .antMatchers("/testHR").hasAnyRole("HR") //以API的形式判断某个角色是否可访问某个链接
            .antMatchers("/", "/home").permitAll()// 凡是/和/home路径的请求统统放行
            .anyRequest().authenticated()//其他所有URL都需要验证鉴权
            .and()
            .formLogin()//以form表单的形式登录
            .loginPage("/login").permitAll()//指定登录接口，且登录接口不需要验证鉴权
            .and()
            .logout().permitAll();//登出接口不需要鉴权 默认接口为/logout
    }

    @Bean
    @Override
    //使用内存存储用户角色和账号密码，以后可以自定义存储到redis或者database
    public UserDetailsService userDetailsService() {
        UserDetails user =
                //被标注废弃是因为在此仅作example使用，不能在生产环境中使用
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password("password")
                        .roles("USER","ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
}
