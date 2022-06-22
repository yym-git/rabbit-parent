package com.rabbit.core.producer.config.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

/**
 * @author ym.y
 * @description 自动装配DataSource数据源
 * @date 0:26 2022/3/27
 */
@Configuration
@PropertySource({"classpath:rabbit-producer-message.properties"})
public class RabbitProducerDataSourceConfiguration {
    private Logger LOGGER = LoggerFactory.getLogger(RabbitProducerDataSourceConfiguration.class);
    @Value("${rabbait.producer.druid.type}")
    private Class<? extends DataSource> dataSourceType;

    @Primary
    @Bean(name = "rabbitProducerDataSource")
    @ConfigurationProperties(prefix = "rabbait.producer.druid.jdbc")
    public DataSource rabbitProducerDataSource() {
        DataSource dataSource = DataSourceBuilder.create().type(dataSourceType).build();
        LOGGER.info("===============rabbitProducerDataSource:{}============",dataSource);
        return dataSource;
    }

    public DataSourceProperties primaryDataSourceProperties(){
        return new DataSourceProperties();
    }

    public DataSource primaryDataSource(){
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }
}
