package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class RequestBasicSettingsDto {
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 类型
     */
    private String type;

    /**
     * 分组id
     */
    private Long groupId;
}
