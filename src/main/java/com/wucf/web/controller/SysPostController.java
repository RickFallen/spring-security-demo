package com.wucf.web.controller;


import com.wucf.core.common.UserConstants;
import com.wucf.core.controller.BaseController;
import com.wucf.core.page.TableDataInfo;
import com.wucf.system.domain.ResponseEntity;
import com.wucf.system.domain.SysPost;
import com.wucf.system.service.ISysPostService;
import com.wucf.system.service.TokenService;
import com.wucf.utils.ExcelUtil;
import com.wucf.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位信息操作处理
 */
@Controller
@RequestMapping("/system/post")
public class SysPostController extends BaseController {

    @Autowired
    private ISysPostService postService;
    @Autowired
    private TokenService tokenService;
    
    @PreAuthorize("@ss.hasPermi('system:post:view')")
    @GetMapping()
    public String operlog(ModelMap modelMap) {
        modelMap.put("Token", tokenService.getToken());
        return "system/post/post";
    }
    
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysPost post) {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @PostMapping("/export")
    @ResponseBody
    public ResponseEntity export(SysPost post) {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        return util.exportExcel(list, "岗位数据");
    }

    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity remove(String ids) {
        try {
            return toAjax(postService.deletePostByIds(ids));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 新增岗位
     */
    @GetMapping("/add")
    public String add(ModelMap modelMap) {
        modelMap.put("Token", tokenService.getToken());
        return "system/post/add";
    }

    /**
     * 新增保存岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity addSave(@Validated SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(SecurityUtils.getUsername());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @GetMapping("/edit/{postId}")
    public String edit(@PathVariable("postId") Long postId, ModelMap mmap) {
        mmap.put("post", postService.selectPostById(postId));
        mmap.put("Token", tokenService.getToken());
        return "system/post/edit";
    }

    /**
     * 修改保存岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity editSave(@Validated SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(postService.updatePost(post));
    }

    /**
     * 校验岗位名称
     */
    @PostMapping("/checkPostNameUnique")
    @ResponseBody
    public String checkPostNameUnique(SysPost post) {
        return postService.checkPostNameUnique(post);
    }

    /**
     * 校验岗位编码
     */
    @PostMapping("/checkPostCodeUnique")
    @ResponseBody
    public String checkPostCodeUnique(SysPost post) {
        return postService.checkPostCodeUnique(post);
    }
}
