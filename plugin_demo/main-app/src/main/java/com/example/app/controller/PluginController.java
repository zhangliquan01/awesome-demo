package com.example.app.controller;

import com.example.app.service.PluginManager;
import com.example.plugin.api.PluginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件管理REST API控制器
 */
@RestController
@RequestMapping("/api/plugins")
@CrossOrigin(origins = "*")
public class PluginController {
    
    @Autowired
    private PluginManager pluginManager;
    
    /**
     * 获取所有插件列表
     */
    @GetMapping
    public ResponseEntity<List<PluginInfo>> getAllPlugins() {
        List<PluginInfo> plugins = pluginManager.getAllPlugins();
        return ResponseEntity.ok(plugins);
    }
    
    /**
     * 获取指定插件信息
     */
    @GetMapping("/{pluginName}")
    public ResponseEntity<PluginInfo> getPlugin(@PathVariable String pluginName) {
        PluginInfo plugin = pluginManager.getPluginInfo(pluginName);
        if (plugin != null) {
            return ResponseEntity.ok(plugin);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 执行插件
     */
    @PostMapping("/{pluginName}/execute")
    public ResponseEntity<Map<String, Object>> executePlugin(
            @PathVariable String pluginName,
            @RequestBody(required = false) Object input) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Object result = pluginManager.executePlugin(pluginName, input);
            response.put("success", true);
            response.put("result", result);
            response.put("message", "插件执行成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "插件执行失败");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 启用插件
     */
    @PostMapping("/{pluginName}/enable")
    public ResponseEntity<Map<String, Object>> enablePlugin(@PathVariable String pluginName) {
        Map<String, Object> response = new HashMap<>();
        boolean success = pluginManager.enablePlugin(pluginName);
        
        response.put("success", success);
        response.put("message", success ? "插件启用成功" : "插件启用失败");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 禁用插件
     */
    @PostMapping("/{pluginName}/disable")
    public ResponseEntity<Map<String, Object>> disablePlugin(@PathVariable String pluginName) {
        Map<String, Object> response = new HashMap<>();
        boolean success = pluginManager.disablePlugin(pluginName);
        
        response.put("success", success);
        response.put("message", success ? "插件禁用成功" : "插件禁用失败");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 上传并安装插件
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPlugin(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!file.getOriginalFilename().endsWith(".jar")) {
            response.put("success", false);
            response.put("message", "只支持JAR文件格式");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 保存文件到plugins目录
            String fileName = file.getOriginalFilename();
            Path pluginsDir = Paths.get("plugins");
            if (!Files.exists(pluginsDir)) {
                Files.createDirectories(pluginsDir);
            }
            
            Path filePath = pluginsDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // 加载插件
            boolean success = pluginManager.loadPlugin(filePath.toFile());
            
            response.put("success", success);
            response.put("message", success ? "插件上传并加载成功" : "插件上传成功但加载失败");
            response.put("fileName", fileName);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 重新扫描插件目录
     */
    @PostMapping("/scan")
    public ResponseEntity<Map<String, Object>> scanPlugins() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            pluginManager.scanAndLoadPlugins();
            response.put("success", true);
            response.put("message", "插件扫描完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "插件扫描失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
