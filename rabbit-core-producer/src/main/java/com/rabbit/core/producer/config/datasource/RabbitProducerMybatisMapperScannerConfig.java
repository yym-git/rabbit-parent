package com.rabbit.core.producer.config.datasource;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ym.y
 * @description 初始化mapperScanner
 * @date 9:48 2022/3/27
 */
@Configuration
@AutoConfigureAfter({RabbitProducerDataSourceConfiguration.class})
public class RabbitProducerMybatisMapperScannerConfig {
    @Bean("rabbitProducerMapperScannerConfig")
    public MapperScannerConfigurer rabbitProducerMapperScannerConfig() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("rabbitProducerSqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.rabbit.core.producer.mapper");
        return mapperScannerConfigurer;
    }
}
