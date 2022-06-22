package com.rabbit.core.producer.service;

import com.rabbit.core.producer.constant.BrokerMessageStatus;
import com.rabbit.core.producer.entity.BrokerMessage;

import java.util.List;

/**
 * @author ym.y
 * @description
 * @date 13:04 2022/3/27
 */
public interface BrokerMessageService {
    int insert(BrokerMessage brokerMessage);

    void success(String messageId);

    void failure(String messageId);

    List<BrokerMessage>  fetchTimeoutMessage4Retry(BrokerMessageStatus status);

    BrokerMessage queryByMessageId(String messageId);

    int updateTryCount(String messageId);
}
