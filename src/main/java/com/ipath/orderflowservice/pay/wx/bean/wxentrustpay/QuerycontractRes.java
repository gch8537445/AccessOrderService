package com.ipath.orderflowservice.pay.wx.bean.wxentrustpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


/**
 * 查询微信免密签约信息 返回参数
 */
@Data
public class QuerycontractRes extends WxentrustpayResponse{
    /**
     * 用户数据，回调时原样返回
     */
    @JSONField(name="attachInfo")
    private String attachInfo;
    /**
     * 数据对象。state是0时返回
     */
    @JSONField(name="data")
    private QuerycontractResData data;

}
