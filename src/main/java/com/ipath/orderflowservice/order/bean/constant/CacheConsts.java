package com.ipath.orderflowservice.order.bean.constant;

public class CacheConsts {

     //通用主数据信息
     public static final String REDIS_KEY_MD_SCENE = "icar:md:scene:{}"; // 场景信息，{}为sceneId
     public static final String REDIS_KEY_MD_COMPANY = "icar:md:company:{}"; // 公司信息，{}为companyId
     public static final String REDIS_KEY_MD_USER = "icar:md:user:{}"; // 用户信息，{}为userId
     public static final String REDIS_KEY_MD_COMPANY_USER_MD = "icar:md:company_user:{}"; // 公司用户信息，{}为userId
     public static final String REDIS_KEY_MD_EXTRA = "icar:md:extra";// 增值服务
     public static final String REDIS_KEY_MD_CARSOURCE = "icar:md:carsource";// 运力
 
     //配置数据缓存
     public static final String REDIS_KEY_CONFIG_COMPANY_SOURCE = "icar:config:company:source:{}";// 企业配置项-企业配置的可用运力
     public static final String REDIS_KEY_CONFIG_COMPANY_FREE_UPGRADE = "icar:config:company:freeupgrade:{}";// 企业配置项-免费升舱
     public static final String REDIS_KEY_CONFIG_COMPANY_PREPAY = "icar:config:company:prepay:{}";// 企业配置项-大额预付配置
     public static final String REDIS_KEY_CONFIG_SCENE_PARA = "icar:config:scene_para:{}";// 场景配置参数
     public static final String REDIS_KEY_CONFIG_CAR_TYPE_USER_COMPLETE_ORDER = "icar:config:company:car_type_lable:finish:user:{}";// 用户完单画像配置
     public static final String REDIS_KEY_CONFIG_MANUAL_H5_PERSONAL_PAY = "icar:config:manual:h5:personal_pay_by_tj";// h5客户超限额转个人支付
     public final static String REDIS_KEY_USER_CONFIG_GRAY = "icar:config:gray:";//灰度人员 按企业
     public static final String REDIS_KEY_COMMON_CONFIG_TAKE_DISTANCE = "icar:config:take_distance"; // 接单距离配置
     public static final String REDIS_KEY_CONFIG_IGNORE_LOG_REPORTING = "icar:order:config:ignore:log_reporting"; // 订单日志上报忽略的接口
     public static final String REDIS_KEY_ORDER_SOURCE_PRIOR_PREFIX = "icar:order:config:source:prior"; // 优先使用的运力code
     

     public static final String REDIS_KEY_CONFIG_COMPANY_USE_CAR = "icar:config:company:use_car:{}";// 企业配置项-用车配置项
     public static final String REDIS_KEY_CONFIG_COMPANY_ESTIMATEPRICERULE = "icar:config:company:estimate_rule:{}";// 企业配置项-预估规则
     public static final String REDIS_KEY_CONFIG_COMPANY_VIP = "icar:config:company:vip:{}";// 企业配置项-vip配置项
     public static final String REDIS_KEY_CONFIG_COMPANY_BASE = "icar:config:company:base:{}";// 企业配置项-基础设置
     public static final String REDIS_KEY_CONFIG_COMPANY_TAXFEE = "icar:config:company:taxFee:{}";// 企业配置项-税费
 
     //订单数据缓存
     public static final String REDIS_KEY_ORDER_MAPPING_ESTIMATE_TRACEID = "icar:order:mapping:estimate_to_trace:{}"; // 预估价id和traceid的映射，日志用
     public static final String REDIS_KEY_ORDER_MAPPING_ESTIMATE_PARA = "icar:order:mapping:estimate_to_para:{}"; // 预估参数参缓存 {}->预估id或者订单id
     public static final String REDIS_KEY_ORDER_ESTIMATE_RESULT = "icar:order:estimate:result:{}"; // 预估结果 {}->estiamte id
     public static final String REDIS_KEY_ORDER_PARA = "icar:order:placeOrder.para:{}"; // 下单参数，{}->order id
     public static final String REDIS_KEY_ORDER_LIMIT = "icar:order:limit:{}"; // 下单限制参数，key为 限制类型
     public static final String REDIS_KEY_ORDER_USER_UNPAID_ORDERS = "icar:order:user:unpaid:orders:{}";// 用户未支付订单 key为用户id
     public static final String REDIS_KEY_ORDER_H5_PERSONAL_PAY_LIMITED = "icar:order:pay:personal:limited:";// h5客户超限额转个人支付，限额缓存信息。":"后为 {userId}_{limitedType}
     public static final String REDIS_KEY_ORDER_H5_PERSONAL_PAY_AMOUNT = "icar:order:pay:personal:amount:";// h5客户超限额转个人支付，限额缓存信息。":"后为 {userId}_{yyyyMMdd}_{limitedType}
     public static final String REDIS_KEY_ORDER_CFG_IO_OPEN_PREFIX = "icar:order:config:open_io"; // 公司开启io验证
     public static final String REDIS_KEY_ORDER_CFG_IO_MAPPING_PREFIX = "icar:order:config:mapping"; // 公司io mapping AZ
     public static final String REDIS_KEY_ORDER_IO_MAPPING_PREFIX="order:io:mapping:"; //公司io mapping AZ

     //日志数据缓存
     public static final String REDIS_KEY_LOG_MAPPING_SERVICE_USER = "icar:log:mapping:service_to_user"; // 服务和对应负责人映射 item为服务编码

    /**
     * redis key 拼接前缀
     */

    public static final String REDIS_KEY_COMMON_COMPANY_INFO_PREFIX = "common:company:"; // 企业基本信息缓存
    public static final String REDIS_KEY_COMMON_CAR_SOURCE_PREFIX = "common:carSource:";// 运力信息缓存
    public static final String REDIS_KEY_COMMON_FEEDBACK_LABEL = "common:feedbackLabel:"; // 反馈标签
    public static final String REDIS_KEY_COMMON_FEENAME_LABEL = "common:feeName"; // 费用名称中文资源文件标签
    public final static String REDIS_KEY_COMMON_RESOUCE = "common:resource:";// 资源文件
    public final static String REDIS_KEY_COMMON_GRAY_TESTING_COMPANY = "common:config:gray:";// 灰度测试公司id,后面拼接灰度类型，比如开启结算灰度测试common:config:gray:settle

    public final static String REDIS_KEY_USER_LANGUAGE = "user:language";// 用户语言设置

    public static final String REDIS_KEY_ORDER_CLOSE_REPLACE_BOOKING_PREFIX = "order:closeReplaceBooking:"; // 公司关闭代叫车功能
    public static final String REDIS_KEY_ORDER_TRIP_OVERTIME_ADDRESS_PREFIX = "order:validateBusinessTrip:"; // 公司开启出差验证
                                                                                                             // 海目星
    public static final String REDIS_KEY_ORDER_USER_UNCONFIRMED_ABNORMAL_PREFIX = "order:abnormal:unconfirmed:"; // 用户未确认的异常订单
    public static final String REDIS_KEY_ORDER_SOURCE_PRIOR_EXCLUDE_PREFIX = "order:source:prior:exclude:"; // 由优选运力原因导致排除的平台code

    public static final String REDIS_KEY_CUSTOM_INFO_PREFIX = "order:custom:"; // 客户自定义信息缓存，key 为 userid 和 type

    public static final String REDIS_KEY_ORDER_FOR_REPORT_PREFIX = "order:report:"; // 下单时缓存订单进本信息供报表调用key

    public static final String REDIS_KEY_ORDER_PARAM_PREFIX = "order:param:"; // 下单参数 订单取消后缓存30分钟，供重新下单用
    public static final String REDIS_KEY_FEEDBACK_LABEL = "order:feedbackLabel"; // 反馈标签 老
    public static final String REDIS_KEY_CITIES_BY_CODE = "order:city:code";
    public static final String REDIS_KEY_CITIES_BY_NAME = "order:city:name";
    public static final String REDIS_KEY_CITIES_HUITONG_PREFIX = "order:city:huitong:"; // 慧通城市code，key为城市名
    public static final String REDIS_KEY_AIRPORT_PREFIX = "order:airport:"; // 机场坐标信息缓存，key为机场码
    public static final String REDIS_KEY_HISTORY_LOCATION_PREFIX = "order:location"; // 用户城市历史地点，key 为 userid 和 cityCode
    public static final String REDIS_KEY_USER_ORDER_PREFIX = "order:userId:"; // 按用户保存进行中的订单，以用户id为key，每个用户保存一个hash表存订单
    public static final String REDIS_KEY_ESTIMATE_PREFIX = "order:estimate:"; // 预价接口返回结果整理后的数据，key为随机数
    public static final String REDIS_KEY_ESTIMATE_CAR_TYPE_LABEL_PREFIX = "order:estimate:car_type_label:"; // 预价接口返回结果整理后的数据，key为随机数
    public static final String REDIS_KEY_ESTIMATE_PASSING_POINT_PREFIX = "order:estimate:passingPoints:"; // 预估途经点缓存
    public static final String REDIS_KEY_ESTIMATE_PASSING_POINT_SOURCE_PREFIX = "order:estimate:passingPoints:availableSource:"; // 预估途经点可用平台
    public static final String REDIS_KEY_ESTIMATE_ORDERID_PREFIX = "order:estimate:orderId:"; // 通过orderid获取预估id
    public static final String REDIS_KEY_EXTRA_SERVICE_PREFIX = "order:extraService:"; // 可用增值服务数据，key为serviceId
    public static final String REDIS_KEY_SCENE_INFO_PREFIX = "order:scene:"; // 场景信息
    public static final String REDIS_KEY_SCENE_PUBLISH_INFO_PREFIX = "order:scene:pub:"; // 场景信息，key为scenePublishId
    public static final String REDIS_KEY_PROJECT_PREFIX = "order:project:"; // 项目信息，key为projectId
    public static final String REDIS_KEY_CAR_SOURCE = "order:carSource"; // car source 中文名称映射
    public static final String REDIS_KEY_CAR_SOURCE_EN = "order:carSource:en"; // car source 英文名称映射
    public static final String REDIS_KEY_COMPANY_PLACE_ORDER_LIMIT = "order:limit:company:"; // 企业自定义用车限制
    public static final String REDIS_KEY_COMPANY_LIMIT_PARAM_MAPPING = "order:limit:mapping:company:"; // 企业自定义用车限制和param映射
    public static final String REDIS_KEY_COMPANY_ESTIMATE_DISCOUNT_LIMIT = "order:limit:estimate:discount"; // 企业预估价折扣配置
    public static final String REDIS_KEY_COMPANY_ESTIMATE_DISCOUNT_LOADED = "order:limit:estimate:loaded"; // 企业预估价折扣是否已经加载过缓存
    public static final String REDIS_KEY_ESTIMATE_PARA_PREFIX = "order:estimate.para:"; // 预估参数参缓存
    public static final String REDIS_KEY_PLACEORDER_PARA_PREFIX = "order:placeOrder.para:"; // 下单参数，key为 orderId
    public static final String REDIS_KEY_ICAR_ORDER_PREFIX = "order:iCarDcOrderId:"; // 以中台订单号为key保存对应userId和orderId
    public static final String REDIS_KEY_APPEND_PLACEORDER_SELECTED_CARS_PREPAY = "order:appendPlaceOrderSelectedCars.prepay:"; // 大额预付，追加的车型缓存
    public static final String REDIS_KEY_TRANSORDERNO_USERPAY_PREFIX = "order:transorderno:userpay:"; // 个人支付缓存
    public static final String REDIS_KEY_PREPAY_ORDER_PREFIX = "order:prepay:"; // 大额预付金额
    public static final String REDIS_KEY_ABNORMAL_PREFIX = "order:abnormal:company:"; // 企业合规预警设置
    public static final String REDIS_KEY_COMPANY_AVAILABLE_EXTRA_PREFIX = "order:extra:company:"; // 公司可用的增值服务
    public static final String REDIS_KEY_CALLBACK_CONFIG_PREFIX = "order:callback:company:"; // 企业回传配置
    public static final String REDIS_KEY_CALLBACK_CONFIG_LOAD_PREFIX = "order:load:callback:"; // 企业回传配置是否已经加载过
    public static final String REDIS_KEY_AUTH_CONFIG_PREFIX = "order:auth:config:";// 签名配置
    public static final String REDIS_KEY_COMPANY_AVAILABLE_CAR_SOURCE_PREFIX = "order:availableCarSource:";// 企业可用平台
    public static final String REDIS_KEY_COMPANY_USE_CAR_SETTING_PREFIX = "order:useCarSetting:";// 用车设置
    public static final String REDIS_KEY_BOOKING_OLD_ID_TO_NEW_ID_PREFIX = "order:booking:";// 预约管家老订单id对应新订单id
    public static final String REDIS_KEY_APPEND_ORDER_PREFIX = "order:append:";// 预约管家订单id
    public static final String REDIS_KEY_ORDER_STATUS = "order:status:";// orderId对应状态
    public static final String REDIS_KEY_HISTORY_ORDER_PREFIX = "order:history:";// 历史订单缓存key
    public static final String REDIS_KEY_ORDER_BASE = "order:orderbase:";// 订单基础表
    public static final String REDIS_KEY_ORDER_SOURCE = "order:ordersource:";// 订单子表
    public static final String REDIS_KEY_ORDER_PHONE_VIRTUAL = "order:phoneVirtual:";// 订单子表
    public static final String REDIS_KEY_DIC = "order:dic";// 字典表相关数据缓存
    public static final String REDIS_KEY_LANGUAGE = "user:language";// 字典表相关数据缓存
    public static final String REDIS_KEY_USER_DISPATCH_MODE = "user:dispatch:mode";// 派车模式
    public static final String REDIS_KEY_USER_INFO = "user:phone:";// 用户手机号
    public static final String REDIS_KEY_COMPANY_COMMON_CONFIG_PREFIX = "order:companyConfig:";// 公司配置项
    public static final String REDIS_KEY_CANCEL_ORDER_PREFIX = "order:cancel:";// 用户主动取消订单缓存


    /**
     * 订单状态历史
     */
    public static final String REDIS_KEY_ORDER_STATUS_HISTORY = "order:state:history:{}";

    public static final String REDIS_KEY_COMMONLY_USED_MEETING_USER = "order:meeting:commonlyUsed:user:";
    public static final String REDIS_KEY_USER_CUSTOM_INFO = "user:custominfo:"; // h5接入的客户，获取token时，携带的自定义信息

    public static final String REDIS_KEY_USER_LAST_COST_CENTER_INFO = "user:lastCostCenter:"; // 用户上一次选择的成本中心

    public static final String REDIS_KEY_REPORT_EVENT_INFO_PREFIX = "common:event:"; // event信息

    // 预估价
    public static final String REDIS_KEY_ESTIMATE_ORIGINAL_PREFIX = "icar:order:estimate:original:"; // 中台返回的原始预估价

    public static final String REDIS_KEY_Default_ACCOUNT_PREFIX = "icar:order:account"; // 公司默认账户

    public static final String REDIS_KEY_COMPANY_CONFIG_PREFIX = "icar:config:company:"; // 公司配置缓存key

    public static final String REDIS_KEY_COMPANY_CAR_TYPE_LABEL_ESTIMATE = "icar:config:company:car_type_lable:estimate:{}"; // 企业配置项:  预估车型标签配置项
    public static final String REDIS_KEY_CAR_TYPE_LABEL_ENABLE = "icar:config:cartypelabel:enable:{}"; // 企业配置项:  预估车型标签配置项

    public static final String REDIS_KEY_COMPANY_CAR_TYPE_LABEL_ESTIMATE_USER = "icar:config:company:car_type_lable:estimate:user:{}"; // 标签车型,预估用户画像

    public static final String REDIS_KEY_COMPANY_CAR_TYPE_LABEL_ORDER = "icar:config:template:order:{}"; // 标签车型,完单用户画像
    public static final String REDIS_KEY_COMPANY_CAR_TYPE_LABEL_CITY_ENABLE = "icar:config:cartypelabel:city:enable:{}"; // 标签车型,完单用户画像
    public static final String REDIS_KEY_BASE_INFO = "icar:carbaseinfo";
    public static final String REDIS_KEY_SOURCE_INFO = "icar:carsourceinfo";
    public static final String REDIS_KEY_CAR_LEVEL_INFO = "icar:carlevelinfo";

    /**
     * 新-标签车型缓存配置
     */
    public static final String REDIS_KEY_CAR_TYPE_LABEL_ESTIMATE_USER = "icar:config:car_type_label:estimate:user:{}"; // 标签车型-预估画像-用户id维度
    public static final String REDIS_KEY_CAR_TYPE_LABEL_ESTIMATE_USER_SIMPLE = "icar:config:car_type_label:estimate:company:user:{}"; // 标签车型-预估画像-用户维度
    public static final String REDIS_KEY_CAR_TYPE_LABEL_ESTIMATE_COMPANY = "icar:config:car_type_label:estimate:company:{}"; // 标签车型-预估画像-企业维度

    public static final String REDIS_KEY_CAR_TYPE_LABEL_FINISH_USER = "icar:config:car_type_label:finish:user:{}"; // 标签车型-完单画像-企业维度
    public static final String REDIS_KEY_CAR_TYPE_LABEL_FINISH_USER_SIMPLE = "icar:config:car_type_label:finish:company:user:{}"; // 标签车型-完单画像-用户维度
    public static final String REDIS_KEY_CAR_TYPE_LABEL_FINISH_COMPANY = "icar:config:car_type_label:finish:company:{}"; // 标签车型,完单用户画像, 企业维度

    public static final String REDIS_KEY_CHANGE_DEST_ESTIMATE_DYNAMIC_CODE = "icar:order:change_dest_dynamic_code:{}"; // 变更目的地预估动态码

    public static final String REDIS_KEY_EXTRAL_FEE_CODES = "icar:config:manual:abnormal_order:extral_fee_codes";

    public static final String REDIS_KEY_CHANGE_DEST_ESTIMATE = "icar:config:change_dest_estimate:{}";



    public static final Long ONE_HOUR_CACHE_EXPIRE_TIME = 3600 * 1L; // 缓存1小时
    public static final Long THIRTY_MINUTE_CACHE_EXPIRE_TIME = 1800 * 1L; // 缓存半小时
    public static final Long TEMP_CACHE_EXPIRE_TIME = 60L; // 临时1分钟
    public static final Long ORDER_CACHE_EXPIRE_TIME = 3600 * 24L; // 订单过期时间24小时
    public static final Long ORDER_ESTIMATE_CACHE_EXPIRE_TIME = 1800L; // 预估结果过期时间半小时
    public static final Long USERPAY_CACHE_EXPIRE_TIME = 3600 * 4L; // 个人支付过期时间4小时
    public static final Long BOOKING_CACHE_EXPIRE_TIME = 3600 * 5 * 24L; // 5天
    public static final Long STABLE_CACHE_EXPIRE_TIME = 3600 * 365 * 24L; // 相对稳定不变的数据缓存时间
    public static final Long TEN_SECOND = 10 * 1L; // 10秒
    public static final Long ONE_MINUTE = 60 * 1L; // 1分钟
    /**
     * 5分钟
     */
    public static final Long FIEVE_MINUTE = 5 * 60 * 1L; // 5分钟
    public static final Long TEN_MINUTE = 10 * 60 * 1L; // 10分钟
    public static final Long ONE_HOUR = 60 * 60 * 1L; // 1小时
    public static final Long ONE_DAY = 24 * 60 * 60 * 1L; // 1天
    public static final Long ONE_WEEK = 7 * 24 * 60 * 60 * 1L; // 1周
    public static final Long ONE_MONTH = 30 * 24 * 60 * 60 * 1L; // 1月
    public static final Long ONE_YEAR = 365 * 24 * 60 * 60 * 1L; // 1年

    public static final Long HOUR_FOUR_CACHE_EXPIRE_TIME = 60 * 60 * 4 * 1L; // 缓存4小时

}
