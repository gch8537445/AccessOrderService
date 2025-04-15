package com.ipath.orderflowservice.order.dao.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CompanyLimitVo {
    /**
     * 限制起始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date limitTimeFrom;

    /**
     * 限制结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date limitTimeTo;

    /**
     * 限制类型
     */
    private int type;

    /**
     * 限制值
     */
    private String value;

    public CompanyLimitVo(Date limitTimeFrom, Date limitTimeTo, Short type, String value) {
        this.limitTimeFrom = limitTimeFrom;
        this.limitTimeTo = limitTimeTo;
        this.type = type;
        this.value = value;
    }

    public CompanyLimitVo(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public CompanyLimitVo() {

    }
}
