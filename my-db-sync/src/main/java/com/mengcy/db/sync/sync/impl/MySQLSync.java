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

import com.mengcy.db.sync.task.entity.JobInfo;
import com.mengcy.db.sync.sync.DBSync;
import com.mengcy.db.sync.task.plugin.IFieldPlugin;
import com.mengcy.db.sync.utils.StringUtils;
import com.mengcy.db.sync.utils.Tool;
import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ValueRange;
import java.util.Map;

/**
 * @author mengcy
 * @version 1.0.0
 * @date 2018/11/11 10:21
 * @description MySQL数据库同步实现
 */
public class MySQLSync extends AbstractDBSync implements DBSync {

    private Logger logger = Logger.getLogger(MySQLSync.class);

    @Override
    public String assembleSQL(String srcSql, Connection conn, JobInfo jobInfo, Map<String, IFieldPlugin> plugins) throws SQLException {

        String uniqueName = Tool.generateString(6) + "_" + jobInfo.getName();

        String destTableFields = StringUtils.formatSql(jobInfo.getDestTableFields());
        String[] fields = destTableFields.split(",");
        fields = this.trimArrayItem(fields);

        String destUpdateFields = StringUtils.formatSql(jobInfo.getDestTableUpdate());
        String[] updateFields = destUpdateFields.split(",");
        updateFields = this.trimArrayItem(updateFields);

        String destTable = jobInfo.getDestTable();
        String destTableKey = jobInfo.getDestTableKey();

        // 查询源数据
        PreparedStatement pst = conn.prepareStatement(srcSql);
        ResultSet rs = pst.executeQuery();
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ")
                .append(jobInfo.getDestTable())
                .append(" (")
                .append(jobInfo.getDestTableFields())
                .append(") values ");
        long count = 0;
        while (rs.next()) {

            sql.append("(");
            for (int index = 0; index < fields.length; index++) {
                String field = fields[index];
                String value = rs.getString(field);
                value = handleField(field, value, plugins);
                sql.append("'").append(value).append(index == (fields.length - 1) ? "'" : "',");
            }
            sql.append("),");
            count++;
        }
        if (rs != null) {
            rs.close();
        }
        if (pst != null) {
            pst.close();
        }
        if (count > 0) {
            sql = sql.deleteCharAt(sql.length() - 1);
            if ((!jobInfo.getDestTableUpdate().equals("")) && (!jobInfo.getDestTableKey().equals(""))) {
                sql.append(" on duplicate key update ");
                for (int index = 0; index < updateFields.length; index++) {
                    sql.append(updateFields[index]).append("= values(").append(updateFields[index]).append(index == (updateFields.length - 1) ? ")" : "),");
                }
                return new StringBuffer("alter table ").append(destTable).append(" add constraint ").append(uniqueName).append(" unique (").append(destTableKey).append(");").append(sql.toString())
                        .append(";alter table ").append(destTable).append(" drop index ").append(uniqueName).toString();
            }
            logger.debug(sql.toString());
            return sql.toString();
        }
        return null;
    }

    @Override
    public void executeSQL(String sql, Connection conn) throws SQLException {

        PreparedStatement pst = conn.prepareStatement("");
        String[] sqlList = sql.split(";");
        for (int index = 0; index < sqlList.length; index++) {
            pst.addBatch(sqlList[index]);
        }
        pst.executeBatch();
        conn.commit();
        pst.close();
    }
}
