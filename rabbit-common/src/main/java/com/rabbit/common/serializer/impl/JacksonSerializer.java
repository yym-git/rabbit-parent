package com.rabbit.common.serializer.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rabbit.common.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * @author ym.y
 * @description
 * @date 15:28 2022/3/26
 */
@Slf4j
public class JacksonSerializer implements Serializer {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // 美化输出，转换为格式化的json
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        //在遇到未知属性的时候不抛出异常。
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //允许序列化空的POJO类，否则序列化空对象是会抛出异常
        mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        //强制JSON 空字符串("")转换为null对象值:
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        //是否允许使用java/C++样式注释
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //是否允许使用非双引号属性名
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //是否允许使用单引号来包住属性名或者属性值
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private final JavaType type;

    private JacksonSerializer(JavaType type) {
        this.type = type;
    }

    private JacksonSerializer(Type type) {
        this.type = mapper.getTypeFactory().constructType(type);
    }

    public static JacksonSerializer createParametricType(Class<?> cls) {
        return new JacksonSerializer(mapper.getTypeFactory().constructType(cls));
    }

    /**
     * 对象转字节数组
     *
     * @param object
     * @return
     */
    @Override
    public byte[] serializeRaw(Object object) {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (Exception e) {
            log.error("序列化出错:{}",e);
        }
        return null;
    }

    /**
     * 对象转字符串
     *
     * @param object
     * @return
     */
    @Override
    public String serialize(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("序列化出错:{}",e);
        }
        return null;
    }

    /**
     * 字符串转对象
     *
     * @param content
     * @param <T>
     * @return
     */
    @Override
    public <T> T deserialize(String content) {
        try {
            return mapper.readValue(content,type);
        } catch (Exception e) {
            log.error("反序列化出错:{}",e);
        }
        return null;
    }

    /**
     * 字节数组转对象
     *
     * @param bytes
     * @param <T>
     * @return
     */
    @Override
    public <T> T deserialize(byte[] bytes) {
        try {
            return mapper.readValue(bytes,type);
        } catch (Exception e) {
            log.error("反序列化出错:{}",e);
        }
        return null;
    }
}
