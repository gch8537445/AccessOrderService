package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FlightInfoVo implements Serializable {
    private String day;                    // 日期
    private List<FlightDetailVo> flights;  // 航班信息
}
