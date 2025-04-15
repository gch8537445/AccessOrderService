package com.ipath.orderflowservice.order.dao.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.vo.KeyValueInfoVo;
import com.ipath.orderflowservice.order.bean.vo.KeyValueVo;
import com.ipath.orderflowservice.order.dao.bean.OrderBase;

import lombok.Data;

@Data
public class OrderDetailVo extends OrderBase {
    private String orderId;
    private BigDecimal totalAmount; 
    private String coreOrderId;
    private String userNameCn;
    private BigDecimal travelDistance; // 里程
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date travelBeginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date travelEndTime;
    private String duration; // 时长"HH:mm:ss"
    private String vehicleNo; // 车牌号
    private String vehicleModel; // 型号
    private BigDecimal companyBearAmount;// 企业支付
    private BigDecimal userBearAmount; // 个人支付
    private BigDecimal personalPayableAmount; // 待支付金额
    private BigDecimal personalPaidAmount; // 已支付金额
    private BigDecimal increaseAmount; // 加价金额(分)
    private Short payState;//个人支付状态，针对泛嘉、梓如这样的客户，状态5时，此字段会有值
    private Long workFlowId;
    private Long sceneId;
    /**
     * 审批拒绝后个人是否支付
     */
    private Boolean isPay;
    private Boolean charterCarCancel;
    private Short charteredBusType;
    private JSONArray passingPoints;

    private String driverLevel;
    private String vehicleColor;
    private String orderParams;
    private Boolean showReplaceOrderBtn;

    private String customInfo;

    /**
     * 自定义label
     */
    private List<KeyValueVo> customLabel;

    /**
     * 行前审批单id
     */
    private Long rulePreDepartApplyId;

     /**
     * 合规预警是否显示提示信息flag
     */
    private Boolean abnormalTipFlag;
    /**
     * 合规预警提示信息
     */
    private String tip;

    /**
     * 0 - 非结算中
     * 1 - 结算中
     * 2 - 支付中
     */
    private Integer settling = 0;

    private String driverAvatar;
}
