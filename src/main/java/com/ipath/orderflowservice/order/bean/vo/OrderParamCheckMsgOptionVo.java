package com.ipath.orderflowservice.order.bean.vo;


import lombok.Data;

import java.io.Serializable;


@Data
public class OrderParamCheckMsgOptionVo implements Serializable {


    private String value;

    private String label;

    private Boolean editing;

    private String placeholder;

    private Boolean isUserPay;

}
