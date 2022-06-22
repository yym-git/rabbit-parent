package com.rabbit.core.producer.config.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * @author ym.y
 * @description 通过DataSource来初始化broker-message表，即执行SQL脚本，创建表
 * @date 0:42 2022/3/27
 */
@Configuration
public class BrokerMessageTableConfiguration {
    private  final Logger LOGGER = LoggerFactory.getLogger(BrokerMessageTableConfiguration.class);
    @Autowired
    private DataSource rabbitProducerDataSource;
    @Value("classpath:rabbit-producer-message-schema.sql")
    private Resource schemaScript;

    @Bean
    public DataSourceInitializer initDataSourceInitializer(){
        //打印日志查看是否获取到了数据源
//        LOGGER.info("=============rabbitProducerDataSource=============:{}",rabbitProducerDataSource);
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(rabbitProducerDataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator(){
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaScript);
        return populator;
    }
}
