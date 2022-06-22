package com.rabbit.test;

import com.rabbit.api.Message;
import com.rabbit.api.MessageBuilder;
import com.rabbit.api.MessageType;
import com.rabbit.core.producer.broker.ProducerClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ym.y
 * @description
 * @date 12:53 2022/4/5
 */
@SpringBootTest
public class ApplicationTest {
    @Autowired
    private ProducerClient producerClient;

    @Test
    void testProducerClient() throws InterruptedException {
        for(int i =0; i<1; i++){
            String uuid= UUID.randomUUID().toString();
            Map<String,Object> attributes = new HashMap<>();
            attributes.put("name", "詹丹");
            attributes.put("age", "24");
            Message message = MessageBuilder.create()
                    .withTopic("exchange-1")
                    .wittMessageId(uuid)
                    .withKey("springboot.abc")
                    .withDelayMills(0)
                    .withProperties(attributes)
                    .withMessageType(MessageType.RELIANT)
                    .build();
            producerClient.send(message);
        }
        Thread.sleep(30000);
    }

    @Test
    void printTest(){
        System.out.println("==============");
    }
}
