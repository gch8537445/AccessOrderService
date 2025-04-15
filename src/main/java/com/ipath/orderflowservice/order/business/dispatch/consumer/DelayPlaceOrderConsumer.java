package com.ipath.orderflowservice.order.business.dispatch.consumer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.business.dispatch.service.DispatchService;
import com.ipath.orderflowservice.order.dao.bean.ActivityLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
/**
 * 注意同一个group中的所有消费者，订阅topic和tags都必须相同，否则后启动注册的会冲掉先注册的
 * selectorExpression 是过滤tag
 */
// 集群方式消费, consumeThreadMax默认值64
@RocketMQMessageListener(topic = "${rocketmq.orderService.topicDispatch}", consumerGroup = "${rocketmq.orderService.group}", selectorExpression = "${rocketmq.orderService.tagPlaceOrder}", messageModel = MessageModel.CLUSTERING)
public class DelayPlaceOrderConsumer implements RocketMQListener<MessageExt> {


    // 延时下单
    @Value("${rocketmq.orderService.tagPlaceOrder}")
    private String tagPlaceOrder;

    @Autowired
    private DispatchService dispatchService;

    /**
     * 消费消息
     */
    @Override
    public void onMessage(MessageExt message) {
        String body = new String(message.getBody());
        log.info("--------- Cousumer received: tags=" + message.getTags() + ", keys=" + message.getKeys() + ", body=" + body);
        try {
            if (message.getTags().equals(tagPlaceOrder)) {
                JSONObject jsonObject = JSONUtil.parseObj(body);
                // 需求:极速派单调度 下单(mq 延时)
                log.info("订单号:{},需求:极速派单调度 下单(mq 延时) 开始===> ,coreOrderId:{}",jsonObject.getLong("orderId"),jsonObject.getLong("coreOrderId"));
                dispatchService.depayPlaceOrder(jsonObject.getLong("orderId"),jsonObject.getLong("coreOrderId"));
                log.info("订单号:{},需求:极速派单调度 下单(mq 延时) 结束<=== ,coreOrderId:{}",jsonObject.getLong("orderId"),jsonObject.getLong("coreOrderId"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
