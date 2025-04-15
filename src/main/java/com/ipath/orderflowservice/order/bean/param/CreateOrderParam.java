package com.ipath.orderflowservice.order.bean.param;

import java.util.Date;
import java.util.List;
import java.io.Serializable;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;


@Data
public class CreateOrderParam implements Serializable {
    private Long orderId;                       // 订单号，修改目的地时上传
    private String dynamicCode;                 // 预估动态码，修改目的地时上传
    private Long userId;                        // 用户id
    private Long companyId;                     // 公司id
    private Long accountId;                     // 账户id
    private Short serviceType;                  // 服务类型
    private String passengerName;               // 乘客姓名
    private String passengerPhone;              // 乘客手机号
    private Long couponId;                      // 优惠券id
    private String departCityCode;
    private String departCityName;
    private String departLat;
    private String departLng;
    private String pickupLocationName;          // 出发地名称
    private String pickupLocation;              // 出发地详细地址
    private String destCityCode;
    private String destCityName;
    private String destLat;
    private String destLng;
    private String departPoi;
    private String departStationType;
    private String destPoi;
    private String destStationType;
    private String destLocationName;            // 目的地名称
    private String destLocation;                // 目的地详细地址
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date departTime;                    // 出发时间(yyyy-MM-dd HH:mm:ss)
    private String estimateId;                  // 估价结果redis缓存key
    private Integer distance;                   // 估计距离（米，整数）
    private Integer estimateTime;               // 估计时间（秒，整数）
    private String flightNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date flightArrivalTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date flightDepartTime;
    private String flightDepartAirportCode;
    private String flightArrivalAirportCode;
    private List<SelectedCar> cars;             // 选择的车型数组
    private List<ExtraService> extraServices;   // 增值服务id和code 数组
    private OrderExtraParameter extraParameter; // 附加参数json，目前有字段: usedReason 用车原因
    private Boolean confirm;                    // 是否已确认，true则不检查是否重复订单
    private String orderParams;                 // 原始下单参数base64，前端用的
    private String trainDep;
    private String trainArr;
    private String trainNumber;                 // 列车号
    /**
     * 行前审批单主键
     */
    private Long preDepartApplyId;

    /**
     * 行前审批单code
     */
    private String preDepartApplyCode;

    /**
     * 预估过期时间
     */
    private Date estimateExpireTime;
    /**
     * 大额预付标识
     * true:大额预付 false or null :非大额预付
     */
    private Boolean prePayFlag;
    /**
     * 大额预付已付金额
     */
    private BigDecimal prePayAmount;
    /**
     * 大额预付的支付渠道
     */
    private String payType;
    /**
     * 追加车型预付
     */
    private Boolean appendPrePayFlag;
    /**
     * 用户中文名称
     */
    private String nameCn;
    /**
     * 紧急联系电话
     */
    private String emergencyPhone;
    /**
     * 用户电话
     */
    private String userPhone;
    /**
     * 用车原因
     */
    private String useCarReason;
    /**
     * 场景id
     */
    private Long sceneId;
    /**
     * 场景发布id
     */
    private Long scenePublishId;
    /**
     * 场景英文名
     */
    private String sceneCode;
    /**
     * 场景中文名
     */
    private String sceneNameCn;
    /**
     * 场景英文名
     */
    private String sceneNameEn;
    /**
     * 项目id
     */
    private Long projectId;

    private String partnerOrderId;

    private Short charteredBusType;

    /**
     * 途经点
     * "passingPoints":[
     * {
     * "lat":"39.915978",
     * "lng":"116.433631",
     * "name":"梓峰大厦"
     * },
     * {
     * "lat":"39.919155",
     * "lng":"116.44236",
     * "name":"日坛国际贸易中心"
     * }
     * ]
     */
    private JSONArray passingPoints;

    private Boolean isUserPay;

    private String isNotUserPayReason;

    private String customInfo;

    /**
     * 会议id
     */
    private Long meetingId;

    /**
     * 审批人
     */
    private Long approverId;


}
