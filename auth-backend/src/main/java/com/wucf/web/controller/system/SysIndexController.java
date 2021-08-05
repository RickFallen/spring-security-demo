package com.wucf.web.controller.system;

import com.wucf.utils.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 */
@RestController
public class SysIndexController {
    /**
     * 系统基础配置
     */
    @Value("${system.version}")
    private String version;
    @Value("${system.name}")
    private String sysName;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index() {
        return Strings.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", sysName, version);
    }
}
