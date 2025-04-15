package com.ipath.orderflowservice.order.bean.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;

import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.CarTypeLabelEstimateVo;
import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;


// 缓存到redis中的订单信息
@Data
public class CacheOrder {
    private Long orderId;
    private String passengerName;               // 乘客姓名
    private String passengerPhone;               // 乘客手机号
    private String departCityName;
    private String pickupLocationName;          // 出发地名称
    private String destLocationName;            // 目的地名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date departTime;                    // 出发时间(yyyy-MM-dd HH:mm:ss)
    private String estimateId;                  // 估价结果redis缓存key
    private List<SelectedCar> cars;             // 选择的车型数组
    private String orderParams;                 // 原始下单参数base64，前端用的
    private String coreOrderId;
    private Short state;
    private String sourceNameCn;
    private String sourceNameEn;
    private DriverInfo driverInfo;
    private List<CacheEstimateResult> callingCars; // 保存预估结果中用户选择的车型信息
    private List<CarTypeLabelEstimateVo> callingCarTypeLabelCars; // 保存预估结果中用户选择的车型信息
    //增值服务id数组
    private List<Long> extraServiceIds;
    //增值服务code数组
    private List<String> extraServiceCodes;
    //优惠券Ids
    private List<Long> couponIds;
    //订单所在城市名称
    private String cityName;
    //订单所在城市code
    private String cityCode;
    //订单所在城市id
    private Long cityId;

    private Integer carSource;
    private Integer carType;
    private String carTypeName;
    private Date driverArrivedTime;
    private Integer carLevel;
    private Date driverPickOrderTime;
    private Date beginChargeTime;
    private Date driveCarTime;
    private Date orderTime;
    private Date finishTime;
    private Date payTime;
    private Long normalDistance;
    private Object actualStartLocation;
    private Object actualEndLocation;
    private Map<String, String> preApply;
    /**
     * 服务类型
     * 1：实时 2：预约 6：接机
     */
    private int serviceType;
}
