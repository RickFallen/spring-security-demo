package com.wucf.web.controller.system;

import com.wucf.core.common.UserConstants;
import com.wucf.core.controller.BaseController;
import com.wucf.core.page.TableDataInfo;
import com.wucf.system.domain.ResponseEntity;
import com.wucf.system.domain.SysConfig;
import com.wucf.system.service.system.ISysConfigService;
import com.wucf.utils.ExcelUtil;
import com.wucf.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参数配置 信息操作处理
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {
    @Autowired
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config) {
        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }
    
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @GetMapping("/export")
    public ResponseEntity export(SysConfig config) {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        return util.exportExcel(list, "参数数据");
    }

    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    public ResponseEntity getInfo(@PathVariable Long configId) {
        return ResponseEntity.success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public ResponseEntity getConfigKey(@PathVariable String configKey) {
        return ResponseEntity.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @PostMapping
    public ResponseEntity add(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return ResponseEntity.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(SecurityUtils.getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @PutMapping
    public ResponseEntity edit(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return ResponseEntity.error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @DeleteMapping("/{configIds}")
    public ResponseEntity remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return success();
    }

    /**
     * 刷新参数缓存
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @DeleteMapping("/refreshCache")
    public ResponseEntity refreshCache() {
        configService.resetConfigCache();
        return ResponseEntity.success();
    }
}
