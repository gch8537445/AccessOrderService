package com.ipath.orderflowservice.order.dao.bean;

import lombok.Data;

import java.util.List;

/**
 * @description: 场景规则
 * @author: qy
 **/
@Data
public class SceneItemValue {
    /**
     * 自动审批状态
     * 1:  通过
     * 2:  拒绝
     */
    private Short action;

    /**
     *  自动审批类型
     *  weekday:  时间段
     *  holiday:  工作日/节假日
     */
    private String mode;

    /**
     * false: 显示为系统审批
     * true: 显示为用户实际审批人
     */
    private Boolean actualApprover;

    /**
     * 具体规则
     */
    private List<ItemValue> rules;


}
