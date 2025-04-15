package com.ipath.orderflowservice.order.service;

import cn.hutool.json.JSONObject;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.dao.bean.OrderBase;

import javax.annotation.PostConstruct;
import java.util.List;

public interface CipherService {


    List<Long> refreshCacheIsOpenAddressCipherCache();

    boolean isOpenAddressCipher(Long companyId);

    //CreateOrderParam地址加密
    void addressEncryptForCreateOrderParam(CreateOrderParam createOrderParam);

    //LocationJSONObject地址加密
    void addressEncryptForLocationJSONObject(Long companyId, JSONObject location);

    //OrderBase地址解密
    void addressDecryptOrderBase(OrderBase orderBase);

    //OrderBase地址加密
    void addressEncryptOrderBase(OrderBase orderBase);

    //地址加密
    String addressEncrypt(Long companyId, String value);

    //地址解密
    String addressDecrypt(Long companyId, String value);
}
