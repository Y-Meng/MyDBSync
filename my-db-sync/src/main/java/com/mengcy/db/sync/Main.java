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
package com.mengcy.db.sync;

import com.mengcy.db.sync.build.DBSyncBuilder;
import com.mengcy.db.sync.utils.DateUtils;
import org.apache.log4j.Logger;
import java.util.Date;

/**
 * @author mengcy
 * @date 2018/11/11 10:07
 * @description 程序入口
 * @version 1.0.0
 */
public class Main {

    private static Logger logger = Logger.getLogger(DBSyncBuilder.class);

    public static void main(String[] args) {
        logger.info("初始化数据开始===>>>" + DateUtils.parseDateToString(new Date(), DateUtils.DATE_TIME_FORMAT));
        DBSyncBuilder.builder().init().start();
        logger.info("初始化数据结束===>>>" + DateUtils.parseDateToString(new Date(), DateUtils.DATE_TIME_FORMAT));
    }
}
