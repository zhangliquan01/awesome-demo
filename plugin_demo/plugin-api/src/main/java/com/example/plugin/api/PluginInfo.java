package com.example.plugin.api;

/**
 * 插件信息
 */
public class PluginInfo {
    private String name;
    private String version;
    private String description;
    private String className;
    private String jarPath;
    private boolean enabled;
    
    public PluginInfo() {}
    
    public PluginInfo(String name, String version, String description, String className, String jarPath) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.className = className;
        this.jarPath = jarPath;
        this.enabled = false;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getJarPath() {
        return jarPath;
    }
    
    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "PluginInfo{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", className='" + className + '\'' +
                ", jarPath='" + jarPath + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
