package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 公司可用参数
 */
@Data
public class CarSourceParam implements Serializable{
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 可用平台
     */
    private List<Integer> availableCarSources;
}
