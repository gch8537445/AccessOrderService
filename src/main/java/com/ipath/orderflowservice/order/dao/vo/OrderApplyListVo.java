package com.ipath.orderflowservice.order.dao.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.ipath.orderflowservice.order.bean.param.KeyValue;
import com.ipath.orderflowservice.order.bean.vo.KeyValueVo;
import lombok.Data;

@Data
public class OrderApplyListVo {
    private String applyId;             // 申请id
    private String orderId;             // 订单 id
    private BigDecimal amount;          // 审核金额
    private String cityCode;            // 用车城市
    private String cityName;            // 用车城市
    private String sourceCode;           // 车型
    private String carSource;           // 车型
    private String userNameCn;          // 申请人用户名中文
    private String userNameEn;          // 申请人用户名英文
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date createTime;            // 订单创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date departTime;            // 用车时间
    private String pickupLocationName;  // 起点名称
    private String destLocationName;    // 终点名称
    private BigDecimal distance;
    private String duration;            // 时长"HH:mm:ss"
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date travelBeginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    private Date travelEndTime;
    private Boolean warning;
    private String warningRemind;
    private Short orderState;
    private Short approvalState;      // 审核状态：0: 未处理，1：已通过，2：已拒绝
    private Boolean isUpgrade;
    private Integer carLevel;
    private Integer serviceType;
    private String sourceNameCn;
    private String customInfo;
    private Boolean isAbnormal;
    private List<String> abnormalRules;
    private Long rulePreDepartApplyId;
    private Boolean abnormalTipFlag;
    private String tip;

    /**
     * 场景code
     */
    private String sceneCode;
    /**
     * 场景名称
     */
    private String sceneNameCn;
    /**
     * 用车原因
     */
    private String useCarReason;
    /**
     * 自定义label
     */
    private List<KeyValueVo> customLabel;

}
