package com.rabbit.core.producer.broker;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author ym.y
 * @description 发送异步消息
 * @date 10:51 2022/3/26
 */
@Slf4j
public class AsyncBaseQueue {
    private static final Integer THREAD_SIZE = Runtime.getRuntime().availableProcessors();
    private static final Integer QUEUE_SIZE = 1000;
    private static final ExecutorService executor = new ThreadPoolExecutor(
            THREAD_SIZE,
            THREAD_SIZE,
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_SIZE), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("rabbitmq client async sender");
            return thread;
        }
    }, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.error("async sender is error rejected,runnable：{},executor:{}", r, executor);
        }
    });

    public static void submit(Runnable r) {
        executor.submit(r);
    }
}
