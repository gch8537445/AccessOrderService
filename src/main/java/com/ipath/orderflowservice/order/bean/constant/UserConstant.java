package com.ipath.orderflowservice.order.bean.constant;

/**
 * 用户相关配置
 */
public class UserConstant {

    /**
     * 缓存key
     */
    //用户取消订单的车牌号集合
    public static final String USER_ORDER_CANCEL_VEHICLENOS = "user:order:cancel:vehiclenos";

    //获取用户提示信息
    public static final String USER_ORDER_MESSAGES = "user:order:messages";


    //提示文案
    public static final String USER_ORDER_MESSAGE_OPTIMAL_DRIVER = "提示当前附近司机较少，已为您匹配最优司机";

    //用户最近输入的乘车人
    public static final String USER_LATELY_PASSENGER = "user:lately:passenger";
}
