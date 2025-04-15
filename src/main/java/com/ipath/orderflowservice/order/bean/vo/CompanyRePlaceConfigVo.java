package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 企业自动重新派单配置
 */
@Data
public class CompanyRePlaceConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 自动派单，是否开启
     */
    private boolean enable;
    /**
     * 自动派单，关闭时间 （1.从系统自动派单时间开始算。2.单位 min。）
     */
    private int closeTime;

}
