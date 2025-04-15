package com.ipath.orderflowservice.log.aop;

import com.ipath.orderflowservice.log.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 日志切面
 */
@Slf4j
@ComponentScan
@Component
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true)
public final class LogDataAspect {

    @Autowired
    private LogService logService;

    @Pointcut("@annotation(AopLog)")
    public void aopLogPointCut() {
    }

    @Before("aopLogPointCut()")
    public void before(JoinPoint point) {
        logService.handleLogBefore(point);
    }

    @AfterReturning(value = "aopLogPointCut()", returning = "ret")
    public void afterReturning(JoinPoint point, Object ret) {
        logService.handleLogAfterReturning(point, ret);
    }

    @AfterThrowing(value = "aopLogPointCut()", throwing = "e")
    public void afterThrowing(JoinPoint point, Exception e) {
        logService.handleLogAfterThrowing(point, e);
    }

}
