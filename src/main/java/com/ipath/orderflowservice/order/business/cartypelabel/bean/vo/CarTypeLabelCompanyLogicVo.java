package com.ipath.orderflowservice.order.business.cartypelabel.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 企业维度画像信息
 * @author: qy
 * @create: 2025-01-02 09:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarTypeLabelCompanyLogicVo {
    /**
     * 符合规则的用户id
     */
    private List<Long> userIds;

    /**
     * 逻辑类型 1: 排除用户 2: 包含用户
     */
    private Integer logicType;

    /**
     * 模板信息
     */
    private TemplateVo templateVo;

    /**
     * 模板id
     */
    private Long templateId;


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
