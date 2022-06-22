package com.rabbit.core.producer.config;

import com.rabbit.task.annotation.EnableElasticJob;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ym.y
 * @description 自动装配类
 * @date 0:22 2022/3/26
 */
@EnableElasticJob
@Configuration
@ComponentScan({"com.rabbit.core.producer"})
public class RabbitProducerAutoConfiguration {

}
