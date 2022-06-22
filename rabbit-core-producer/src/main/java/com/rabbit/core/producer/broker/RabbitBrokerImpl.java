package com.rabbit.core.producer.broker;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import com.rabbit.core.producer.constant.BrokerMessageConst;
import com.rabbit.core.producer.constant.BrokerMessageStatus;
import com.rabbit.core.producer.entity.BrokerMessage;
import com.rabbit.core.producer.service.BrokerMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author ym.y
 * @description
 * @date 0:58 2022/3/26
 */
@Slf4j
@Component
public class RabbitBrokerImpl implements RabbitBroker {
    @Autowired
    private RabbiTemplateContainer rabbiTemplateContainer;
    @Autowired
    private BrokerMessageService brokerMessageService;

    /**
     * 发送迅速消息
     *
     * @param message
     */
    @Override
    public void sendRapid(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }


    /**
     * 发送确认消息
     *
     * @param message
     */
    @Override
    public void sendConfirm(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);
    }

    /**
     * 发送可靠消息
     *
     * @param message
     */
    @Override
    public void sendReliant(Message message) {
        message.setMessageType(MessageType.RELIANT);
        BrokerMessage bmsg = brokerMessageService.queryByMessageId(message.getMessageId());
        if (bmsg == null) {
            //发送消息的落库
            BrokerMessage brokerMessage = new BrokerMessage();
            brokerMessage.setMessageId(message.getMessageId());
            brokerMessage.setStatus(BrokerMessageStatus.SEND_ING.getCode());
            //tryCount：首次发送时为0
            //LocalDateTime转Date
            LocalDateTime now = LocalDateTime.now();
            Date date = this.localDateTimeToDate(now);
            DateTime dateTime = DateUtil.offsetMinute(date, BrokerMessageConst.TIMEOUT);
            //Date转LocalDateTime
            LocalDateTime localDateTime = this.dateToLocalDateTime(dateTime);
            brokerMessage.setNextTry(localDateTime);
            brokerMessage.setMessage(JSONUtil.toJsonStr(message));
            brokerMessage.setCreateTime(now);
            brokerMessage.setUpdateTime(now);
            brokerMessageService.insert(brokerMessage);
        }
        //真正发消息
        sendKernel(message);
    }

    /**
     * 批量发消息
     */
    @Override
    public void sendMessages() {
        List<Message> messages = MessageHolder.clear();
        messages.forEach(message -> {
            AsyncBaseQueue.submit((Runnable) () -> {
                CorrelationData correlationData = new CorrelationData(String.format("%s#%s#%s",
                        message.getMessageId(), System.currentTimeMillis(),message.getMessageType()));
                String topic = message.getTopic();
                String routingKey = message.getRoutingKey();
                RabbitTemplate rabbitTemplate = rabbiTemplateContainer.getRabbitTemplate(message);
                rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
                log.info("RabbitBrokerImpl#sendMessages# send message to rabbitmq,messageId:{}", message.getMessageId());
            });
        });
    }

    /**
     * 使用线程池异步发送消息
     *
     * @param message
     */
    private void sendKernel(Message message) {
        AsyncBaseQueue.submit((Runnable) () -> {
            String routingKey = message.getRoutingKey();
            String topic = message.getTopic();
            CorrelationData correlationData = new CorrelationData(String.format("%s#%s#%s",
                    message.getMessageId(), System.currentTimeMillis(),message.getMessageType()));
            RabbitTemplate rabbitTemplate = rabbiTemplateContainer.getRabbitTemplate(message);
            rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
            log.info("RabbitBrokerImpl#sendKernel# send message to rabbitmq,messageId:{}", message.getMessageId());
        });
    }

    public static void main(String[] args) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        ZonedDateTime zonedDateTime = now.atZone(zoneId);
        Date date = Date.from(zonedDateTime.toInstant());
        DateTime dateTime = DateUtil.offsetMinute(date, BrokerMessageConst.TIMEOUT);
        System.out.println(dateTime);
    }

    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        return date;
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return localDateTime;
    }
}
