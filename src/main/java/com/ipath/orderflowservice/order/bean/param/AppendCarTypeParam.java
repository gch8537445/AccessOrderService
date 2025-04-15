package com.ipath.orderflowservice.order.bean.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AppendCarTypeParam implements Serializable{
    private Long userId;
    private Long companyId;
    private Long orderId;                       // 订单主键
    private String estimateId;                  // 预估主键
    private List<SelectedCar> cars;             // 选择的车型数组
}
