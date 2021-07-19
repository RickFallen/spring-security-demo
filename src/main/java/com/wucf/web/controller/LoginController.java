package com.wucf.web.controller;

import com.wucf.core.domain.ResponseEntity;
import com.wucf.core.exception.BaseException;
import com.wucf.web.dto.LoginBody;
import com.wucf.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 登录方法
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginBody loginBody) {
        ResponseEntity response = ResponseEntity.success();
        Authentication authentication;
        try {
            /**
             *   {@link WebSecurityConfig#userDetailsService}
             *   该方法会去调用UserDetailsService.loadUserByUsername
             */
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginBody.getUsername(), loginBody.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BaseException("用戶名密码不匹配");
        } catch (Exception e) {
            throw new BaseException(e);
        }

        UserDetails loginUserDetails = (UserDetails) authentication.getPrincipal();
        // 生成token
        String token = tokenService.createToken(loginUserDetails);
        response.put("Token", token);
        return response;
    }
}
