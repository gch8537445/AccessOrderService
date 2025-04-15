package com.ipath.orderflowservice.feignclient.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class RequestConfirmDependencyDto {
    private Long userId;
    private Long companyId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date carTime;
    private String departCityCode;
    private Short serviceType;
    private Long sceneId;
    private Long scenePublishId;
    private List<SelectedCar> cars;
}
