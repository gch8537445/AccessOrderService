package com.ipath.orderflowservice.order.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.order.bean.constant.CompanyConstant;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.dao.bean.OrderBase;
import com.ipath.orderflowservice.order.service.CipherService;
import com.ipath.orderflowservice.order.service.OrderLimitService;
import com.ipath.orderflowservice.order.util.CipherUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.message.AuthException;
import java.util.ArrayList;
import java.util.List;

/**
 * 加密业务 service
 */
@Service
@Slf4j
public class CipherServiceImpl implements CipherService {

    @Value("${aes.address-key}")
    private String key;

    @Autowired
    private RedisUtil redisUtil;


    @Autowired
    private OrderLimitService orderLimitService;

    private static List<Long> isOpenAddressCipherCompanyIds;

    //============================ 地址 解密与解密 start <<<<<< ==============================


    /**
     * 刷新缓存地址 解密与解密 公司
     *
     * @return
     */
    @PostConstruct
    @Override
    public List<Long> refreshCacheIsOpenAddressCipherCache() {
        isOpenAddressCipherCompanyIds = new ArrayList<>();
        redisUtil.delete(CompanyConstant.COMPANY_ONOFF_ADDRESS_CIPHER);
        List<Long> redisCacheCompanys = orderLimitService.getRedisCacheCompanys(
                CompanyConstant.COMPANY_ONOFF_ADDRESS_CIPHER,
                CompanyConstant.COMPANY_ONOFF_ADDRESS_CIPHER_TYPE);
        log.info("cacheIsOpenAddressCipherCache ===> {}", redisCacheCompanys);
        isOpenAddressCipherCompanyIds = redisCacheCompanys;
        return redisCacheCompanys;
    }

    /**
     * 判断公司是否需要加解密地址
     *
     * @param companyId
     * @return
     */
    @Override
    public boolean isOpenAddressCipher(Long companyId) {

        if (isOpenAddressCipherCompanyIds.contains(companyId)) {
            return true;
        } else {
            boolean openCompany = orderLimitService.isOpenCompany(
                    CompanyConstant.COMPANY_ONOFF_ADDRESS_CIPHER,
                    CompanyConstant.COMPANY_ONOFF_ADDRESS_CIPHER_TYPE,
                    companyId);
            if(openCompany){
                isOpenAddressCipherCompanyIds.add(companyId);
            }
            return openCompany;
        }
    }


    //CreateOrderParam地址加密
    @Override
    public void addressEncryptForCreateOrderParam(CreateOrderParam createOrderParam) {
        Long companyId = createOrderParam.getCompanyId();
        createOrderParam.setPickupLocation(this.addressEncrypt(companyId, createOrderParam.getPickupLocation()));
        createOrderParam.setPickupLocationName(this.addressEncrypt(companyId, createOrderParam.getPickupLocationName()));
        createOrderParam.setDestLocation(this.addressEncrypt(companyId, createOrderParam.getDestLocation()));
        createOrderParam.setDestLocationName(this.addressEncrypt(companyId, createOrderParam.getDestLocationName()));
    }

    @Override
    public void addressEncryptForLocationJSONObject(Long companyId, JSONObject location) {
        if(null == location){
            return;
        }

        JSONObject startLocation = location.getJSONObject("startLocation");
        JSONObject endLocation = location.getJSONObject("endLocation");

        if(null != startLocation){
            String address = this.addressEncrypt(companyId, startLocation.getStr("address"));
            String name = this.addressEncrypt(companyId, startLocation.getStr("name"));
            startLocation.set("address",address);
            startLocation.set("name",name);
        }

        if(null != endLocation){
            String address = this.addressEncrypt(companyId, endLocation.getStr("address"));
            String name = this.addressEncrypt(companyId, endLocation.getStr("name"));
            endLocation.set("address",address);
            endLocation.set("name",name);
        }
    }

    //OrderBase地址解密
    @Override
    public void addressDecryptOrderBase(OrderBase orderBase) {
        Long companyId = orderBase.getCompanyId();
        orderBase.setPickupLocation(this.addressDecrypt(companyId, orderBase.getPickupLocation()));
        orderBase.setPickupLocationName(this.addressDecrypt(companyId, orderBase.getPickupLocationName()));
        orderBase.setDestLocation(this.addressDecrypt(companyId, orderBase.getDestLocation()));
        orderBase.setDestLocationName(this.addressDecrypt(companyId, orderBase.getDestLocationName()));
    }

    @Override
    public void addressEncryptOrderBase(OrderBase orderBase) {
        if(null == orderBase){
            return;
        }
        Long companyId = orderBase.getCompanyId();
        orderBase.setPickupLocation(this.addressEncrypt(companyId, orderBase.getPickupLocation()));
        orderBase.setPickupLocationName(this.addressEncrypt(companyId, orderBase.getPickupLocationName()));
        orderBase.setDestLocation(this.addressEncrypt(companyId, orderBase.getDestLocation()));
        orderBase.setDestLocationName(this.addressEncrypt(companyId, orderBase.getDestLocationName()));
    }


    //地址加密
    @Override
    public String addressEncrypt(Long companyId, String value) {
        if (StringUtils.isNotBlank(value) && this.isOpenAddressCipher(companyId)) {
            String result = "";
            try {
                result = CipherUtil.encrypt(value, key);
            } catch (Exception e) {
                log.error("addressEncryptService ===> 异常：{}", e);
            }
            return result;
        }
        return value;
    }

    //地址解密
    @Override
    public String addressDecrypt(Long companyId, String value) {
        //log.error("addressDecrypt ===> 解密参数 需要解密公司:{}, companyId：{}，value：{}",JSONUtil.toJsonStr(isOpenAddressCipherCompanyIds), companyId,value);
        if (StringUtils.isNotBlank(value) && this.isOpenAddressCipher(companyId)) {
            String result = "";
            try {
                result = CipherUtil.decrypt(value, key);
                //log.error("addressDecrypt ===> 解密结果 companyId：{}，value：{}", companyId,result);
                return result;
            } catch (Exception e) {
                log.error("addressDecrypt ===> 解密参数 需要解密公司:{}, companyId：{}，value：{}",JSONUtil.toJsonStr(isOpenAddressCipherCompanyIds), companyId,value);
                log.error("addressDecryptService ===> 异常：{}", e.getMessage());
            }
            if(StringUtils.isNotBlank(value) && StringUtils.isBlank(result)){
                return value;
            }
        }
        //log.error("addressDecrypt ===> 不需要解密 companyId：{}，value：{}", companyId,value);
        return value;
    }
    //============================ 地址 解密与解密 end <<<<<< ==============================


}
