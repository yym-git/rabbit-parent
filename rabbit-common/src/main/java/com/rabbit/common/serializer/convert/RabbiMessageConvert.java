package com.rabbit.common.serializer.convert;

import com.google.common.base.Preconditions;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author ym.y
 * @description 装饰类
 * @date 16:45 2022/3/26
 */
public class RabbiMessageConvert implements MessageConverter {
    private GenericMessageConvert genericMessageConvert;

    public RabbiMessageConvert(GenericMessageConvert genericMessageConvert) {
        Preconditions.checkNotNull(genericMessageConvert);
        this.genericMessageConvert = genericMessageConvert;
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        //设置延迟
        com.rabbit.api.Message  message = ( com.rabbit.api.Message) object;
        messageProperties.setDelay(message.getDelayMills());
        return this.genericMessageConvert.toMessage(object, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        com.rabbit.api.Message msg = (com.rabbit.api.Message) this.genericMessageConvert.fromMessage(message);
        return msg;
    }
}
