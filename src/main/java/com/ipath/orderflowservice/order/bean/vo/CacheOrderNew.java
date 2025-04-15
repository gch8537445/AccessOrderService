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

/**
 * 订单缓存对象
 */
@Data
public class CacheOrderNew {
    /**订单id */
    private Long id;

    /**中台订单id */
    private Long coreOrderId;

    /**预估id */
    private String estimateId;

    /**用车时间(yyyy-MM-dd HH:mm:ss) */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date departTime;

    /**服务类型 1-实时 2-预约 6-接机 7-接站 20-包车*/
    private int serviceType;

    /**下单人姓名 */
    private String userName;

    /**下单人手机号 */
    private String userPhone;

    /**乘客姓名 */
    private String passengerName;

    /**乘客手机号 */
    private String passengerPhone;

    /**出发地城市名称 */
    private String departCityName;

    /**出发地城市code */
    private String cityCode;

    /**出发地名称 */
    private String pickupLocationName;

    /**目的地城市名称 */
    private String destCityName;

    /**目的地城市code */
    private String destCityCode;

    /**目的地名称 */
    private String destLocationName;
    
    /**选择的车型数组 */
    private List<SelectedCar> cars;

    /**原始下单参数base64，前端用的 */
    private String orderParams;

    /**状态 */
    private Short state;

    /**运力名称-中文 */
    private String sourceNameCn;

    /**运力名称-英文 */
    private String sourceNameEn;

    /**司机信息 */
    private DriverInfo driverInfo;

    /**用户选择的车型信息 */
    private List<CacheEstimateResult> callingCars;

    /**用户选择的标签车型信息 */
    private List<CarTypeLabelEstimateVo> callingCarTypeLabelCars;

    /**订单使用的增值服务id数组 */
    private List<Long> extraServiceIds;

    /**订单使用的增值服务code数组 */
    private List<String> extraServiceCodes;

    /**订单使用的优惠券id数组 */
    private List<Long> couponIds;

    /**运力code */
    private Integer carSource;

    /**运力车型code */
    private Integer carType;

    /**运力车型名称 */
    private String carTypeName;

    /**途径车型code */
    private Integer carLevel;

    /**司机接单时间 */
    private Date driverPickOrderTime;

    /**司机到达时间 */
    private Date driverArrivedTime;

    /**行程开始时间 */
    private Date travelBeginTime;

    /**行程结束时间 */
    private Date travelEndTime;

    /**行驶距离（米） */
    private int travelDistance;

    /**行驶时间（秒） */
    private int travelDuration;
}
