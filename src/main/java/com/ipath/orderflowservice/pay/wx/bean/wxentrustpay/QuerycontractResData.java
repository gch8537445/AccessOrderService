package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


/**
 * 查询微信免密签约信息 返回参数
 */
@Data
public class QuerycontractResData{
    /**
     * 签约状态。0：已签约1：未签约  9：签约进行中
     */
    @JSONField(name="contract_state")
    private String contractState;
    /**
     * 签约对应公众号的openid。小程序对应outerid
     */
    @JSONField(name="openid")
    private String openid;

}
