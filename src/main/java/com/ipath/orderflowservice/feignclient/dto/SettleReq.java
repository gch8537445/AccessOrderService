package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import cn.hutool.json.JSONArray;

@Data
public class SettleReq {

  /**ka端 appid */
  private String appId;
  /**是否是灰度环境 */
  private Boolean grayFlag;
    /**
     * 订单状态
     */
    private Short eventType;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 账户id
     */
    private Long accountId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 服务类型
     */
    private Short serviceType;
    
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
     * 出发时间
     */
    private String departTime;

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
     * 增值服务id列表
     */
    private List<Long> extrFeeList;
    
    /**
     * 使用的优惠券id列表
     */
    private List<Long> couponIds;

    /**
     * 运力平台Id
     */
    private String carSource;

    /**
     * 城市code
     */
    private String cityCode;

    /**
     * 审核状态
     */
    private Short approveState;

    /**个人支付，如果此值为true，忽略sceneReim，直接是个人支付 。修改目的地时，改变此值*/
    private Boolean personalPay;

    /**
     * 升舱描述
     */
    private UpgradeModel upgradeCar;

    /**
     * 调度费
     */
    private DispatchFee dispatchFee;

   /**
     * 费用明细
     */
    private JSONArray transFeeDetail;
    /**
     * 运力总费用
     */
    private BigDecimal transTotalFee;

    /**限制金额，针对H5客户，超出管控限额后需拉起途径侧个人支付的情况 截止到2025/01/02，只有嘉宝客户使用 */
    private BigDecimal limitEnterpriceSettleMoney;

    private ServiceFeeModel serviceFees;//适配老结算
}
