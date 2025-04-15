package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

@Data
public class ApprovalNodeVo {
    /**
     * 分类id
     */
    private Short sortId;

    /**
     * 节点code
     */
    private String outCode;

    /**
     * 用户主键，我们系统的用户主键
     * 需转换成用户的code
     */
    private String userId;
}
