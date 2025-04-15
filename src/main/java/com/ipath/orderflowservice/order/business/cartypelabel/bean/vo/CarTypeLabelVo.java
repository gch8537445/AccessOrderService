package com.ipath.orderflowservice.order.business.cartypelabel.bean.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description:
 * @author: qy
 * @create: 2024-10-16 10:00
 **/
@Data
public class CarTypeLabelVo {

    private Long companyId;
    @JSONField(name = "lable_code")
    private String labelCode;
    @JSONField(name = "lable_name")
    private String labelName;
    @JSONField(name = "lable_desc")
    private String labelDesc;
    private String supplierCode;
    private String supplierName;
    private String sourceCode;
    private String sourceName;
    private String ipathCode;
    private String carTypeCode;
    private String carTypeName;
    private String cityCode;

}
