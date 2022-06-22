CREATE TABLE IF NOT EXISTS`broker_message` (
  `message_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_croatian_ci NOT NULL COMMENT '消息id',
  `message` varchar(4000) COLLATE utf8mb4_croatian_ci DEFAULT NULL COMMENT '消息内容',
  `try_count` int DEFAULT NULL COMMENT '重发次数',
  `status` varchar(10) COLLATE utf8mb4_croatian_ci DEFAULT NULL COMMENT '消息发送状态',
  `next_try` datetime DEFAULT NULL COMMENT '下次重发时间',
  `create_time` datetime DEFAULT NULL COMMENT '消息首次发送时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_croatian_ci COMMENT='消息发送记录表';