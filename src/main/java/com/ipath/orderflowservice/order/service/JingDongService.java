package com.ipath.orderflowservice.order.service;


public interface JingDongService {

    /**
     * 获取京东订单Id
     *
     * @param userId
     * @throws Exception
     */
    String getJingDongOrderId(Long userId, String sku) throws Exception;
}
