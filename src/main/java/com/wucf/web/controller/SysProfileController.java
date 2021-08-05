package com.wucf.web.controller;

import com.wucf.core.common.UserConstants;
import com.wucf.core.controller.BaseController;
import com.wucf.core.model.LoginUser;
import com.wucf.system.domain.ResponseEntity;
import com.wucf.system.domain.SysUser;
import com.wucf.system.service.ISysUserService;
import com.wucf.system.service.TokenService;
import com.wucf.utils.DateUtils;
import com.wucf.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人信息 业务处理
 */
@Controller
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SysProfileController.class);
    @Autowired
    private ISysUserService userService;
    @Autowired
    private TokenService tokenService;

    /**
     * 个人信息
     */
    @GetMapping()
    public String profile(ModelMap mmap) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getLoginUser();
        mmap.put("user", ((LoginUser) SecurityUtils.getLoginUser()).getUser());
        mmap.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUser().getUserId()));
        mmap.put("postGroup", userService.selectUserPostGroup(loginUser.getUser().getUserId()));
        mmap.put("Token", tokenService.getToken());
        return "system/user/profile/profile";
    }

    @GetMapping("/checkPassword")
    @ResponseBody
    public boolean checkPassword(String password) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        return SecurityUtils.matchesPassword(password, user.getPassword());
    }

    @GetMapping("/resetPwd")
    public String resetPwd(ModelMap mmap) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        mmap.put("user", userService.selectUserById(user.getUserId()));
        mmap.put("Token", tokenService.getToken());
        return "system/user/profile/resetPwd";
    }

    @PostMapping("/resetPwd")
    @ResponseBody
    public ResponseEntity resetPwd(String oldPassword, String newPassword) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (!SecurityUtils.matchesPassword(oldPassword,user.getPassword())) {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, user.getPassword())) {
            return error("新密码不能与旧密码相同");
        }
        user.setPassword(SecurityUtils.encryptPassword(newPassword));
        user.setPwdUpdateDate(DateUtils.getNowDate());
        if (userService.resetUserPwd(user) > 0) {
            //TODO 刷新缓存 ShiroUtils.setSysUser(userService.selectUserById(user.getUserId()));
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 修改头像
     */
    @GetMapping("/avatar")
    public String avatar(ModelMap mmap) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        mmap.put("user", userService.selectUserById(user.getUserId()));
        return "system/user/profile/avatar";
    }

    /**
     * 修改用户
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity update(SysUser user) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        currentUser.setUserName(user.getUserName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(currentUser))) {
            return error("修改用户'" + currentUser.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(currentUser))) {
            return error("修改用户'" + currentUser.getLoginName() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserInfo(currentUser) > 0) {
            //TODO 刷新缓存 ShiroUtils.setSysUser(userService.selectUserById(currentUser.getUserId()));
            return success();
        }
        return error();
    }

    /**
     * 保存头像
     */
    @PostMapping("/updateAvatar")
    @ResponseBody
    public ResponseEntity updateAvatar(@RequestParam("avatarfile") MultipartFile file) {
        return error("暂不支持修改头像");
    }
}
