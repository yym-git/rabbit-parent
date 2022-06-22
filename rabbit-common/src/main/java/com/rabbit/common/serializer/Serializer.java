package com.rabbit.common.serializer;

/**
 * @author ym.y
 * @description
 * @date 15:21 2022/3/26
 */
public interface Serializer {
    byte[] serializeRaw(Object object);

    String serialize(Object object);

    <T> T deserialize(String content);

    <T> T deserialize(byte[] bytes);
}
