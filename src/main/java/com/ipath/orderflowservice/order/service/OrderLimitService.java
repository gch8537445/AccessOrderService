package com.ipath.orderflowservice.order.service;


import com.ipath.orderflowservice.order.bean.RedisCpol;
import com.ipath.orderflowservice.order.bean.bo.CompanyOrderLimit;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.param.KeyValue;
import com.ipath.orderflowservice.order.bean.vo.OrderLimitConfigValueCompanyVo;
import com.ipath.orderflowservice.order.dao.bean.OrderLimitConfig;
import com.ipath.orderflowservice.order.dao.vo.CompanyLimitVo;

import java.math.BigDecimal;
import java.util.List;

public interface OrderLimitService {

    String delRedisKey(Long companyId, String key);

    /**
     * 获取设置了下单限制的公司列表
     *
     * @return
     */
    List<Long> getOrderLimitCompanys();

     /**
     * 获取设置了一次跳转只允许下一单限制的公司列表
     *
     * @return
     */
    List<Long> getOrderLimitOneH5Companys();


    List<Long> checkRunningOneOrderCompanys();

    /**
     * 判断公司是否开启下单限制配置
     *
     * @param companyId
     * @return
     */
    boolean isOpenOrderLimitConfig(Long companyId);

    /**
     * 获取下单限制公司配置(通过公司id)
     *
     * @param companyId
     * @return
     */
    OrderLimitConfigValueCompanyVo getOrderLimitConfig(Long companyId);

    /**
     * 获取redis 存储的下单限制
     * @param companyId
     * @param userId
     * @return
     */
    RedisCpol getRedisCpol(Long companyId, Long userId);

    /**
     * 保存redis 存储的下单限制
     * @param redisCpol
     * @param companyId
     * @param userId
     * @return
     */
    RedisCpol saveRedisCpol(RedisCpol redisCpol,Long companyId, Long userId);


    /**
     * 获取企业自定义限制信息(通过公司id,用户id)
     * @param companyId
     * @param userId
     * @return
     */
    List<CompanyLimitVo> getCompanyLimitList(Long companyId, Long userId, String departCityCode,CreateOrderParam createOrderParam,List<KeyValue> customLimitMapping);

    /**
     * 获取企业自定义限制信息映射
     * @param companyId
     * @param userId
     * @param departCityCode
     * @return
     */
    List<KeyValue> getCustomLimitMapping(Long companyId, Long userId, String departCityCode,CreateOrderParam createOrderParam);

    /**
     * 判断当前用户是否有进行中的订单（配置了下单限制的公司）
     * 说明：下单限制的客户，每次登录最多下一单，切进行中的订单只有一笔，目前通过缓存获取进行中的订单
     *
     * @param companyId 公司id
     * @param userId    用户id
     * @return true-存在 false-不存在
     */
    boolean existsRunningOrders(Long companyId, Long userId);

    /**
     * 是否开启费用配置
     * @param companyId
     * @return
     */
    boolean isOpenSettingFee(Long companyId);

    /**
     * 获取设置了免费升舱的公司列表
     *
     * @return
     */
    List<Long> getUpgradeCarLevlCompanys();

    /**
     * 判断公司是否开启了免费升舱（根据公司id）
     *
     * @return
     */
    boolean isOpenUpgradeCarLevlCompanys(Long companyId);

    /**
     * 获取设置了大额预付的公司列表
     *
     * @return
     */
    List<Long> getBigAmountPrePayCompanys();

    /**
     * 判断公司是否开启了大额预付（根据公司id）
     *
     * @return
     */
    boolean isOpenBigAmountPrePayCompanys(Long companyId);

    /**
     * 获取设置了城市转换的公司列表
     *
     * @return
     */
    List<Long> getCityConvertCompanys();

    /**
     * 判断公司是否开启了城市转换（根据公司id）
     *
     * @return
     */
    boolean isOpengetCityConvertCompanys(Long companyId);



    /**
     * 城市转换
     * @param companyId
     */
    cn.hutool.json.JSONObject getCityConvertMapping(Long companyId);

    /**
     * 获取设置了下单通知配置的公司列表
     *
     * @return
     */
    List<Long> getCompanyPlaceorderNotifyCompanys();

    /**
     * 判断公司是否开启了下单通知配置（根据公司id）
     *
     * @return
     */
    boolean isOpengetCompanyPlaceorderNotify(Long companyId);

    /**
     * 获取公司下单通知配置是否开启
     *
     * @return
     */
    boolean getCompanyPlaceorderNotifyConfig(CreateOrderParam orderParam, String type);

    boolean isOpenUnpaidOrderCompanys(Long companyId);

    boolean isOpenNeedPayNotifyCompanys(Long companyId);
    /**
     * 判断公司是否开启下单关联第三方id
     * @param companyId
     * @return
     */
    boolean isOpenPartnerOrderId(Long companyId);

    String getPartnerOrderIdCode(Long companyId);

    /**
     * 判断公司是否开启预估自定义显示
     * @param companyId
     * @return
     */
    boolean isOpenEstimateCustomDisplay(Long companyId);

    /**
     * 预估自定义显示配置
     *
     * @param companyId
     * @return
     */
    String getEstimateCustomDisplay(Long companyId);

    boolean isOpenOrderInfoCustomDisplay(Long companyId);

    String getOrderInfoCustomDisplay(Long companyId);

    /**
     * 审批单列表自定义
     * @param companyId
     * @return
     */
    String getApprovalListCustomDisplay(Long companyId);

    boolean isOpenApprovalListCustomDisplay(Long companyId);

    boolean isOpenCompany(String key, String type, Long companyId);

    List<Long> getRedisCacheCompanys(String key, String type);

    boolean isOpenCompanyOrderLimitByType(Long companyId, String type);

    OrderLimitConfig getCompanyOrderLimitByType(Long companyId, String type);

    List<CompanyOrderLimit> getCompanyOrderLimitAll();

    List<CompanyOrderLimit> getCompanyOrderLimitAllRe();

    List<Long> getRedisCacheCompanyIdListByType(String type);

    String getRedisCacheValueByType(String type, Long companyId);

    /**
     * 获取redis缓存限制配置表中的公司的配置（根据配置类型与公司id）
     *
     * @param key  缓存key
     * @param type 配置类型
     * @param companyId 公司id
     * @return
     */
    OrderLimitConfig getRedisCacheCompanyConfig(String key, String type, Long companyId);

    /**计算阶梯费用
     *
     * @param companyId
     * @param amount
     * @return
     */
    BigDecimal getCompanyTieredfee(Long companyId, BigDecimal amount);

    /**
     * 判断公司是否开启了大额预付（根据公司id）
     *
     * @return
     */
    boolean isOpenShowBillCompanys(Long companyId);

    /**
     * 判断公司是否异常订单确认通知报表（根据公司id）
     *
     * @return
     */
    boolean isOpenAnbnormalConfirmedNotifyReport(Long companyId);

    /**只允许新能源车型接单
     *
     * @param companyId
     * @return
     */
    boolean isOnlyAllowGreenTravel(Long companyId);

    void deleteCustomInfo(Long companyId, Long userId);

    /**
     * 是否开启 机场跨城映射
     * @return
     */
    boolean isOpenAirportCrossCityMapping(Long companyId);

    /**
     * 获取 机场跨城映射
     * @return
     */
    String getAirportCrossCityMapping(Long companyId);

    boolean checkAirportCrossCityMapping(Long companyId, CreateOrderParam createOrderParam) ;

    boolean isOpenCancelFeeToWaitPay(Long companyId);
}
