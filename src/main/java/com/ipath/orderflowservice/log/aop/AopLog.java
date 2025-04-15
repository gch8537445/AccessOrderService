package com.ipath.orderflowservice.log.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AopLog
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AopLog {

    String logName() default "";

    String interfaceName() default "";

    String companyId() default "";

    String userId() default "";

    String orderId() default "";

    //1-controller  2-service 3-feign
    int classType() default 1;

    String eInterfacePath() default "";

    String eInterfaceName() default "";

}
