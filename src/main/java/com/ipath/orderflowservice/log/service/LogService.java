package com.ipath.orderflowservice.log.service;


import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;

import org.aspectj.lang.JoinPoint;
import org.springframework.scheduling.annotation.Async;

public interface LogService {


    String setLogEnabled(boolean enabled, int level, Log log);

    @Async("saveLogAsyncExecutor")
    void saveLogAsync(Log log, String resBody);

    @Async("saveLogAsyncExecutor")
    void saveWarningLogAsync(Log log, String resBody);

    @Async("saveLogAsyncExecutor")
    void saveErrorLogAsync(Log log, String resBody);

    @Async("saveLogAsyncExecutor")
    void saveErrorLogAsync(Log log, Exception e);

    @Async("saveLogAsyncExecutor")
    void handleLogBefore(JoinPoint point);

    @Async("saveLogAsyncExecutor")
    void handleLogAfterReturning(JoinPoint point, Object ret);

    @Async("saveLogAsyncExecutor")
    void handleLogAfterThrowing(JoinPoint point, Exception e);

    Log getLog(Long companyId, Long userId, Long orderId, InterfaceEnum interfaceEnum);

    Log getLog(Long companyId, Long userId, Long orderId, InterfaceEnum interfaceEnum,String traceId);
}
