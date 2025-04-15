package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class RequestUserCouponDto {
    private long userId;// 用户主键
    private long companyId;// 公司主键
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    // private Date carTime;//用车时间
    // private String cityCode ;//城市code
}
