package com.mengcy.db.sync.task.plugin;

/**
 * @author mengcy
 * @date 2018/11/19
 * 同步处理插件抽象接口
 */
public interface IFieldPlugin {

    String handle(String fieldValue);
}
