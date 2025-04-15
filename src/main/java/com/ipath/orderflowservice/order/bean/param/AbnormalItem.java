package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 合规预警参数
 */
@Data
public class AbnormalItem implements Serializable{
    /**
     * code
     */
    private String itemCode;

    /**
     * name
     */
    private String itemName;

    /**
     * value
     */
    private String itemValue;

    /**
     * type
     */
    private String itemType;
}
