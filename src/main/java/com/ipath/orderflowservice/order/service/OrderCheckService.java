package com.ipath.orderflowservice.order.service;


import com.ipath.orderflowservice.order.bean.bo.CompanyOrderCheckConfigBo;
import com.ipath.orderflowservice.order.bean.param.*;
import com.ipath.orderflowservice.order.bean.vo.CompanyOrderCountVo;
import com.ipath.orderflowservice.order.bean.vo.OrderParamCheckVo;
import com.ipath.orderflowservice.order.dao.bean.OrderBase;
import com.ipath.orderflowservice.order.dao.bean.OrderSource;

import java.util.List;
import java.util.Map;

/**
 * 订单校验 Service
 */
public interface OrderCheckService {

    /**
     * 参数校验
     * @param param
     * @return
     * @throws Exception
     */
    List<OrderParamCheckVo> checkOrderParam(OrderParamCheckParam param) throws Exception;
    /**
     * 参数校验（根据code）
     *
     * @param code
     * @return
     */
    OrderParamCheckVo setOrderParamCheck(String code, Long companyId,OrderParamCheckParam param);

    OrderParamCheckVo setOrderParamCheckTrafficHub(Long companyId, OrderParamCheckParam param);

    /**
     * 判断是否需要个人支付 （状态5 结算时）
     * @param orderBase
     * @return
     */
    boolean isNeedIndividualPay(OrderBase orderBase, OrderSource orderSource);


    /**
     * 自定义参数校验
     * @param param
     * @return
     * @throws Exception
     */
    void checkOrderParamRegulationInfo(CreateOrderParam param, String step) throws Exception;


    void checkSceneApprovalTypePreAndAf(CreateOrderParam param);

    /**
     * 判断 对账单 账期数量
     * @param param
     * @return
     */
    int checkStatementOfAccount(StatementOfAccountParam param);

    /**
     * 获取 公司,订单时间内的账单数量
     * @param param
     * @return
     */
    List<CompanyOrderCountVo> getCompanyCountByCreateTime(CompanyOrderCountParam param);


    /**
     * 获取 公司,订单状态,订单时间内的账单数量
     * @param param
     * @return
     */
    List<CompanyOrderCountVo> getCompanyStateCountByCreateTime(CompanyOrderCountParam param);

    /**
     * "获取 公司,审批时间内的账单数量
     * @param param
     * @return
     */
    List<CompanyOrderCountVo> getCompanyCountByApprovalTime(CompanyOrderCountParam param);

    /** 下单参数是否包含交通枢纽 **/
    CheckOrderParamResult checkOrderParamIncludeTraffichub(CreateOrderParam param, String step) throws Exception;

    CompanyOrderCheckConfigBo getCompanyOrderCheckConfig();
    CompanyOrderCheckConfigBo reCompanyOrderCheckConfig();
}
