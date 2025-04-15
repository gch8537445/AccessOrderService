package com.ipath.orderflowservice.order.consumer;

import com.ipath.orderflowservice.order.service.OrderService;

import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSONObject;

@Slf4j
@Component
/**
 * 注意同一个group中的所有消费者，订阅topic和tags都必须相同，否则后启动注册的会冲掉先注册的
 * selectorExpression 是过滤tag
 */
// 集群方式消费, consumeThreadMax默认值64
@RocketMQMessageListener(topic = "${rocketmq.orderStatusChg.topic}", consumerGroup = "${rocketmq.orderStatusChg.group}", selectorExpression = "${rocketmq.orderStatusChg.tag}", messageModel = MessageModel.CLUSTERING)
public class OrderStatusChgConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private OrderService orderService;

    // 要接收的tag
    @Value("${rocketmq.orderStatusChg.tag}")
    private String TAG_ORDER_STATUS_CHG;

    /**
     * 消费消息
     */
    @Override
    public void onMessage(MessageExt message) {
        String body = new String(message.getBody());
        log.info("--------- Cousumer received: tags=" + message.getTags() + ", keys=" + message.getKeys() + ", body="
                + body);
        try {
            if (message.getTags().equals(TAG_ORDER_STATUS_CHG)) {
                JSONObject orderStatusJsonObject = new JSONObject(body);
                orderService.notifyOrderStatus(
                    orderStatusJsonObject.getLong("orderId"),
                    orderStatusJsonObject.getShort("previousOrderState"),
                    orderStatusJsonObject.getShort("orderState"),
                    orderStatusJsonObject.getBool("replaceOrderFlag",false),
                    orderStatusJsonObject.getStr("phoneForUser")
                    );
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
