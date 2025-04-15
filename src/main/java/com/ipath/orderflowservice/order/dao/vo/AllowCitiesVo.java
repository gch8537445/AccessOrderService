package com.ipath.orderflowservice.order.dao.vo;

import lombok.Data;

import java.util.List;

@Data
public class AllowCitiesVo {
    private String mode;
    private List<String> list;
}
