package com.ipath.orderflowservice.order.dao;

import java.math.BigDecimal;
import java.util.List;

import com.ipath.orderflowservice.order.bean.param.CompanyOrderCountParam;
import com.ipath.orderflowservice.order.bean.param.QueryOrderListByMonth;
import com.ipath.orderflowservice.order.bean.param.StatementOfAccountParam;
import com.ipath.orderflowservice.order.bean.vo.CompanyOrderCountVo;
import com.ipath.orderflowservice.order.dao.bean.OrderBase;
import com.ipath.dao.param.QueryOrderCriteria;
import com.ipath.orderflowservice.order.dao.vo.*;
import com.ipath.orderflowservice.feignclient.dto.SelectPayedOrdersCount;
import org.apache.ibatis.annotations.Param;


public interface OrderBaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderBase record);

    int insertSelective(OrderBase record);

    OrderBase selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderBase record);

    int updateByPrimaryKey(OrderBase record);

    int updateUserPay(OrderBase record);

    // 以下手工添加
    OrderBase selectPreOrderCreateTime(OrderBase orderBase);
    List<OrderListByMonthVo> selectOrdersByCreateDate(QueryOrderCriteria criteria);
    OrderDetailVo selectOrderDetail(Long id);
    List<OrderListWaitPayVo> selectWaitPayOrders(List<Long> orderIdList);
    List<OrderListWaitPayVo> selectWaitPayOrdersByUserId(@Param("userId") Long userId);
    List<PaidOrderVo> selectPayOrdersByUserId(QueryOrderListByMonth param);

    BigDecimal selectTotalAmount(QueryOrderListByMonth param);

    List<PaidOrderVo> selectPaidOrdersByOrderIds(@Param("list") List<Long> list);
    List<PaidOrderVo> selectPayedOrderCountByUserId(SelectPayedOrdersCount selectPayedOrdersCount);
    List<CompanyLimitMappingVo> selectLimitMappingByCompanyId(Long companyId);
    int selectRunningOrderByUserId(Long userId);

    OrderBase selectByPartnerOrderId(String partnerOrderId);

    List<OrderErrorState> selectErrorStateOrderList();

    List<RunningOrdersVo> selectRunningOrderList(Long companyId);

    int selectWaitPayOrderCntByUserId(Long userId);

    OrderForReport selectOrderForReportById(Long orderId);

    List<CompanyOrderCountVo> getCompanyCountByCreateTime(CompanyOrderCountParam param);

    List<CompanyOrderCountVo> getCompanyStateCountByCreateTime(CompanyOrderCountParam param);

    List<CompanyOrderCountVo> getCompanyCountByApprovalTime(CompanyOrderCountParam param);

    int checkStatementOfAccountByOrderTime(StatementOfAccountParam param);

    int checkStatementOfAccountByApprovalTime(StatementOfAccountParam param);

    /**
     * 用户和司机在指定天数内成单次数
     * @param companyId 企业id
     * @param userId 下单人id
     * @param vehicleNo 车牌
     * @param days 指定天数
     * @return 数量
     */
    int getSameDriverAndPassengerCount(@Param("companyId") Long companyId, @Param("userId") Long userId, @Param("vehicleNo") String vehicleNo,@Param("days") Integer days);

    /**
     * 用户在指定天数内总单数
     * @param companyId 企业id
     * @param userPhone 下单人手机号
     * @param days 指定天数
     * @return 数量
     */
    int getPassengerCount(@Param("companyId")Long companyId, @Param("userPhone")String userPhone, @Param("days")Integer days);

    /**
     * 用户和司机在指定天数内成单次数结束时间
     * @param param
     * @return
     */
    int checkStatementOfAccountByTravelEndTime(StatementOfAccountParam param);

    int rePlaceUpdateStateByOrderId(OrderBase record);

    int checkStatementOfAccountByTravelBeginTime(StatementOfAccountParam param);

    int updatePhoneVirtual(@Param("id")Long id, @Param("userPhoneVirtual")String userPhoneVirtual, @Param("passengerPhoneVirtual")String passengerPhoneVirtual);
}