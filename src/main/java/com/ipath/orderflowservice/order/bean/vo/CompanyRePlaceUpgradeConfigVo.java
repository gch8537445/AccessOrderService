package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 企业自动重新派单配置
 */
@Data
public class CompanyRePlaceUpgradeConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 是否开启
     */
    private boolean enable;
    /**
     * 预估价格超过多少限制升舱 (单位：分)
     */
    private int money;

    /**
     *
     */
    private List<Long> userList;

    /**
     *
     */
    private List<Long> cityList;

    /**
     *
     */
    private List<Long> carTypeList;

    /**
     *
     */
    private Data startTime;

    /**
     *
     */
    private Data endTime;

    /**
     */
    private int limit1;

    /**
     */
    private int limit2;

}
