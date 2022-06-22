package com.rabbit.core.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbit.core.producer.entity.BrokerMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 消息发送记录表 Mapper 接口
 * </p>
 *
 * @author yym
 * @since 2022-03-26
 */
@Mapper
public interface BrokerMessageMapper extends BaseMapper<BrokerMessage> {
}
