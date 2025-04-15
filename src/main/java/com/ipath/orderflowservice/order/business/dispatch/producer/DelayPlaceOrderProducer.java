package com.ipath.orderflowservice.order.business.dispatch.producer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 *
 * 延时消息 Producer
 *
 * 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h rocketMQ自动支持18个级别
 * 等级全部转化为秒
 */
@Component
@Slf4j
public class DelayPlaceOrderProducer extends RocketMQTemplate {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RedisUtil redisUtil;

    // 极速派单
    @Value("${rocketmq.orderService.topicDispatch}")
    private String topicDispatch;

    // 延时下单
    @Value("${rocketmq.orderService.tagPlaceOrder}")
    private String tagPlaceOrder;


    private static final int DEFAULT_DELAY_LEVEL = 1;
    private static final long DEFAULT_TIMEOUT = 3000;

    /**
     * 发送 极速派单 延时下单
     * 
     * @param dataObject
     */
    public void sendDelayPlaceOrder(JSONObject dataObject, long delayTime) {
        try {
            String destination = topicDispatch + ":" + tagPlaceOrder;

            String msgModel = dataObject.toString();
            Message msg = MessageBuilder.withPayload(msgModel).build();

            rocketMQTemplate.asyncSend(
                    destination,
                    msg,
                    new ProducerSendCallback("发送 极速派单 延时下单"),
                    DEFAULT_TIMEOUT,
                    this.getDelayLevel(delayTime));

        } catch (Exception e) {
            log.info("sendDelayPlaceOrder ===> 失败：{}", e.getMessage());
        }
    }

    /**
     *  1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h rocketMQ自动支持18个级别
     * @return
     */
    private int getDelayLevel(long delayTime) {

        if(0 < delayTime && delayTime <= 1){
            return 1;
        } else if (1 < delayTime && delayTime <= 5){
            return 2;
        }else if (5 < delayTime && delayTime <= 10){
            return 3;
        }else if (10 < delayTime && delayTime <= 30){
            return 4;
        }else if (30 < delayTime && delayTime <= 60){
            return 5;
        }else if (60 < delayTime && delayTime <= 120){
            return 6;
        }else if (120 < delayTime && delayTime <= 180){
            return 7;
        }else if (180 < delayTime && delayTime <= 240){
            return 8;
        }else if (240 < delayTime && delayTime <= 300){
            return 9;
        }else if (300 < delayTime && delayTime <= 360){
            return 10;
        }else if (360 < delayTime && delayTime <= 420){
            return 11;
        }else if (420 < delayTime && delayTime <= 480){
            return 12;
        }else if (480 < delayTime && delayTime <= 540){
            return 13;
        }else if (540 < delayTime && delayTime <= 600){
            return 14;
        }else if (600 < delayTime && delayTime <= 1200){
            return 15;
        }else if (1200 < delayTime && delayTime <= 1800){
            return 16;
        }else if (1800 < delayTime && delayTime <= 3600){
            return 17;
        }else if (3600 < delayTime && delayTime <= 7200){
            return 18;
        }else if (7200 < delayTime){
            return 18;
        }else{
            return 1;
        }
    }
}
