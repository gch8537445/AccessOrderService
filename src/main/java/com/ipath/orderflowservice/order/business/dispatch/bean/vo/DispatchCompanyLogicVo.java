package com.ipath.orderflowservice.order.business.dispatch.bean.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: qy
 * @create: 2025-01-06 10:02
 **/
@Data
public class DispatchCompanyLogicVo {

    private Long companyId;

    /**
     * 逻辑类型 1: 排除用户 2: 包含用户
     */
    private Integer logicType;

    private List<Long> userIds;

    public enum LogicType {
        EXCLUDE(1),
        INCLUDE(2),
        ;

        private Integer type;

        public Integer getType() {
            return type;
        }

        LogicType(Integer type) {
            this.type = type;
        }


    }

}
