package com.example.plugin.api;

/**
 * 插件接口定义
 */
public interface Plugin {
    
    /**
     * 获取插件名称
     * @return 插件名称
     */
    String getName();
    
    /**
     * 获取插件版本
     * @return 插件版本
     */
    String getVersion();
    
    /**
     * 获取插件描述
     * @return 插件描述
     */
    String getDescription();
    
    /**
     * 插件启动
     */
    void start();
    
    /**
     * 插件停止
     */
    void stop();
    
    /**
     * 执行插件功能
     * @param input 输入参数
     * @return 执行结果
     */
    Object execute(Object input);
}
