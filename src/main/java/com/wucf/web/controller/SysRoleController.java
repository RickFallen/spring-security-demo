package com.wucf.web.controller;


import com.wucf.core.common.UserConstants;
import com.wucf.core.controller.BaseController;
import com.wucf.core.page.TableDataInfo;
import com.wucf.system.domain.ResponseEntity;
import com.wucf.system.domain.SysRole;
import com.wucf.system.domain.SysUser;
import com.wucf.system.domain.SysUserRole;
import com.wucf.system.service.ISysRoleService;
import com.wucf.system.service.ISysUserService;
import com.wucf.system.service.TokenService;
import com.wucf.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息
 */
@Controller
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;
    
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping()
    public String role(ModelMap modelMap) {
        modelMap.put("Token", tokenService.getToken());
        return "system/role/role";
    }

    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysRole role) {
        startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    @ResponseBody
    public ResponseBody export(SysRole role) {
        //TODO 导出
        return null;
    }

    /**
     * 新增角色
     */
    @GetMapping("/add")
    public String add(ModelMap modelMap) {
        modelMap.put("Token", tokenService.getToken());
        return "system/role/add";
    }

    /**
     * 新增保存角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity addSave(@Validated SysRole role) {
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(SecurityUtils.getUsername());
        //TODO 清缓存
        return toAjax(roleService.insertRole(role));
    }

    /**
     * 修改角色
     */
    @GetMapping("/edit/{roleId}")
    public String edit(@PathVariable("roleId") Long roleId, ModelMap mmap) {
        mmap.put("role", roleService.selectRoleById(roleId));
        return "system/role/edit";
    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity editSave(@Validated SysRole role) {
        roleService.checkRoleAllowed(role);
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(SecurityUtils.getUsername());
        //AuthorizationUtils.clearAllCachedAuthorizationInfo();
        return toAjax(roleService.updateRole(role));
    }

    /**
     * 角色分配数据权限
     */
    @GetMapping("/authDataScope/{roleId}")
    public String authDataScope(@PathVariable("roleId") Long roleId, ModelMap mmap) {
        mmap.put("role", roleService.selectRoleById(roleId));
        mmap.put("Token", tokenService.getToken());
        return "system/role/dataScope";
    }

    /**
     * 保存角色分配数据权限
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PostMapping("/authDataScope")
    @ResponseBody
    public ResponseEntity authDataScopeSave(SysRole role) {
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(SecurityUtils.getUsername());
        if (roleService.authDataScope(role) > 0) {
            //ShiroUtils.setSysUser(userService.selectUserById(ShiroUtils.getSysUser().getUserId()));
            return success();
        }
        return error();
    }

    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity remove(String ids) {
        return toAjax(roleService.deleteRoleByIds(ids));
    }

    /**
     * 校验角色名称
     */
    @PostMapping("/checkRoleNameUnique")
    @ResponseBody
    public String checkRoleNameUnique(SysRole role) {
        return roleService.checkRoleNameUnique(role);
    }

    /**
     * 校验角色权限
     */
    @PostMapping("/checkRoleKeyUnique")
    @ResponseBody
    public String checkRoleKeyUnique(SysRole role) {
        return roleService.checkRoleKeyUnique(role);
    }

    /**
     * 角色状态修改
     */

    @PostMapping("/changeStatus")
    @ResponseBody
    public ResponseEntity changeStatus(SysRole role) {
        roleService.checkRoleAllowed(role);
        return toAjax(roleService.changeStatus(role));
    }

    /**
     * 分配用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @GetMapping("/authUser/{roleId}")
    public String authUser(@PathVariable("roleId") Long roleId, ModelMap mmap) {
        mmap.put("role", roleService.selectRoleById(roleId));
        mmap.put("Token", tokenService.getToken());
        return  "system/role/authUser";
    }

    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @PostMapping("/authUser/allocatedList")
    @ResponseBody
    public TableDataInfo allocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return getDataTable(list);
    }

    /**
     * 取消授权
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @PostMapping("/authUser/cancel")
    @ResponseBody
    public ResponseEntity cancelAuthUser(SysUserRole userRole) {
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PostMapping("/authUser/cancelAll")
    @ResponseBody
    public ResponseEntity cancelAuthUserAll(Long roleId, String userIds) {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 选择用户
     */
    @GetMapping("/authUser/selectUser/{roleId}")
    public String selectUser(@PathVariable("roleId") Long roleId, ModelMap mmap) {
        mmap.put("role", roleService.selectRoleById(roleId));
        mmap.put("Token", tokenService.getToken());
        return "system/role/selectUser";
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @PostMapping("/authUser/unallocatedList")
    @ResponseBody
    public TableDataInfo unallocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return getDataTable(list);
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PostMapping("/authUser/selectAll")
    @ResponseBody
    public ResponseEntity selectAuthUserAll(Long roleId, String userIds) {
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }
}