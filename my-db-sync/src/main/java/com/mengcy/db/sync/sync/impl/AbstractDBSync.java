/**
 * Copyright 2018-2118 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mengcy.db.sync.sync.impl;

import com.mengcy.db.sync.sync.DBSync;
import com.mengcy.db.sync.task.plugin.IFieldPlugin;

import java.util.Map;

/**
 * @author mengcy
 * @date 2018/11/11 10:20
 * @description 执行数据库同步的抽象类
 * @version 1.0.0
 */
public abstract class AbstractDBSync implements DBSync {

    @Override
    public String completeSrcSql(String srcSql, Map<String, String> params) {

        srcSql = srcSql.replaceAll("\r\n", "");
        for(Map.Entry<String, String> entry : params.entrySet()){
            srcSql = srcSql.replaceAll(entry.getKey(), entry.getValue());
        }
        return srcSql;
    }

    @Override
    public String handleField(String field, String value, Map<String, IFieldPlugin> plugins) {

        IFieldPlugin plugin = null;
        if(plugins != null) {
            plugin = plugins.get(field);
        }
        return plugin == null ? value : plugin.handle(value);
    }

    /**
     * 去除String数组每个元素中的空格
     * @param src 需要去除空格的数组
     * @return 去除空格后的数组
     */
    protected String[] trimArrayItem(String[] src){
        if(src == null || src.length == 0) return src;
        String[] dest = new String[src.length];
        for(int i = 0; i < src.length; i++){
            dest[i] = src[i].trim();
        }
        return dest;
    }
}
