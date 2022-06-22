package com.rabbit.core.producer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbit.core.producer.constant.BrokerMessageStatus;
import com.rabbit.core.producer.entity.BrokerMessage;
import com.rabbit.core.producer.mapper.BrokerMessageMapper;
import com.rabbit.core.producer.service.BrokerMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ym.y
 * @description
 * @date 13:05 2022/3/27
 */
@Slf4j
@Service
public class BrokerMessageServiceImpl implements BrokerMessageService {
    @Autowired
    private BrokerMessageMapper brokerMessageMapper;

    @Override
    public int insert(BrokerMessage brokerMessage) {
        int num = brokerMessageMapper.insert(brokerMessage);
        return num;
    }

    @Override
    public void success(String messageId) {
        log.info("messageId:{}", messageId);
        BrokerMessage brokerMessage = new BrokerMessage();
        brokerMessage.setMessageId(messageId);
        brokerMessage.setUpdateTime(LocalDateTime.now());
        brokerMessage.setStatus(BrokerMessageStatus.SEND_OK.getCode());
        int num = brokerMessageMapper.updateById(brokerMessage);
        log.info("消息发送成功，更新message消息状态:{}", num);
    }

    @Override
    public void failure(String messageId) {
        BrokerMessage brokerMessage = new BrokerMessage();
        brokerMessage.setMessageId(messageId);
        brokerMessage.setUpdateTime(LocalDateTime.now());
        brokerMessage.setStatus(BrokerMessageStatus.SEND_FAIL.getCode());
        int num = brokerMessageMapper.updateById(brokerMessage);

        log.info("消息发送失败，更新message消息状态:{}", num);

    }

    @Override
    public List<BrokerMessage> fetchTimeoutMessage4Retry(BrokerMessageStatus status) {
        log.info("--------------------------定时任务查询消息状态为0-发送中，并且当前时间大于重试时间的记录------------------");
        //当前时间大于重试时间
        LambdaQueryWrapper<BrokerMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BrokerMessage::getStatus, status.getCode())
                .lt(BrokerMessage::getNextTry, DateUtil.now());
        List<BrokerMessage> brokerMessages = brokerMessageMapper.selectList(lambdaQueryWrapper);
        log.info("-------------------------------------查询结果：{}---------------------------------------------------", brokerMessages.size());
        return brokerMessages;
    }

    @Override
    public BrokerMessage queryByMessageId(String messageId) {
        BrokerMessage brokerMessage = brokerMessageMapper.selectById(messageId);
        return brokerMessage;
    }

    @Override
    public int updateTryCount(String messageId) {
        log.info("--------------------------------更新tryCount重试次数,messageId:{}-----------------", messageId);
        BrokerMessage brokerMessage = brokerMessageMapper.selectById(messageId);
        if (brokerMessage != null) {
            int count = brokerMessage.getTryCount() == null ? 0 : brokerMessage.getTryCount();
            brokerMessage.setTryCount(count + 1);
            brokerMessage.setUpdateTime(LocalDateTime.now());
            int num = brokerMessageMapper.updateById(brokerMessage);
            return num;
        }
        return 0;
    }

    public static void main(String[] args) {

    }
}
