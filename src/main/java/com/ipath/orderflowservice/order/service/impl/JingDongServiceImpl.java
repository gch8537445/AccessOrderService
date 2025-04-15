package com.ipath.orderflowservice.order.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.order.service.JingDongService;
import com.ipath.orderflowservice.order.service.UserService;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.jdxcx.MiniappSubmitZeroOrderRequest;
import com.jd.open.api.sdk.response.jdxcx.MiniappSubmitZeroOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class JingDongServiceImpl implements JingDongService {

    @Value("${jingDong.url}")
    private String serverUrl;
    @Value("${jingDong.app_key}")
    private String appKey;
    @Value("${jingDong.app_secret}")
    private String appSecret;
    @Value("${spring.profiles.active}")
    private String active;

    private static final String REDIS_KEY_JD_ACCESSTOKEN = "user:jd:miniapp.access_token";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;


//    //全局http代理
//    @PostConstruct
//    public void setProperty(){
//        //测试环境
//        if(StringUtils.equals(active,"dev") ||
//                StringUtils.equals(active,"test")||
//                StringUtils.equals(active,"uat")
//                ){
//            // 对http开启全局代理
//            System.setProperty("http.proxyHost", "172.16.5.19");
//            System.setProperty("http.proxyPort", "7788");
//            // 对https开启全局代理
//            System.setProperty("https.proxyHost", "172.16.5.19");
//            System.setProperty("https.proxyPort", "7788");
//            log.info("setProperty===> {}开启http代理成功 ", active);
//        }
//        //生产环境
//        if(StringUtils.equals(active,"prod")){
//            // 对http开启全局代理
//            System.setProperty("http.proxyHost", "172.16.99.100");
//            System.setProperty("http.proxyPort", "7788");
//            // 对https开启全局代理
//            System.setProperty("https.proxyHost", "172.16.99.100");
//            System.setProperty("https.proxyPort", "7788");
//            log.info("setProperty===> prod开启http代理成功 ");
//        }
//    }

    /**
     * 获取京东订单Id
     *
     * @param userId
     * @throws Exception
     */
    @Override
    public String getJingDongOrderId(Long userId, String sku) throws Exception {
        boolean hasKey = redisUtil.hashHasKey(REDIS_KEY_JD_ACCESSTOKEN, userId.toString());
        String staffCode = "";
        if (hasKey) {
            JSONObject userObject = userService.getUserInfoByUserId(userId);
            if (userObject.containsKey("staffCode")) {
                staffCode = userObject.getStr("staffCode");
            } else {
                throw new BusinessException("用户工号为空");
            }

            String accessToken = (String) redisUtil.hashGet(REDIS_KEY_JD_ACCESSTOKEN, userId.toString());
            JdClient client = new DefaultJdClient(serverUrl, accessToken, appKey, appSecret);
            MiniappSubmitZeroOrderRequest request = new MiniappSubmitZeroOrderRequest();
            request.setQuantity("1");
            request.setSku(sku);//"10075782792493" -> 10082897995057
            request.setPin(staffCode);
            request.setOpenIdBuyer(staffCode);
            request.setTrackerId(staffCode);
            request.setXidBuyer(staffCode);
            log.info("getJingDongOrderId===>请求京东服务，请求参数userId{},serverUrl:{}，appKey:{}，appSecret:{},accessToken{}，sku{}", userId, serverUrl, appKey, appSecret, accessToken, sku);
            MiniappSubmitZeroOrderResponse response = client.execute(request);
            if(null == response.getResponseResult()
                    || null == response.getResponseResult().getOrderResult()
                    ||StringUtils.isBlank(response.getResponseResult().getOrderResult().getOrderId())
                    ){
                log.error("getJingDongOrderId===>请求京东服务，获取订单id失败，返回结果：{}", JSONUtil.toJsonStr(response));
                throw new BusinessException("请求京东服务，获取订单id失败");
            }
            log.info("getJingDongOrderId===>请求京东服务，获取订单id成功，返回结果：{}", JSONUtil.toJsonStr(response));
            return response.getResponseResult().getOrderResult().getOrderId();
        } else {
            log.error("getJingDongOrderId===>userId:{},access_token 缓存信息不存在",userId);
            throw new BusinessException("access_token 已经失效，请重新登录");
        }
    }
}
