package com.mengcy.db.sync.task.plugin.impl;

import com.mengcy.db.sync.task.plugin.IFieldPlugin;

/**
 * @author mengcy
 * @date 2018/11/19
 */
public class UrlPlugin implements IFieldPlugin {

    String src;
    String dest;

    @Override
    public String handle(String fieldValue) {
        if(src != null && dest != null){
            return fieldValue.replaceAll(src, dest);
        }
        return fieldValue;
    }
}
