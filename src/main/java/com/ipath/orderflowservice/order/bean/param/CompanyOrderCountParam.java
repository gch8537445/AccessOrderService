package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @description: 公司账单数量参数
 * @author: qy
 **/
@Data
public class CompanyOrderCountParam {

    private List<Long> companyIds;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date begin;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end;
}
