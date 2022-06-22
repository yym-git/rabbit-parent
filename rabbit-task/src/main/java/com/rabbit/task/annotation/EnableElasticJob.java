package com.rabbit.task.annotation;

import com.rabbit.task.autoconfiguration.JobParseAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author ym.y
 * @description
 * @date 12:03 2022/3/29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(JobParseAutoConfiguration.class)
public @interface EnableElasticJob {
}
