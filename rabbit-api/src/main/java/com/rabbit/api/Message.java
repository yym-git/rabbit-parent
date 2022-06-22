package com.rabbit.api;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ym.y
 * @description
 * @date 18:47 2022/3/25
 */
@Data
public class Message implements Serializable {

    private static final long serialVersionUID = -6427856076762208161L;
    /**
     * 主題（exchange）
     */
    private String topic;
    /**
     * 消息id
     */
    private String messageId;
    /**
     * 路由规则
     */
    private String routingKey = "";
    /**
     * 附加信息
     */
    private Map<String, Object> properties = new HashMap();
    /**
     * 延迟消息配置
     */
    private int delayMills;

    /**
     * 消息类型：默认为确认消息
     */
    private String messageType = MessageType.CONFIRM;

    public Message() {
    }

    public Message(String topic, String messageId, String routingKey, Map<String, Object> properties, int delayMills, String messageType) {
        this.topic = topic;
        this.messageId = messageId;
        this.routingKey = routingKey;
        this.properties = properties;
        this.delayMills = delayMills;
        this.messageType = messageType;
    }

}
