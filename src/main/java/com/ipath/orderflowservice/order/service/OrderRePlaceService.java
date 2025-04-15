package com.ipath.orderflowservice.order.service;


/***
 * 订单重新派单 ServiceImpl
 */
public interface OrderRePlaceService {


    void rePlaceForNoDriver(Long companyId);

    void rePlaceForCoreCancel(Long companyId);
}
