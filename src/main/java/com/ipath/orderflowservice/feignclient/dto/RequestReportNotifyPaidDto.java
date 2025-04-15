package com.ipath.orderflowservice.feignclient.dto;

import com.ipath.orderflowservice.order.bean.param.OrderPayDetail;
import lombok.Data;
import java.util.List;

@Data
public class RequestReportNotifyPaidDto {
    /**
     * 订单ids
     */
    private List<Long> orderIds;

    /**
     * 支付详情
     */
    private List<OrderPayDetail> orderPayInfo;

    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 支付号
     */
    public String transNo;

    /**
     * 支付类型;1:微信 2:支付宝 3:银联支付 4:华为支付 5:京东支付
     */
    public Short payWay;
}
