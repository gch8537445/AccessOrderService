package com.ipath.orderflowservice.feignclient.dto;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class RequestEstimateDto {
    private Long companyId;
    private String coreOrderId;
    private String flightNumber;
    private String flightArrivalAirportCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date flightDepartTime;
    private String departLat;
    private String departLng;
    private String destLat;
    private String destLng;
    private String departPoi;
    private String destPoi;
    private Short serviceType;
    private String departCityCode;
    private String passengerPhone;
    private String pickupLocation;
    private String destLocation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date departTime;
    private List<Integer> includeCarSources;
    private List<RequestPlaceOrderExtraParamsDto> extraParams;

    /**
     * 途经点
     * "passingPoints":[
     *         {
     *             "lat":"39.915978",
     *             "lng":"116.433631",
     *             "name":"梓峰大厦"
     *         },
     *         {
     *             "lat":"39.919155",
     *             "lng":"116.44236",
     *             "name":"日坛国际贸易中心"
     *         }
     *     ]
     */
    private JSONArray passingPoints;

    private String traceId;
    
    private JSONObject userData;
}
