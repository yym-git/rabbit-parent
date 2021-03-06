package com.rabbit.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.rabbit.task.annotation.ElasticJobConfig;
import com.rabbit.task.annotation.EnableElasticJob;
import com.rabbit.task.autoconfiguration.JobZookeeperProperties;
import com.rabbit.task.enums.ElasticJobTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author ym.y
 * @description
 * @date 13:56 2022/3/29
 */
@Slf4j
public class ElasticJobConfParser2 implements ApplicationListener<ApplicationReadyEvent> {
    private JobZookeeperProperties jobProperties;
    private ZookeeperRegistryCenter registryCenter;

    public ElasticJobConfParser2(JobZookeeperProperties jobProperties, ZookeeperRegistryCenter registryCenter) {
        this.jobProperties = jobProperties;
        this.registryCenter = registryCenter;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();
            //??????????????????EnableElasticJob????????????
            Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);
            Iterator<Object> iterator = beanMap.values().iterator();
            while (iterator.hasNext()) {
                Object configBean = iterator.next();
                Class<?> clazz = configBean.getClass();
                if (clazz.getName().indexOf("$") > 0) {
                    String className = clazz.getName();
                    clazz = Class.forName(className.substring(0, className.indexOf("$")));
                }
                //??????????????????????????????????????????????????????
                Class<?>[] clazzes = clazz.getInterfaces();
                String jobTypeName = clazzes[0].getSimpleName();
                //???????????????ElasticJobConfig
                ElasticJobConfig conf = clazz.getAnnotation(ElasticJobConfig.class);
                String jobClass = clazz.getName();
                String jobName = this.jobProperties.getNameSpace() + "." + conf.name();
                String cron = conf.cron();
                int shardingTotalCount = conf.shardingTotalCount();
                String shardingItemParameters = conf.shardingItemParameters();
                String jobParameter = conf.jobParameter();
                boolean failover = conf.failover();
                boolean misfire = conf.misfire();
                String description = conf.description();
                boolean overwrite = conf.overwrite();
                boolean streamingProcess = conf.streamingProcess();
                String scriptCommandLine = conf.scriptCommandLine();
                boolean monitorExecution = conf.monitorExecution();
                int monitorPort = conf.monitorPort();
                int maxTimeDiffSeconds = conf.maxTimeDiffSeconds();
                String jobShardingStrategyClass = conf.jobShardingStrategyClass();
                int reconcileIntervalMinutes = conf.reconcileIntervalMinutes();
                String eventTraceRdbDataSource = conf.eventTraceRdbDataSource();
                String listener = conf.listener();
                boolean disabled = conf.disabled();
                String distributedListener = conf.distributedListener();
                long startedTimeoutMilliseconds = conf.startedTimeoutMilliseconds();
                long completedTimeoutMilliseconds = conf.completedTimeoutMilliseconds();
                String jobExceptionHandler = conf.jobExceptionHandler();
                String executorServiceHandler = conf.executorServiceHandler();
                JobCoreConfiguration coreConfiguration = JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount)
                        .shardingItemParameters(shardingItemParameters)
                        .description(description)
                        .failover(failover)
                        .jobParameter(jobParameter)
                        .misfire(misfire)
                        .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
                        .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler)
                        .build();

                JobTypeConfiguration typeConfiguration = null;
                if (ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
                    typeConfiguration = new SimpleJobConfiguration(coreConfiguration, jobClass);
                }
                if (ElasticJobTypeEnum.DATAFLOW_JOB.getType().equals(jobTypeName)) {
                    typeConfiguration = new DataflowJobConfiguration(coreConfiguration, jobClass, streamingProcess);
                }
                if (ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
                    typeConfiguration = new ScriptJobConfiguration(coreConfiguration, scriptCommandLine);
                }
                LiteJobConfiguration jobConfiguration = LiteJobConfiguration.newBuilder(typeConfiguration)
                        .overwrite(overwrite)
                        .disabled(disabled)
                        .monitorExecution(monitorExecution)
                        .monitorPort(monitorPort)
                        .maxTimeDiffSeconds(maxTimeDiffSeconds)
                        .jobShardingStrategyClass(jobShardingStrategyClass)
                        .reconcileIntervalMinutes(reconcileIntervalMinutes)
                        .build();

                //????????????Spring???benDefinition
                BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
                factory.setInitMethodName("init");
                factory.setScope("prototype");
                //??????bean??????????????????,?????????????????????????????????????????????
                if (!ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
                    factory.addConstructorArgValue(configBean);
                }
                //??????????????????
                factory.addConstructorArgValue(this.registryCenter);
                //??????LiteConfiguration
                factory.addConstructorArgValue(jobConfiguration);

                //?????????eventTranceRdbDataSource???????????????
                if (StringUtils.hasText(eventTraceRdbDataSource)) {
                    BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
                    rdbFactory.addConstructorArgReference(eventTraceRdbDataSource);
                    rdbFactory.addConstructorArgValue(rdbFactory.getRawBeanDefinition());
                }
                //????????????
                List<?> elasticJobListeners = this.getTargetElasticJobListeners(conf);
                factory.addConstructorArgValue(elasticJobListeners);
                // 	??????????????????factory ????????? SpringJobScheduler?????????Spring?????????
                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

//                String registerBeanName = conf.name() + "SpringJobScheduler";
                String registerBeanName = conf.name();
                defaultListableBeanFactory.registerBeanDefinition(registerBeanName, factory.getBeanDefinition());
                SpringJobScheduler scheduler = (SpringJobScheduler) applicationContext.getBean(registerBeanName);
                scheduler.init();
                log.info("??????elastic-job??????: " + jobName);
            }
            log.info("????????????elastic-job???????????????: {} ???", beanMap.values().size());
        } catch (Exception e) {
            log.error("elasticjob ????????????, ??????????????????", e);
            System.exit(1);
        }
    }

    private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig conf) {
        List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
        String listeners = conf.listener();
        if (StringUtils.hasText(listeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
            factory.setScope("prototype");
            result.add(factory.getBeanDefinition());
        }

        String distributedListeners = conf.distributedListener();
        long startedTimeoutMilliseconds = conf.startedTimeoutMilliseconds();
        long completedTimeoutMilliseconds = conf.completedTimeoutMilliseconds();

        if (StringUtils.hasText(distributedListeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
            factory.setScope("prototype");
            factory.addConstructorArgValue(Long.valueOf(startedTimeoutMilliseconds));
            factory.addConstructorArgValue(Long.valueOf(completedTimeoutMilliseconds));
            result.add(factory.getBeanDefinition());
        }
        return result;
    }
}
