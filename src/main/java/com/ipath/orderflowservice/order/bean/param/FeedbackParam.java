package com.ipath.orderflowservice.order.bean.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

@Data
public class FeedbackParam {
    private Long userId;
    private Long orderId;
    private Long companyId;
    private String feedback;
    private Short score;
    private Boolean isConfirmAbnormal;//是否自动确认异常
    private Integer type;//1-评价 2-投诉
    private Short feedbackTypeId;//反馈类型
    private JSONArray complaints; // 投诉标签数组: [{id, lable}]
}
