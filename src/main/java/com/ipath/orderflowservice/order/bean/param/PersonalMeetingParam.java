package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

import java.util.List;

/**
 * @description: 个人会议参数
 * @author: qy
 **/
@Data
public class PersonalMeetingParam {

    private String param;

    private List<Long> excludeIds;

    private Integer size;

    private Long userid;

}
