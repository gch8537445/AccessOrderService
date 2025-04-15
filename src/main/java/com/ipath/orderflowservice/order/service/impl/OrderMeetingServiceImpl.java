package com.ipath.orderflowservice.order.service.impl;

import com.ipath.orderflowservice.order.bean.vo.MeetingVo;
import com.ipath.orderflowservice.order.dao.OrderMeetingMapper;
import com.ipath.orderflowservice.order.dao.bean.OrderMeeting;
import com.ipath.orderflowservice.order.service.OrderMeetingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 订单会议关系实现类
 * @author: qy
 **/
@Service
public class OrderMeetingServiceImpl implements OrderMeetingService {

    @Resource
    private OrderMeetingMapper orderMeetingMapper;

    @Override
    public int insertSelective(OrderMeeting orderMeeting) {
        return orderMeetingMapper.insertSelective(orderMeeting);
    }


}
