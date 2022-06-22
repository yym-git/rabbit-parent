package com.rabbit.api;

import cn.hutool.core.util.StrUtil;
import com.rabbit.api.exception.MessageRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ym.y
 * @description
 * @date 23:11 2022/3/25
 */
public class MessageBuilder {
    private String topic;
    private String messageId;
    private String routingKey = "";
    private Map<String, Object> properties = new HashMap();
    private int delayMills;
    private String messageType = MessageType.CONFIRM;

    public static MessageBuilder create() {
        return new MessageBuilder();
    }


    public MessageBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder wittMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public MessageBuilder withKey(String key) {
        this.routingKey = key;
        return this;
    }

    public MessageBuilder withProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public MessageBuilder withDelayMills(int delayMills) {
        this.delayMills = delayMills;
        return this;
    }

    public MessageBuilder withMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public Message build() {
        if (StrUtil.isBlankIfStr(messageId)) {
            messageId = UUID.randomUUID().toString();
        }
        if (topic == null) {
            throw new MessageRuntimeException("topic is  null");
        }
        return new Message(topic, messageId, routingKey, properties, delayMills, messageType);
    }
}
