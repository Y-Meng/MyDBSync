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
package com.mengcy.db.sync.sync;

import com.mengcy.db.sync.task.entity.JobInfo;
import com.mengcy.db.sync.task.plugin.IPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author mengcy
 * @date 2018/11/11 10:19
 * @description 数据库同步接口
 * @version 1.0.0
 */
public interface DBSync {
    /**
     * 组装SQL
     * @param paramString:同步参数
     * @param paramConnection：数据库连接
     * @param paramJobInfo：同步任务
     * @return
     * @throws SQLException
     */
    String assembleSQL(String paramString, Connection paramConnection, JobInfo paramJobInfo) throws SQLException;
    /**
     * 执行SQL
     * @param sql：要执行的SQL语句
     * @param conn：数据库连接
     * @param plugins: 数据处理插件列表
     * @throws SQLException
     */
    void executeSQL(String sql, Connection conn, List<IPlugin> plugins) throws SQLException;
}
