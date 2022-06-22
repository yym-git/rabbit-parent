package com.rabbit.api;

/**
 * @author ym.y
 * @description
 * @date 22:29 2022/3/25
 */
public final class MessageType {
    /**
     * 迅速消息：不需要保证消息的可靠性，也不需要confirm消息
     */
    public static final  String RAPID = "0";
    /**
     * 确认消息：不保证消息的可靠性，但是会进行消息的确认
     */
    public static final  String CONFIRM="1";
    /**
     * 可靠性消息：一定要保证消息的100%可靠性投递，不允许任何消息的丢失
     */
    public static final  String RELIANT="2";
}
