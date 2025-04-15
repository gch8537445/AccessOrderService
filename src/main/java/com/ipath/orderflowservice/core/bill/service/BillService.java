package com.ipath.orderflowservice.core.bill.service;

/**
 * bill service
 */
public interface BillService {

    /**
     * 获取公司默认账户
     * 
     * @param companyId
     * @return
     */
    Long getDefaultAccountId(Long companyId);

    /**
     * 插入人员和账户匹配关系
     * @param companyId
     * @param userId
     * @param accountId
     */
    void insertAccountUser(Long companyId,Long userId, Long accountId);
}
