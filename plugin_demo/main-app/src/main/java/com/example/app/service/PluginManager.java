package com.example.app.service;

import com.example.plugin.api.Plugin;
import com.example.plugin.api.PluginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

/**
 * 插件管理器
 */
@Service
public class PluginManager {
    
    private static final Logger logger = LoggerFactory.getLogger(PluginManager.class);
    
    private final Map<String, Plugin> loadedPlugins = new ConcurrentHashMap<>();
    private final Map<String, PluginInfo> pluginInfos = new ConcurrentHashMap<>();
    private final Map<String, URLClassLoader> classLoaders = new ConcurrentHashMap<>();
    
    private static final String PLUGINS_DIR = "plugins";
    
    @PostConstruct
    public void init() {
        // 创建插件目录
        File pluginsDir = new File(PLUGINS_DIR);
        if (!pluginsDir.exists()) {
            pluginsDir.mkdirs();
            logger.info("创建插件目录: {}", pluginsDir.getAbsolutePath());
        }
        
        // 扫描并加载插件
        scanAndLoadPlugins();
    }
    
    /**
     * 扫描并加载插件
     */
    public void scanAndLoadPlugins() {
        File pluginsDir = new File(PLUGINS_DIR);
        File[] jarFiles = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));
        
        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                try {
                    loadPlugin(jarFile);
                } catch (Exception e) {
                    logger.error("加载插件失败: {}", jarFile.getName(), e);
                }
            }
        }
    }
    
    /**
     * 加载插件
     */
    public boolean loadPlugin(File jarFile) {
        try {
            URL jarUrl = jarFile.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());
            
            // 从JAR文件中查找插件类
            try (JarFile jar = new JarFile(jarFile)) {
                String pluginClassName = findPluginClass(jar);
                if (pluginClassName == null) {
                    logger.warn("在JAR文件中未找到插件类: {}", jarFile.getName());
                    return false;
                }
                
                Class<?> pluginClass = classLoader.loadClass(pluginClassName);
                Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
                
                String pluginName = plugin.getName();
                
                // 如果插件已存在，先卸载
                if (loadedPlugins.containsKey(pluginName)) {
                    unloadPlugin(pluginName);
                }
                
                // 加载新插件
                loadedPlugins.put(pluginName, plugin);
                classLoaders.put(pluginName, classLoader);
                
                PluginInfo info = new PluginInfo(
                    plugin.getName(),
                    plugin.getVersion(),
                    plugin.getDescription(),
                    pluginClassName,
                    jarFile.getAbsolutePath()
                );
                info.setEnabled(true);
                pluginInfos.put(pluginName, info);
                
                plugin.start();
                logger.info("插件加载成功: {} v{}", plugin.getName(), plugin.getVersion());
                return true;
            }
        } catch (Exception e) {
            logger.error("加载插件失败: {}", jarFile.getName(), e);
            return false;
        }
    }
    
    /**
     * 卸载插件
     */
    public boolean unloadPlugin(String pluginName) {
        try {
            Plugin plugin = loadedPlugins.get(pluginName);
            if (plugin != null) {
                plugin.stop();
                loadedPlugins.remove(pluginName);
                
                PluginInfo info = pluginInfos.get(pluginName);
                if (info != null) {
                    info.setEnabled(false);
                }
                
                URLClassLoader classLoader = classLoaders.remove(pluginName);
                if (classLoader != null) {
                    classLoader.close();
                }
                
                logger.info("插件卸载成功: {}", pluginName);
                return true;
            }
        } catch (Exception e) {
            logger.error("卸载插件失败: {}", pluginName, e);
        }
        return false;
    }
    
    /**
     * 执行插件
     */
    public Object executePlugin(String pluginName, Object input) {
        Plugin plugin = loadedPlugins.get(pluginName);
        if (plugin != null) {
            try {
                return plugin.execute(input);
            } catch (Exception e) {
                logger.error("执行插件失败: {}", pluginName, e);
                throw new RuntimeException("插件执行失败: " + e.getMessage());
            }
        }
        throw new RuntimeException("插件未找到: " + pluginName);
    }
    
    /**
     * 获取所有插件信息
     */
    public List<PluginInfo> getAllPlugins() {
        return new ArrayList<>(pluginInfos.values());
    }
    
    /**
     * 获取插件信息
     */
    public PluginInfo getPluginInfo(String pluginName) {
        return pluginInfos.get(pluginName);
    }
    
    /**
     * 启用插件
     */
    public boolean enablePlugin(String pluginName) {
        PluginInfo info = pluginInfos.get(pluginName);
        if (info != null && !info.isEnabled()) {
            File jarFile = new File(info.getJarPath());
            if (jarFile.exists()) {
                return loadPlugin(jarFile);
            }
        }
        return false;
    }
    
    /**
     * 禁用插件
     */
    public boolean disablePlugin(String pluginName) {
        return unloadPlugin(pluginName);
    }
    
    /**
     * 从JAR文件中查找插件类
     */
    private String findPluginClass(JarFile jarFile) {
        return jarFile.stream()
            .filter(entry -> entry.getName().endsWith(".class"))
            .map(entry -> entry.getName().replace('/', '.').replace(".class", ""))
            .filter(className -> {
                try {
                    // 这里简化处理，实际项目中可以通过注解或配置文件来标识插件类
                    return className.contains("Plugin") && !className.contains("$");
                } catch (Exception e) {
                    return false;
                }
            })
            .findFirst()
            .orElse(null);
    }
}
