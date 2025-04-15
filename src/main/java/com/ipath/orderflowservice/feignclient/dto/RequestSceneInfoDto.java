package com.ipath.orderflowservice.feignclient.dto;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RequestSceneInfoDto {
    private Long sceneId;
    private Long scenePublishId;
    private Long userId;
    private Long companyId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // 输入参数格式
    private Date orderTime;
}
