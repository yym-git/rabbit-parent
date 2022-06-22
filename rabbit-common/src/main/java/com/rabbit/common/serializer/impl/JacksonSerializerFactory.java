package com.rabbit.common.serializer.impl;

import com.rabbit.api.Message;
import com.rabbit.common.serializer.Serializer;
import com.rabbit.common.serializer.SerializerFactory;

/**
 * @author ym.y
 * @description
 * @date 16:20 2022/3/26
 */
public class JacksonSerializerFactory implements SerializerFactory {
    private static final JacksonSerializerFactory jksf = new JacksonSerializerFactory();

    private JacksonSerializerFactory() {
    }

    public static JacksonSerializerFactory getInstance() {
        return jksf;
    }

    @Override
    public Serializer create() {
        return JacksonSerializer.createParametricType(Message.class);
    }
}
