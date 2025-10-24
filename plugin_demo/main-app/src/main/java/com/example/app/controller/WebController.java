package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web页面控制器
 */
@Controller
public class WebController {
    
    /**
     * 插件管理页面
     */
    @GetMapping({"/", "/plugins"})
    public String pluginsPage() {
        return "plugins";
    }
}
