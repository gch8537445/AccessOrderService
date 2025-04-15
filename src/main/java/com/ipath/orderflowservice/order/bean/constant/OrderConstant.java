package com.ipath.orderflowservice.order.bean.constant;

/**
 * 订单
 */
public class OrderConstant {


    //判断异常信息长度最大值
    public static final int ERORR_MSG_MAX_LENGTH = 60;
    /**
     *  异常提示
     */

    public static final String ERORR_SYSTEM_BUSY = "网络繁忙，请稍后重试";
    public static final String ERORR_SERVICE_EXCEPTION = "服务异常，如遇使用问题可拨打客服热线：400-606-0647";
    public static final String ERORR_HTTPMESSAGENOTREADABLEEXCEPTION = "请求参数错误";
    public static final String ERORR_ILLEGALARGUMENTEXCEPTION = "内部参数错误";


    /**
     * 异常邮件标题
     */
    public static final String ERORR_ALARMTITLE_NULLPOINTEREXCEPTION = "订单服务>>空指针异常";
    public static final String ERORR_ALARMTITLE_ILLEGALARGUMENTEXCEPTION = "订单服务>>内部参数错误";
    public static final String ERORR_ALARMTITLE_BADSQLGRAMMAREXCEPTION = "订单服务>>数据库异常";
    public static final String ERORR_ALARMTITLE_EXTERNSYSTEMERRORCODEEXCEPTION = "订单服务>>调用外部系统返回错误编码";
    public static final String ERORR_ALARMTITLE_OTHEREXCEPTION = "订单服务>>其他异常";
    public static final String ERORR_ALARMTITLE_BUSINESSEXCEPTION = "订单服务>>业务异常";
    public static final String ERORR_ALARMTITLE_CustomException = "订单服务>>自定义code异常";

    /**
     *  校验提示
     */
    public static final String NOT_FOUND_CITY = "未找到城市。";
    public static final String NOT_FOUND_ORDER = "未找到订单";
    public static final String IS_EMPTY_ORDERID = "订单id为空";
    public static final String IS_EMPTY_SCENEID = "没有场景id";
    public static final String IS_EMPTY_DESTPARAMS = "缺少终点参数";
    public static final String IS_EMPTY_PICKUPPARAMS = "缺少起点参数";

    public static final String IS_ERORR_PARAMS_departTime = "预约用车时间参数不正确";

    //缓存 订单标记
    //同一司机需要重新派单的订单
    public static final String ORDER_IS_RE_PLACE_ORDER = "order:is:re:place:order:";

    //开启重新派单的订单
    public static final String ORDER_IS_OPEN_RE_PLACE_ORDER = "order:is:open:re:place:order:";

    public static final String ORDER_IS_SETTLING = "order:is:settling:";
}
