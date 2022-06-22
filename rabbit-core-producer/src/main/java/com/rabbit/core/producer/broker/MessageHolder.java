package com.rabbit.core.producer.broker;

import com.google.common.collect.Lists;
import com.rabbit.api.Message;

import java.util.List;

/**
 * @author ym.y
 * @description
 * @date 14:03 2022/3/30
 */
public class MessageHolder {
    private List<Message> messages = Lists.newArrayList();
    public static final ThreadLocal<MessageHolder> holder = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new MessageHolder();
        }
    };

    public static void addMessage(Message message) {
        holder.get().messages.add(message);
    }

    public static List<Message> clear() {
        List<Message> tempList = Lists.newArrayList(holder.get().messages);
        holder.remove();
        return tempList;
    }
}
