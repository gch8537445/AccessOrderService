package com.ipath.orderflowservice.order.dao.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @description: 同步会议vo
 * @author: qy
 **/
@Data
@ApiModel(value = "同步会议vo")
public class SyncMeetingVo {

    private Long userid;

    private Long meetingId;

    private String ioStatus;

    /**
     * 0: 新增 1: 修改  2: 忽略
     */
    private Integer type;

}
