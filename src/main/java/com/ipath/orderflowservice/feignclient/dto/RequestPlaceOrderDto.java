package com.ipath.orderflowservice.feignclient.dto;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipath.orderflowservice.order.bean.param.SelectedCar;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class RequestPlaceOrderDto {
    private Long userId;  // 用户id
    private String partnerOrderId;
    private Long companyId;
    private String accountId;
    private String estimateId;
    private String sceneId;
    private List<SelectedCar> cars;
    private String flightNumber;// 航班号
    private String flightDepartAirportCode;// 出发机场三字码
    private String flightArrivalAirportCode;// 接机机场三字码
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date flightDepartTime;// 航班起飞时间(yyyy-MM-dd HH:mm:ss)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date flightArrivalTime;// 航班到达时间(yyyy-MM-dd HH:mm:ss)
    private int flightDelayTime;
    private Short serviceType;
    private String departLat;
    private String departLng;
    private String departCityCode;
    private String pickupLocation;
    private String pickupLocationName;
    private String destLat;
    private String destLng;
    private String destCityCode;
    private String destLocation;
    private String destLocationName;
    private String userPhone;//下单人手机号
    private String passengerPhone;//乘客手机号
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date departTime;
    private boolean userAI;
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


    private String departPoi;
    private String destPoi;

    private String coreOrderId;

    private String traceId;
}
