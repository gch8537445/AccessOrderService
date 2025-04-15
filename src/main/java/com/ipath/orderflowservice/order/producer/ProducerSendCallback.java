package com.ipath.orderflowservice.order.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * 生产者消息发送回调
 */
@Slf4j
public class ProducerSendCallback implements SendCallback {

    private String name;

    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("mq:{},消息发送成功。内容:{}", name, sendResult.toString());
    }

    @Override
    public void onException(Throwable throwable) {
        log.error("mq:{},消息发送失败。", name, throwable.getMessage());
    }

    ProducerSendCallback(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}