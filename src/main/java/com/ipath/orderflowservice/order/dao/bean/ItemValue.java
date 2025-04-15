package com.ipath.orderflowservice.order.dao.bean;

import lombok.Data;

/**
 * @description: 场景规则
 * @author: qy
 **/
@Data
public class ItemValue {

    /**
     * mode = weekday <br/>
     * day: 1- 7 周一-周日 <br/>
     * mode = holiday <br/>
     * day: 0  工作日 <br/>
     * day: 1  周末及法定节假日 <br/>
     */
    private Integer day;

    /**
     * 开始时间
     */
    private String b;

    /**
     * 结束时间
     */
    private String e;
}
