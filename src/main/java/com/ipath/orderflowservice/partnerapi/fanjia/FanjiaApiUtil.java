package com.ipath.orderflowservice.partnerapi.fanjia;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FanjiaApiUtil {

    public String getReqestBody(String appId, String bizReq) {
        PublicKey publicKey = SignatureUtil.getPublicKey();
        PrivateKey privateKey = SignatureUtil.getPrivateKey();
        Map<String, Object> data = new HashMap<>();
        data.put("appId", appId);
        data.put("nonceStr", RandomStringUtils.randomAlphanumeric(20));
        data.put("bizReq", bizReq);
        String sign = SignatureUtil.sign(data, privateKey);
        log.info("FanjiaApiUtil ===> sign={}" + sign);
        data.put("sign", sign);
        log.info("FanjiaApiUtil ===> body={}" + JSON.toJSONString(data));
        return JSON.toJSONString(data);
    }
}
