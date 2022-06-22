package com.rabbit.task.autoconfiguration;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.rabbit.task.parser.ElasticJobConfParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ym.y
 * @description
 * @date 11:14 2022/3/29
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(JobZookeeperProperties.class)
@ConditionalOnProperty(prefix = "elastic.job.zk", name = {"namespace", "serverLists"})
public class JobParseAutoConfiguration {
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(JobZookeeperProperties jobProperties) {
        ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(jobProperties.getServerLists(),jobProperties.getNameSpace());
        zkConfig.setBaseSleepTimeMilliseconds(jobProperties.getBaseSleepTimeMilliseconds());
        zkConfig.setSessionTimeoutMilliseconds(jobProperties.getSessionTimeoutMilliseconds());
        zkConfig.setConnectionTimeoutMilliseconds(jobProperties.getConnectionTimeoutMilliseconds());
        zkConfig.setMaxRetries(jobProperties.getMaxRetries());
        zkConfig.setMaxSleepTimeMilliseconds(jobProperties.getMaxSleepTimeMilliseconds());
        zkConfig.setDigest(jobProperties.getDigest());
        log.info("初始化job注册中心配置成功，zkAddress：{}，nameSpace：{}", zkConfig.getServerLists(), zkConfig.getNamespace());
        return new ZookeeperRegistryCenter(zkConfig);
    }

    @Bean
    public ElasticJobConfParser elasticJobConfParser(JobZookeeperProperties jobProperties, ZookeeperRegistryCenter registryCenter) {
        ElasticJobConfParser elasticJobConfParser = new ElasticJobConfParser(jobProperties, registryCenter);
        return elasticJobConfParser;
    }
}
