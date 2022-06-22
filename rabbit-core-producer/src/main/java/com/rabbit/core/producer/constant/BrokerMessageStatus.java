package com.rabbit.core.producer.constant;

/**
 * @author ym.y
 * @description 消息发送状态
 * @date 12:12 2022/3/27
 */
public enum BrokerMessageStatus {
    SEND_ING("0"),
    SEND_OK("1"),
    SEND_FAIL("2")
    ;
    private String code;

    BrokerMessageStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
