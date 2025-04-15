package com.ipath.orderflowservice.order.dao;

import java.util.List;

import com.ipath.orderflowservice.order.dao.bean.OrderApply;
import com.ipath.dao.param.QueryOrderCriteria;
import com.ipath.orderflowservice.order.dao.param.QueryPendingApproval;
import com.ipath.orderflowservice.order.dao.vo.OrderApplyListVo;

public interface OrderApplyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderApply record);

    int insertSelective(OrderApply record);

    OrderApply selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderApply record);

    int updateByPrimaryKey(OrderApply record);

    // 以下手工添加
    int selectWaitApproveOrderCount(String approverUserId);
    List<OrderApplyListVo> selectOrderApplyList(QueryOrderCriteria criteria);
    Integer selectOrderApplyCnt(QueryOrderCriteria criteria);
    OrderApply selectByOrderId(Long orderId);
}