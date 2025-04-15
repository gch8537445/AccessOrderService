package com.ipath.orderflowservice.order.bean.constant;

/**
 * 中台订单服务相关配置
 */
public class CoreOrderConstant {

    /**
     * 下单参数 extraParams
     */
    // 是否允许重新下单,平台取消不通知前端,需要主动取消 默认false 是：true 否：false
    public static final String RE_PLACE_ORDER = "re_place_order";
    // 是否勾选绿色出行 默认false 是：true 否：false
    public static final String GREEN_TRAVEL = "green_travel";
    // 不允许接单车牌号 多个逗号分割
    public static final String EXCLUDE_VEHICLE_NO = "exclude_vehicle_no";
    // 仅限新能源 默认false 是：true 否：false
    public static final String GREEN_ONLY = "green_only";
    // 派车模式 默认是1 1-智能模式; 2-距离优先; 3-价格优先; 4-响应优先
    public static final String ORDER_TAKEN_MODE = "order_taken_mode";
    // 毫秒,多司机接单的门槛时间,超过这个时间不启动多司机接单,默认值30000
    public static final String THRESHOLD_TIME = "threshold_time";
    // 是否允许非握手运力参与VIP用户的优选司机
    public static final String JOIN_NON_HANDSHAKE_DRIVERS = "join_non_handshake_drivers";
    // 中台回调通知标识
    public static final String GRAY_SCALE = "grayscale";
    // 中台服务灰度标识
    public static final String PRE_ENV_KEY = "pre-env";

    public static final String PRE_ENV_VALUE="grayscale_environment";
    /**
     * 下单勾选双选司机
     */
    public static final String USER_SELECTION_DRIVERS="user_selection_drivers";


}
