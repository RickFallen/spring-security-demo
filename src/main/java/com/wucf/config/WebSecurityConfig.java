package com.wucf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()//开启请求拦截
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
