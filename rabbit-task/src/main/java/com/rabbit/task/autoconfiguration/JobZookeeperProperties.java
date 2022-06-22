package com.rabbit.task.autoconfiguration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.sql.DataSourceDefinition;

/**
 * @author ym.y
 * @description
 * @date 11:27 2022/3/29
 */
@Data
@ConfigurationProperties(prefix = "elastic.job.zk")
public class JobZookeeperProperties {
    private String nameSpace;
    private String serverLists;
    private int maxRetries = 3;
    private int connectionTimeoutMilliseconds = 15000;
    private int sessionTimeoutMilliseconds = 60000;
    private int baseSleepTimeMilliseconds = 1000;
    private int maxSleepTimeMilliseconds = 3000;
    private String digest;
}
