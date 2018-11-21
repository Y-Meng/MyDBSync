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
package com.mengcy.db.sync.task;

import com.mengcy.db.sync.factory.DBSyncFactory;
import com.mengcy.db.sync.task.entity.DBInfo;
import com.mengcy.db.sync.task.entity.JobInfo;
import com.mengcy.db.sync.sync.DBSync;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mengcy
 * @date 2018/11/11 10:30
 * @description 同步数据库任务的具体实现
 * @version 1.0.0
 */
public class JobTask implements Job {

    private Logger logger = Logger.getLogger(JobTask.class);

    /**
     * 执行同步数据库任务
     *
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Connection inConn = null;
        Connection outConn = null;
        JobDataMap data = context.getJobDetail().getJobDataMap();
        DBInfo srcDb = (DBInfo) data.get("srcDb");
        DBInfo destDb = (DBInfo) data.get("destDb");
        JobInfo jobInfo = (JobInfo) data.get("jobInfo");
        String logTitle = (String) data.get("logTitle");

        this.logger.info(jobInfo.getName() + "开始: " + new Date());

        try {
            inConn = createConnection(srcDb);
            outConn = createConnection(destDb);
            if (inConn == null) {
                this.logger.info("请检查源数据连接!");
                return;
            } else if (outConn == null) {
                this.logger.info("请检查目标数据连接!");
                return;
            }

            // 系统变量
            Map<String, String> params = new HashMap<>();

            DBSync dbHelper = DBSyncFactory.create(destDb.getDbtype());
            long start = System.currentTimeMillis();
            String srcSql = dbHelper.completeSrcSql(jobInfo.getSrcSql(), params);
            String targetSql = dbHelper.assembleSQL(srcSql, inConn, jobInfo, null);
            this.logger.info("组装SQL耗时: " + (System.currentTimeMillis() - start) + "ms");
            if (targetSql != null) {
                this.logger.info(targetSql);
                long eStart = System.currentTimeMillis();
                dbHelper.executeSQL(targetSql, outConn);
                this.logger.info("执行SQL耗时: " + (System.currentTimeMillis() - eStart) + "ms");
            }
        } catch (SQLException e) {
            this.logger.error(logTitle + e.getMessage());
            this.logger.error(logTitle + " SQL执行出错，请检查是否存在语法错误");
        } finally {
            this.logger.info("关闭源数据库连接");
            destroyConnection(inConn);
            this.logger.info("关闭目标数据库连接");
            destroyConnection(outConn);
        }
    }

    /**
     * 创建数据库连接
     * @param db
     * @return
     */
    private Connection createConnection(DBInfo db) {
        try {
            Class.forName(db.getDriver());
            Connection conn = DriverManager.getConnection(db.getUrl(), db.getUsername(), db.getPassword());
            conn.setAutoCommit(false);
            return conn;
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 关闭并销毁数据库连接
     * @param conn
     */
    private void destroyConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
                this.logger.debug("数据库连接关闭成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.logger.error("数据库连接关闭失败");
        }
    }
}
