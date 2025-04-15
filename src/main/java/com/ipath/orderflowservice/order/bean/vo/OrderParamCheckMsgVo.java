package com.ipath.orderflowservice.order.bean.vo;


import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class OrderParamCheckMsgVo implements Serializable {


    private String title;

    private String content;

    private JSONObject cancel;

    private JSONObject confirm;

    private List<OrderParamCheckMsgOptionVo> options;

}
