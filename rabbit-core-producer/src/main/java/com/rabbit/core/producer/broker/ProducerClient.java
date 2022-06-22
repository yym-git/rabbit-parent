package com.rabbit.core.producer.broker;

import com.google.common.base.Preconditions;
import com.rabbit.api.Message;
import com.rabbit.api.MessageProducer;
import com.rabbit.api.MessageType;
import com.rabbit.api.SendCallback;
import com.rabbit.api.exception.MessageRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ym.y
 * @description
 * @date 0:25 2022/3/26
 */
@Component
public class ProducerClient implements MessageProducer {
    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public void send(Message message) throws MessageRuntimeException {
        Preconditions.checkNotNull(message.getTopic());
        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.sendRapid(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.sendConfirm(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.sendReliant(message);
                break;
            default:
                break;
        }
    }

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRuntimeException {

    }

    @Override
    public void send(List<Message> messages) throws MessageRuntimeException {
        messages.forEach(message -> {
            message.setMessageType(MessageType.RAPID);
            MessageHolder.addMessage(message);
        });
        rabbitBroker.sendMessages();
    }
}
