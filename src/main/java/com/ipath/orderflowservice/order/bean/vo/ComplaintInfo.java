package com.ipath.orderflowservice.order.bean.vo;

import cn.hutool.json.JSONArray;
import lombok.Data;

@Data
public class ComplaintInfo {
    private Short state;
    private JSONArray tpyes;   // 投诉label数组，结构 [{id, label}]
}
