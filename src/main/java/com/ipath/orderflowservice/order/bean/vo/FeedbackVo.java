package com.ipath.orderflowservice.order.bean.vo;

import cn.hutool.json.JSONArray;
import lombok.Data;

@Data
public class FeedbackVo {
    private Long orderId;
    private Short score;
    private String reply;
    private ComplaintInfo complaint;
    private String sourceNameCn;
    private String feedback;
    private JSONArray tpyes;;
}
