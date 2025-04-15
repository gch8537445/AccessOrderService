package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 行程结束时，订单结算 通知bill-core-service的消息体
 */
@Data
public class RequestBillNotifySettleOrderDto extends RequestBillNotifyBaseDto{
    /**
     * 账户id
     */
    private Long accountId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 场景id
     */
    private Long sceneId;
    /**
     * 场景发布id
     */
    private Long scenePublishId;
    /**
     * 订单使用关联场景的报销模式
     */
    private ReimModel sceneReim;
    /**
     * 预估价格
     */
    private BigDecimal estimatePrice;
    /**
     * 服务类型
     */
    private Short serviceType;
    /**
     * 行驶里程（公里）
     */
    private BigDecimal distance;
    /**
     * 出发时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date departTime;
    /**
     * 费用明细
     */
    private String transFeeDetail;
    /**
     * 运力总费用
     */
    private BigDecimal transTotalFee;
    /**
     * 预付标识
     * true：预付 false：非预付
     */
    private Boolean isPrepay;
    /**
     * 预付阀值
     */
    private BigDecimal prePayPayableAmount;
    /**
     * 车型
     */
    private Integer carLevel;
    /**
     * 车型列表
     */
    private List<SelectedCar> carLevels;
    /**
     * 使用的优惠券id列表
     */
    private List<Long> couponIds;
    /**
     * 运力平台Id
     */
    private Integer carSource;
    /**
     * 城市code
     */
    private String cityCode;
    /**
     * 审核状态
     */
    private Short approveState;
    /**
     * 服务费用
     */
    private ServiceFeeModel serviceFees;
    /**
     * 升舱描述
     */
    private UpgradeModel upgradeCar;
    /**
     * 调度费
     */
    private DispatchFee dispatchFee;
}
