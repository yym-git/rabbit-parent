package com.rabbit.core.producer.broker;

import com.rabbit.api.Message;

import java.util.List;

/**
 * @author ym.y
 * @description 具体发送不同类型消息的接口
 * @date 0:49 2022/3/26
 */
public interface RabbitBroker {
    void sendRapid(Message message);

    void sendConfirm(Message message);

    void sendReliant(Message message);

    void sendMessages();
}
