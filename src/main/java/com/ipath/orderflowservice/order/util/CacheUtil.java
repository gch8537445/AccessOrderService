package com.ipath.orderflowservice.order.util;

import com.ipath.common.bean.BaseResponse;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.feignclient.CacheFeign;
import com.ipath.orderflowservice.feignclient.MessageFeign;
import com.ipath.orderflowservice.feignclient.dto.RequestCacheDto;
import com.ipath.orderflowservice.feignclient.dto.SendMailDto;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.vo.CacheOrder;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheUtil {
    @Autowired
    private CacheFeign cacheFeign;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${sendMsg.mail.enabled}")
    private Boolean SEND_ENABLED;

    @Value("${sendMsg.mail.to}")
    private String TO_EMAILS;

    @Value("${spring.profiles.active}")
    private String active;

    /**
     * 获取用户缓存中的订单
     * 
     * @param userId  用户id
     * @param orderId 订单id
     * @return
     */
    public CacheOrder getUserCacheOrder(Long userId, Long orderId) {
        CacheOrder cacheOrder = null;
        try {
            Object cacheOrderObject = redisUtil.hashGet(
                    CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(userId),
                    String.valueOf(orderId));

            if (ObjectUtil.isNotNull(cacheOrderObject)) {
                cacheOrder = JSONUtil.toBean(JSONUtil.toJsonStr(cacheOrderObject), CacheOrder.class);
            }
        } catch (Exception ex) {
            cacheOrder = (CacheOrder) redisUtil.hashGet(
                    CacheConsts.REDIS_KEY_USER_ORDER_PREFIX + String.valueOf(userId),
                    String.valueOf(orderId));
        }
        return cacheOrder;
    }

    /**
     * 保存缓存
     * 
     * @param key
     * @param value
     */
    public void saveCache(String key, String keyParameterValue, String sqlParameterValue, String item, String timeout,
            Object data) {
        if (StrUtil.isBlank(key) || StrUtil.isBlankIfStr(data))
            return;

        RequestCacheDto requestSaveCacheDto = new RequestCacheDto();
        requestSaveCacheDto.setKey(key);

        if (StrUtil.isNotBlank(keyParameterValue)) {
            requestSaveCacheDto.setKeyParameterValue(keyParameterValue);
        }

        if (StrUtil.isNotBlank(sqlParameterValue)) {
            requestSaveCacheDto.setSqlParameterValue(sqlParameterValue);
        }

        if (StrUtil.isNotBlank(item)) {
            requestSaveCacheDto.setItem(item);
        }
        requestSaveCacheDto.setData(data);
        if (StrUtil.isNotBlank(timeout)) {
            requestSaveCacheDto.setTimeout(timeout);
        }
        try {
            BaseResponse baseResponse = cacheFeign.saveCache(requestSaveCacheDto);
            if (baseResponse.getCode() == 0) {
                baseResponse.getData();
            }
        } catch (Exception ex) {
            sendEmail("下单服务异常-保存缓存信息",
                    StrUtil.format("<p>下单服务，保存缓存时，出现异常。key:{},key parameter,value:{}</p>" + ex, key, keyParameterValue,
                            data));
        }
    }

    /**
     * 获取缓存
     * 
     * @param key
     * @param keyParameterValue
     * @param sqlParameterValue
     * @param item
     * @return
     */
    public Object getCacheInfo(String key, String keyParameterValue, String sqlParameterValue, String item) {
        RequestCacheDto requestCacheDto = new RequestCacheDto();
        requestCacheDto.setKey(key);
        if (StrUtil.isNotBlank(keyParameterValue)) {
            requestCacheDto.setKeyParameterValue(keyParameterValue);
        }

        if (StrUtil.isNotBlank(sqlParameterValue)) {
            requestCacheDto.setSqlParameterValue(sqlParameterValue);
        }

        if (StrUtil.isNotBlank(item)) {
            requestCacheDto.setItem(item);
        }

        try {
            BaseResponse baseResponse = cacheFeign.getCache(requestCacheDto);
            if (baseResponse.getCode() == 0) {
                return baseResponse.getData();
            }
        } catch (Exception ex) {
            sendEmail("下单服务异常-获取缓存信息", "<p>下单服务，从缓存中获取信息时，出现异常。</p>" + ex);
        }

        return null;
    }

    /**
     * 企业人员是否在灰度中
     * 
     * @param companyId 公司id
     * @param userId    用户id
     * @return true-开启 false-未开启
     */
    public boolean isOpenGray(Long companyId, Long userId) {
        try {
            String key = CacheConsts.REDIS_KEY_USER_CONFIG_GRAY + companyId;
            if (redisUtil.hasKey(key)) {
                JSONObject grayJsonObject = JSONUtil.parseObj(redisUtil.get(key));
                if (grayJsonObject.getBool("fullOn", false)) {
                    return true;
                } else {
                    List<Long> grayUserList = JSONUtil.toList(
                            JSONUtil.toJsonStr(grayJsonObject.getJSONArray("userIds")),
                            Long.class);
                    if (null != grayUserList && grayUserList.size() > 0
                            && grayUserList.contains(userId)) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            sendEmail("下单服务异常-获取灰度标识", "<p>下单服务，获取灰度标识时，出现异常。</p>" + ex);
        }
        return false;
    }

    /**
     * 发送邮件
     * 
     * @param title
     * @param body
     */
    private void sendEmail(String title, String body) {
        try {
            if (SEND_ENABLED == false)
                return;

            SendMailDto sendMailDto = new SendMailDto();
            sendMailDto.setCompanyId(0L);
            ;
            sendMailDto.setToEmail(TO_EMAILS);
            sendMailDto.setTitle(StrUtil.format("【{}】【order-flow-service】{}", active, title));
            sendMailDto.setBodyInfo(body);
            messageFeign.sendByEmail(sendMailDto);
        } catch (Exception ex) {
            log.error("发送邮件出现异常【CacheUtil->sendEmail,title:{}】", title, ex);
        }
    }
}
