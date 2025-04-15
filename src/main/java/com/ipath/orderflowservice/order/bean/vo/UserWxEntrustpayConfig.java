package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;


@Data
public class UserWxEntrustpayConfig {
    private Long userId;
    private Long companyId;
    private String userName;
    //客户免密支付协议ID
    private String wxEntrustPayId;
    //客户免密支付签约状态。0：已签约1：未签约  9：签约进行中
    private int wxEntrustPayState;
    //申请时间
    private String wxEntrustPayDateApply;
    //签约成功时间
    private String wxEntrustPayDateSucceed;
    //解约时间
    private String wxEntrustPayDate1Cancel;
}
