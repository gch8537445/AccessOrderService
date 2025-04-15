package com.ipath.orderflowservice.order.bean.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.business.dispatch.bean.vo.OrderDispatchConfigVo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderBaseInfoParam implements Serializable{
    private Long id;

    private Long companyId;

    private Long accountId;

    private Long sceneId;

    private Long projectId;

    private Long userId;

    private Short serviceType;

    private String carSources;

    private String departCityCode;

    private String departCityName;

    private String pickupLocation;

    private String pickupLocationName;

    private String departLat;

    private String departLng;

    private String destCityCode;

    private String destCityName;

    private String destLocation;

    private String destLocationName;

    private String destLat;

    private String destLng;

    private String passengerName;

    private String passengerPhone;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date departTime;

    private String flightNumber;

    private String flightDepartTime;

    private String flightDelayTime;

    private String departAirportCode;

    private String arrivalAirportCode;

    private Boolean isSendPassengerSms;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date updateTime;

    private Short state;

    private String useCarReason;

    private Boolean isNeedApprove;

    private Long scenePublishId;

    private String sceneNameCn;

    private String sceneNameEn;

    private String remark;

    private Boolean allowChangeDest;

    private Boolean allowChangeDestScene;

    private Long workFlowId;

    private String userPhone;

    private String departPoi;

    private String destPoi;

    private static final long serialVersionUID = 1L;

    private Boolean isPrepay;

    private String customInfo;

    private String cancelReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date cancelTime;

    private Long preDepartApplyId;

    private Boolean isUpgrade;

    private String coreOrderId;

    private Boolean isChangeDest;

    private Boolean isAppend;

    private Boolean isGreenTravel;
    private Boolean isTickedGreenTravel;
    private BigDecimal carbonEmission;
    private Boolean charterCarCancel;
    private Short charteredBusType;
    private Short subCarLevel;
    private String carLevelName;
    private JSONObject driver;

    private String carRentSource;
    private String carRentLevelName;

    private int carMode;
    private String message;

    private JSONArray passingPoints;

    private OrderDispatchConfigVo dispatchConfig;
}
