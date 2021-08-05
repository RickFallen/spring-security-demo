package com.wucf.web.controller.system;

import com.wucf.core.common.UserConstants;
import com.wucf.core.controller.BaseController;
import com.wucf.core.page.TableDataInfo;
import com.wucf.system.domain.ResponseEntity;
import com.wucf.system.domain.SysDictType;
import com.wucf.system.service.system.ISysDictTypeService;
import com.wucf.utils.ExcelUtil;
import com.wucf.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典信息
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController {
    @Autowired
    private ISysDictTypeService dictTypeService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDictType dictType) {
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return getDataTable(list);
    }
    
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @GetMapping("/export")
    public ResponseEntity export(SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        return util.exportExcel(list, "字典类型");
    }

    /**
     * 查询字典类型详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public ResponseEntity getInfo(@PathVariable Long dictId) {
        return ResponseEntity.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public ResponseEntity add(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict))) {
            return ResponseEntity.error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public ResponseEntity edit(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict))) {
            return ResponseEntity.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/{dictIds}")
    public ResponseEntity remove(@PathVariable Long[] dictIds) {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return success();
    }

    /**
     * 刷新字典缓存
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/refreshCache")
    public ResponseEntity refreshCache() {
        dictTypeService.resetDictCache();
        return ResponseEntity.success();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public ResponseEntity optionselect() {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return ResponseEntity.success(dictTypes);
    }
}
