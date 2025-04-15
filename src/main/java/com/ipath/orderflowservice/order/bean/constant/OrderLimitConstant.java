package com.ipath.orderflowservice.order.bean.constant;

/**
 * 订单限制
 */
public class OrderLimitConstant {

    /**
     * 缓存统一前缀
     *
     */
    public static final String REDIS_KEY_PREFIX="order:limit:";
    /**
     * 订单限制 类型
     *
     */
    //只允许新能源车型接单
    public static final String TYPE_ISONLYALLOWGREENTRAVEL="isOnlyAllowGreenTravel";
    //推荐上车地址范围限制半径
    public static final String COMPANY_CONFIG_RECOMMENDED_LOCATION_RADIUS = "recommendedLocationRadius";
    // 推荐上车地址配置
    public static final String COMPANY_CONFIG_RECOMMENDED_LOCATION = "companyConfigRecommended";

}
