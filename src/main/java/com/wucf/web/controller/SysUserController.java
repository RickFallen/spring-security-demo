package com.wucf.web.controller;

import com.wucf.core.common.UserConstants;
import com.wucf.core.controller.BaseController;
import com.wucf.core.page.TableDataInfo;
import com.wucf.system.domain.ResponseEntity;
import com.wucf.system.domain.SysRole;
import com.wucf.system.domain.SysUser;
import com.wucf.system.service.ISysRoleService;
import com.wucf.system.service.ISysUserService;
import com.wucf.system.service.TokenService;
import com.wucf.utils.ExcelUtil;
import com.wucf.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息
 */
@Controller
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private TokenService tokenService;
    
    @PreAuthorize("@ss.hasPermi('system:user:view')")
    @GetMapping()
    public String user(ModelMap modelMap,HttpServletRequest request) {
        modelMap.put("Token", tokenService.getToken(request));
        return  "system/user/user";
    }
    
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }
    
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    @ResponseBody
    public ResponseEntity export(SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.exportExcel(list, "用户数据");
    }

    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    @ResponseBody
    public ResponseEntity importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return ResponseEntity.success(message);
    }
    
    @PreAuthorize("@ss.hasPermi('system:user:view')")
    @GetMapping("/importTemplate")
    @ResponseBody
    public ResponseEntity importTemplate() {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.importTemplateExcel("用户数据");
    }

    /**
     * 新增用户
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        mmap.put("roles", roleService.selectRoleAll().stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        // TODO mmap.put("posts", postService.selectPostAll());
        mmap.put("Token", tokenService.getToken());
        return "system/user/add";
    }

    /**
     * 新增保存用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:view')")
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity addSave(@Validated SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkLoginNameUnique(user.getLoginName()))) {
            return error("新增用户'" + user.getLoginName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return error("新增用户'" + user.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return error("新增用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setCreateBy(SecurityUtils.getUsername());
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable("userId") Long userId, ModelMap mmap,HttpServletRequest request) {
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        mmap.put("user", userService.selectUserById(userId));
        mmap.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        // TODO mmap.put("posts", postService.selectPostsByUserId(userId));
        mmap.put("Token", tokenService.getToken(request));
        return "system/user/edit";
    }

    /**
     * 修改保存用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity editSave(@Validated SysUser user) {
        userService.checkUserAllowed(user);
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return error("修改用户'" + user.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return error("修改用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUser(user));
    }

    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @GetMapping("/resetPwd/{userId}")
    public String resetPwd(@PathVariable("userId") Long userId, ModelMap mmap) {
        mmap.put("user", userService.selectUserById(userId));
        mmap.put("Token", tokenService.getToken());
        return "system/user/resetPwd";
    }

    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @PostMapping("/resetPwd")
    @ResponseBody
    public ResponseEntity resetPwdSave(SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        if (userService.resetUserPwd(user) > 0) {
            return success();
        }
        return error();
    }

    /**
     * 进入授权角色页
     */
    @GetMapping("/authRole/{userId}")
    public String authRole(@PathVariable("userId") Long userId, ModelMap mmap, HttpServletRequest request) {
        SysUser user = userService.selectUserById(userId);
        // 获取用户所属的角色列表
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        mmap.put("user", user);
        mmap.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        mmap.put("Token", tokenService.getToken(request));
        return "system/user/authRole";
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PostMapping("/authRole/insertAuthRole")
    @ResponseBody
    public ResponseEntity insertAuthRole(Long userId, Long[] roleIds) {
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity remove(String ids) {
        return toAjax(userService.deleteUserByIds(ids));
    }

    /**
     * 校验用户名
     */
    @PostMapping("/checkLoginNameUnique")
    @ResponseBody
    public String checkLoginNameUnique(SysUser user) {
        return userService.checkLoginNameUnique(user.getLoginName());
    }

    /**
     * 校验手机号码
     */
    @PostMapping("/checkPhoneUnique")
    @ResponseBody
    public String checkPhoneUnique(SysUser user) {
        return userService.checkPhoneUnique(user);
    }

    /**
     * 校验email邮箱
     */
    @PostMapping("/checkEmailUnique")
    @ResponseBody
    public String checkEmailUnique(SysUser user) {
        return userService.checkEmailUnique(user);
    }

    /**
     * 用户状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PostMapping("/changeStatus")
    @ResponseBody
    public ResponseEntity changeStatus(SysUser user) {
        userService.checkUserAllowed(user);
        return toAjax(userService.changeStatus(user));
    }
}