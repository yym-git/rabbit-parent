package com.rabbit.common.serializer.convert;

import com.google.common.base.Preconditions;
import com.rabbit.common.serializer.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author ym.y
 * @description
 * @date 16:28 2022/3/26
 */
public class GenericMessageConvert implements MessageConverter {
    private Serializer serializer;

    public GenericMessageConvert(Serializer serializer) {
        Preconditions.checkNotNull(serializer);
        this.serializer = serializer;
    }

    /**
     * 序列化：自己定义的message转换成amqp的message
     *
     * @param object
     * @param messageProperties
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(this.serializer.serializeRaw(object), messageProperties);
    }

    /**
     * 反序列化：amqp的message转换成自己定义的message
     *
     * @param message
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return this.serializer.deserialize(message.getBody());
    }
}
