package com.wucf.web.controller;

import com.wucf.core.controller.BaseController;
import com.wucf.core.model.LoginBody;
import com.wucf.system.domain.ResponseEntity;
import com.wucf.system.service.SysLoginService;
import com.wucf.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录验证
 */
@Controller
public class SysLoginController extends BaseController {
    @Autowired
    private SysLoginService loginService;

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, ModelMap mmap) {
        // 如果是Ajax请求，返回Json字符串。
        if (ServletUtils.isAjaxRequest(request)) {
            return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"未登录或登录超时。请重新登录\"}");
        }
        mmap.put("captchaEnabled", false);
        return "login";
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity ajaxLogin(LoginBody loginBody) {
        ResponseEntity response = ResponseEntity.success();
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword());
        response.put("Token", token);
        return response;
    }

    @GetMapping("/unauth")
    public String unauth() {
        return "error/unauth";
    }
}
