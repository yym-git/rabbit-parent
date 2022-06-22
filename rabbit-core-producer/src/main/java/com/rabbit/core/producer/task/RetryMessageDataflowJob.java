package com.rabbit.core.producer.task;

import cn.hutool.json.JSONUtil;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.rabbit.api.Message;
import com.rabbit.core.producer.broker.RabbitBroker;
import com.rabbit.core.producer.constant.BrokerMessageStatus;
import com.rabbit.core.producer.entity.BrokerMessage;
import com.rabbit.core.producer.service.BrokerMessageService;
import com.rabbit.task.annotation.ElasticJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ym.y
 * @description
 * @date 12:17 2022/3/30
 */
@Slf4j
@Component
@ElasticJobConfig(name = "com.rabbit.core.producer.task.RetryMessageDataflowJob",
        cron = "0/10 * * * * ?",
        description = "可靠性投递消息补偿任务",
        overwrite = true,
        shardingTotalCount = 1)
public class RetryMessageDataflowJob implements DataflowJob<BrokerMessage> {
    @Autowired
    private BrokerMessageService brokerMessageService;
    @Autowired
    private RabbitBroker rabbitBroker;
    private static final int MAX_RETRY = 3;

    @Override
    public List<BrokerMessage> fetchData(ShardingContext shardingContext) {
        List<BrokerMessage> list = brokerMessageService.fetchTimeoutMessage4Retry(BrokerMessageStatus.SEND_ING);
        log.info("----抓取数据集合:{}--------------------------------", list.size());
        return list;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<BrokerMessage> dataList) {
        log.info("======================processData方法中的数据集合：{}==============================", dataList.size());
        for (BrokerMessage x : dataList) {
            int count = x.getTryCount() == null ? 0 : x.getTryCount();
            if (count>=MAX_RETRY) {
                //大于重试次数：设置为发送失败
                this.brokerMessageService.failure(x.getMessageId());
                log.warn("------消息最终设置为失败，消息ID:{}------------", x.getMessageId());
            } else {
                //更新消息重发次数
                brokerMessageService.updateTryCount(x.getMessageId());
                //重发消息
                String msgStr = x.getMessage();
                Message message = JSONUtil.toBean(msgStr, Message.class);
                rabbitBroker.sendReliant(message);
            }
        }
//        dataList.forEach(x -> {
//            int count = x.getTryCount() == null ? 0 : x.getTryCount();
//            if (MAX_RETRY >= count) {
//                //大于重试次数：设置为发送失败
//                this.brokerMessageService.failure(x.getMessageId());
//                log.warn("------消息最终设置为失败，消息ID:{}------------", x.getMessageId());
//            } else {
//                //更新消息重发次数
//                brokerMessageService.updateTryCount(x.getMessageId());
//                //重发消息
//                String msgStr = x.getMessage();
//                Message message = JSONUtil.toBean(msgStr, Message.class);
//                rabbitBroker.sendReliant(message);
//            }
//        });
    }
}
