# 从0开始搭建一个 Spring Security 权限框架，供学习交流使用
**由于Spring Security OAuth项目已废弃，OAuth 模块也都统一迁移到 Spring Security 项目中。**

**因此本教程要求 Spring Security 版本 > 5.2.x**

>The Spring Security OAuth project is deprecated. The latest OAuth 2.0 support is provided by Spring Security. See the OAuth 2.0 Migration Guide for further details.

本教程计划采用发布版本的形式，从最简单的登录验证、鉴权授权到搭建 OAuth2 微服务，由易到难慢慢学习 Spring Security

## 1.0 版本
1.0 版本直接取自 Spring Security 官方文档[securing web guides](https://spring.io/guides/gs/securing-web/)

此版本包含内容有

+ 首页、登录页面、登陆后跳转页共三个简单页面
+ 开启 Spring Security 的最小配置
+ 没有 application.yml 默认就是启动8080端口

## 1.1 版本

此版本包含内容有

+ 添加 application.yml 

+ 在 WebSecurityConfig 类添加常用鉴权方法注释

+ 使用 API 控制某个链接的访问权限

+ 启用 MethodSecurity 来使用 Spring EL 表达式进行更细粒度的方法鉴权

  



