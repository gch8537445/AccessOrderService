package com.ipath.orderflowservice.order.service;

import com.ipath.orderflowservice.order.bean.vo.MeetingVo;
import com.ipath.orderflowservice.order.dao.bean.OrderMeeting;

import java.util.List;

/**
 * @description: 订单会议关系service
 * @author: qy
 **/
public interface OrderMeetingService {

    /**
     * 新增 会议订单对应关系
     * @param orderMeeting 对应关系
     * @return
     */
    int insertSelective(OrderMeeting orderMeeting);


}
