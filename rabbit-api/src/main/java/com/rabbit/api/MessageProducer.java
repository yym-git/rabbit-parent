package com.rabbit.api;

import com.rabbit.api.exception.MessageRuntimeException;

import java.util.List;

/**
 * @author ym.y
 * @description
 * @date 23:38 2022/3/25
 */
public interface MessageProducer {
    /**
     * 消息发送
     * @param message
     * @throws MessageRuntimeException
     */
    void send(Message message) throws MessageRuntimeException;

    /**
     * 消息发送，附带回调处理
     * @param message
     * @param sendCallback
     * @throws MessageRuntimeException
     */
    void send(Message message, SendCallback sendCallback) throws MessageRuntimeException;

    /**
     * 批量发送消息
     * @param messages
     * @throws MessageRuntimeException
     */
    void send(List<Message> messages) throws MessageRuntimeException;


}
