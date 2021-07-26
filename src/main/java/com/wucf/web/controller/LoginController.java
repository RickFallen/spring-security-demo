package com.wucf.web.controller;

import com.wucf.system.domain.ResponseEntity;

import com.wucf.core.model.LoginBody;
import com.wucf.system.service.SysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private SysLoginService loginService;

    /**
     * 登录方法
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginBody loginBody) {
        ResponseEntity response = ResponseEntity.success();
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword());
        response.put("Token", token);
        return response;
    }
}
