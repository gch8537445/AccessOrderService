package com.ipath.orderflowservice.order.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ipath.orderflowservice.order.bean.param.QueryOrderApply;
import com.ipath.orderflowservice.order.dao.bean.OrderApplyHistory;
import com.ipath.dao.param.QueryOrderCriteria;
import com.ipath.orderflowservice.order.dao.vo.OrderApplyHistoryListVo;
import com.ipath.orderflowservice.order.dao.vo.OrderApplyListVo;
import org.apache.ibatis.annotations.Param;

public interface OrderApplyHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderApplyHistory record);

    int insertSelective(OrderApplyHistory record);

    OrderApplyHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderApplyHistory record);

    int updateByPrimaryKey(OrderApplyHistory record);

    // 以下手工添加
    Integer selectOrderApplyCnt(QueryOrderApply criteria);
    List<OrderApplyListVo> selectOrderApplyList(QueryOrderApply criteria);
    List<OrderApplyHistoryListVo> selectApplyHistoryByOrderId(Long orderId);
    List<OrderApplyHistoryListVo> selectApplyHistoryByOrderIdForAll(Long orderId);
    List<Map<String, Object>> selectSumAmountByUser(QueryOrderCriteria criteria);
    List<Map<String, Object>> selectSumAmountByScene(QueryOrderCriteria criteria);

    List<OrderApplyHistory> selectByOrderId(@Param("orderId") Long orderId);
}