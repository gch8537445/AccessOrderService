package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestExtralServiceDto {
    /**
     * 公司id
     */
    private Long companyd;

    /**
     * 增值服务ids
     */
    private List<Long> extraServiceIds;
}
