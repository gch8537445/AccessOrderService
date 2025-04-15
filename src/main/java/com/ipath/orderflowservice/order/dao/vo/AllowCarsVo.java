package com.ipath.orderflowservice.order.dao.vo;

import lombok.Data;

import java.util.List;

@Data
public class AllowCarsVo {
    private String mode;
    private List<String> listCarLevel;
    private List<String> listCarSource;
}
