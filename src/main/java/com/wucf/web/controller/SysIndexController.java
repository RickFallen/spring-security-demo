package com.wucf.web.controller;

import com.wucf.core.controller.BaseController;
import com.wucf.core.model.LoginUser;
import com.wucf.system.domain.SysMenu;
import com.wucf.system.service.ISysMenuService;
import com.wucf.system.service.TokenService;
import com.wucf.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 首页 业务处理
 */
@Controller
public class SysIndexController extends BaseController {
    @Autowired
    private ISysMenuService menuService;
    @Autowired
    private TokenService tokenService;

    // 系统首页
    @GetMapping("/index")
    public String index(ModelMap mmap, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getLoginUser();
        // 根据用户id取出菜单
        List<SysMenu> menus = menuService.selectMenusByUser(loginUser.getUser());
        mmap.put("menus", menus);
        mmap.put("user", loginUser.getUser());
        mmap.put("Token", tokenService.getToken(request));
        // 取身份信息
        return "index";
    }

    // 系统介绍
    @GetMapping("/system/main")
    public String main(ModelMap mmap) {
        mmap.put("version", "1.0");
        return "main";
    }
}
