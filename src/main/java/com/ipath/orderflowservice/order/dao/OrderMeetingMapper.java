package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.bean.vo.MeetingVo;
import com.ipath.orderflowservice.order.dao.bean.OrderMeeting;

import java.util.List;

public interface OrderMeetingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderMeeting param);

    int insertSelective(OrderMeeting param);

    OrderMeeting selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderMeeting param);

    int updateByPrimaryKey(OrderMeeting param);

}