package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


/**
 * 小程序申请免密支付请求
 */
@Data
public class WxentrustpayResponse {
    /**
     * 接口执行结果。0成功，其他失败
     */
    @JSONField(name="state")
    private Integer state;
    /**
     * 失败原因
     */
    @JSONField(name="txt")
    private String txt;

}
