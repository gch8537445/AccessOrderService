package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

/**
 * 获取审批路径dto
 */
@Data
public class RequestProcessPathDto {
    /**
     * 流程定义id
     */
    private Long procDefId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 场景id
     */
    private Long sceneId;
}
