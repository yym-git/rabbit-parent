package com.rabbit.core.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import com.rabbit.api.exception.MessageRuntimeException;
import com.rabbit.common.serializer.Serializer;
import com.rabbit.common.serializer.convert.GenericMessageConvert;
import com.rabbit.common.serializer.convert.RabbiMessageConvert;
import com.rabbit.common.serializer.impl.JacksonSerializerFactory;
import com.rabbit.core.producer.service.BrokerMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ym.y
 * @description rabbitTemplate池化操作
 * @date 14:39 2022/3/26
 */
@Slf4j
@Component
public class RabbiTemplateContainer implements RabbitTemplate.ConfirmCallback {
    @Autowired
    private BrokerMessageService brokerMessageService;
    /**
     * key ： topic
     * value ： rabbitTemplate
     */
    private Map<String, RabbitTemplate> rabbitMap = new ConcurrentHashMap<>();
    private Splitter splitter = Splitter.on("#");
    @Autowired
    private ConnectionFactory connectionFactory;

    public RabbitTemplate getRabbitTemplate(Message message) throws MessageRuntimeException {
        Preconditions.checkNotNull(message);
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitMap.get(topic);
        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }
        log.info("RabbiTemplateContainer#getRabbitTemplate# topic:{}", topic);
        rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(topic);
        rabbitTemplate.setRetryTemplate(new RetryTemplate());
        rabbitTemplate.setRoutingKey(message.getRoutingKey());
        //添加序列化和反序列化以及convert对象
        Serializer serializer = JacksonSerializerFactory.getInstance().create();
        GenericMessageConvert gmc = new GenericMessageConvert(serializer);
        RabbiMessageConvert rgc = new RabbiMessageConvert(gmc);
        rabbitTemplate.setMessageConverter(rgc);
        //只要不是迅速消息都需要做消息应答处理
        if (!MessageType.RAPID.equals(message.getMessageType())) {
            //设置消息发送后的确认处理
            rabbitTemplate.setConfirmCallback(this);
        }
        rabbitMap.put(message.getTopic(), rabbitTemplate);
        return rabbitTemplate;
    }

    /**
     * 确认消息是否发送到Broker
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //具体的消息应答
        List<String> strs = splitter.splitToList(correlationData.getId());
        String messageId = strs.get(0);
        long sendTime = Long.parseLong(strs.get(1));
        String messageType = strs.get(2);
        if (ack) {
            //只有是可靠性消息才会去更新 消息日志
            //当Broker返回的ack成功时，更新消息日志表对应的消息状态
            if(MessageType.RELIANT.equals(messageType)){
                brokerMessageService.success(messageId);
                log.info("send message is Ok,confirm messageId{:{},sendTime:{}", messageId, sendTime);
            }
        } else {
            //消息发送失败：可能是磁盘满了或者网络闪断，需要根据具体的情况进行处理
            log.error("send message is Fail,confirm messageId{:{},sendTime:{}", messageId, sendTime);
        }
    }
}
