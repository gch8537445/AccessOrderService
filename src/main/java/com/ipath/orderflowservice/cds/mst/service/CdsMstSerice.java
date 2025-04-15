package com.ipath.orderflowservice.cds.mst.service;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.CdsMstDto;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;


public interface CdsMstSerice {
    /**
     * 可用车型
     */
    BaseResponse filterCarType(CdsMstDto cdsMstDto, CreateOrderParam orderParam) throws Exception;

    /**
     * 提单 (管控校验)
     */
    BaseResponse createOrder(CdsMstDto cdsMstDto, CreateOrderParam orderParam) throws Exception;

    /**
     * 获取用户自费金额及管控费用明细
     */
    BaseResponse getPayDetail(CdsMstDto cdsMstDto) throws Exception;

    /**
     * 订单支付
     */
    BaseResponse ordePay(CdsMstDto cdsMstDto) throws Exception;

    /**
     * 订单信息查询
     */
    BaseResponse ordeQuery(CdsMstDto cdsMstDto) throws Exception;
}
