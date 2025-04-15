package com.ipath.orderflowservice.order.producer;

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
public class DelayMstPayProducer extends RocketMQTemplate {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RedisUtil redisUtil;

    // 报表topic
    @Value("${rocketmq.report.topic}")
    private String REPORT_TOPIC;
    // 报表tag
    @Value("${rocketmq.report.tag}")
    private String REPORT_TAG;

    // 结算topic
    @Value("${rocketmq.settle.topic}")
    private String SETTLE_TOPIC;
    // 结算tag
    @Value("${rocketmq.settle.tag}")
    private String SETTLE_TAG;


    @Value("${rocketmq.topic.order-info}")
    private String ORDER_INFO;

    @Value("${rocketmq.tags.process}")
    private String PROCESS_TAG;

    private static final int DEFAULT_DELAY_LEVEL = 1;
    private static final long DEFAULT_TIMEOUT = 3000;

    /**
     * 发送 mst服务支付
     * 
     * @param dataObject
     */
    public void sendDelayMstPay(JSONObject dataObject) {
        try {
            String destination = REPORT_TOPIC + ":" + REPORT_TAG;
            JSONObject jsonObject = new JSONObject();
            Set<Map.Entry<String, Object>> set = dataObject.entrySet();
            for (Map.Entry<String, Object> map : set) {
                jsonObject.set(map.getKey(), map.getValue());
            }
            String msgModel = JSONUtil.toJsonStr(jsonObject);
            Message msg = MessageBuilder.withPayload(msgModel).build();

            rocketMQTemplate.asyncSend(
                    destination,
                    msg,
                    new ProducerSendCallback("发送 报表"),
                    DEFAULT_TIMEOUT,
                    1);
        } catch (Exception e) {
            log.info("sendDelayReport ===> 失败：{}", e.getMessage());
        }
    }

    /**
     * 发送 结算
     * 
     * @param dataObject
     */
    public void sendDelaySettle(JSONObject dataObject) {
        try {
            String destination = SETTLE_TOPIC + ":" + SETTLE_TAG;
            JSONObject jsonObject = new JSONObject();
            Set<Map.Entry<String, Object>> set = dataObject.entrySet();
            for (Map.Entry<String, Object> map : set) {
                jsonObject.set(map.getKey(), map.getValue());
            }
            String msgModel = JSONUtil.toJsonStr(jsonObject);
            Message msg = MessageBuilder.withPayload(msgModel).build();

            rocketMQTemplate.asyncSend(
                    destination,
                    msg,
                    new ProducerSendCallback("发送 结算"),
                    DEFAULT_TIMEOUT,
                    1);
        } catch (Exception e) {
            log.info("sendDelayReport ===> 失败：{}", e.getMessage());
        }
    }

    /**
     * 订单状态5 但是消息不全,30分钟后重新获取信息,在执行
     *
     * @param dataObject
     */
    public void processOrderDetail(JSONObject dataObject) {
        try {
            String destination = ORDER_INFO + ":" + PROCESS_TAG;
            JSONObject jsonObject = new JSONObject();
            Set<Map.Entry<String, Object>> set = dataObject.entrySet();
            for (Map.Entry<String, Object> map : set) {
                jsonObject.set(map.getKey(), map.getValue());
            }
            String msgModel = JSONUtil.toJsonStr(jsonObject);
            Message msg = MessageBuilder.withPayload(msgModel).build();

            rocketMQTemplate.asyncSend(
                    destination,
                    msg,
                    new ProducerSendCallback("流程状态补全"),
                    DEFAULT_TIMEOUT,
                    16);
        } catch (Exception e) {
            log.info("sendDelayReport ===> 失败：{}", e.getMessage());
        }
    }
}
