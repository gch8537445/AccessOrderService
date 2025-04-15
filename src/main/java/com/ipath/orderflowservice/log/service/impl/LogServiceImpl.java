package com.ipath.orderflowservice.log.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.bean.CustomException;
import com.ipath.orderflowservice.config.ExternSystemErrorCodeException;
import com.ipath.orderflowservice.log.aop.AopLog;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.constant.OrderConstant;
import com.ipath.orderflowservice.order.bean.vo.CacheCompanyInfo;
import com.ipath.orderflowservice.order.service.CacheService;
import com.ipath.orderflowservice.order.util.CacheUtil;
import com.ipath.orderflowservice.order.util.CipherUtil;
import com.jd.open.api.sdk.internal.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Value("${log.enabled}")
    public boolean enabledLog;

    @Value("${log.level}")
    public int levelLog;

    @Value("${log.url}")
    private String url;

    @Value("${log.serviceCode}")
    public String serviceCode;

    @Value("${log.serviceName}")
    public String serviceName;

    @Autowired
    private CacheService cacheService;

    @Override
    public String setLogEnabled(boolean enabled, int level, Log log) {
        this.enabledLog = enabled;
        this.levelLog = level;
        return "当前日志enabled=" + this.enabledLog + ",level=" + this.levelLog;
    }

    /**
     *
     * @param log
     */
    @Async("saveLogAsyncExecutor")
    @Override
    public void saveLogAsync(Log log, String resBody) {
        if (!enabledLog) {
            return;
        }
        log.setState(1);
        if (levelLog > log.getState()) {
            return;
        }

        log.setSrevice(serviceCode);
        log.setSreviceName(serviceName);
        log.setResBody(resBody);
        if(null == log.getResMillsecond()){
            log.setEndTime(System.currentTimeMillis());
            log.setResMillsecond(log.getEndTime() - log.getStartTime());
        }
        this.post(log);
    }

    /**
     *
     * @param log
     */
    @Async("saveLogAsyncExecutor")
    @Override
    public void saveWarningLogAsync(Log log, String resBody) {
        if (!enabledLog) {
            return;
        }
        log.setState(2);
        if (levelLog > log.getState()) {
            return;
        }

        log.setSrevice(serviceCode);
        log.setSreviceName(serviceName);
        // log.setInterfaceTime(date);
        log.setResBody(resBody);
        this.post(log);
    }

    /**
     * 保存日志（错误）
     */
    @Override
    public void saveErrorLogAsync(Log log, String resBody) {
        if (!enabledLog) {
            return;
        }

        log.setState(3);
        if (levelLog > log.getState()) {
            return;
        }
        log.setErrorMsg(resBody);
        log.setSrevice(serviceCode);
        log.setSreviceName(serviceName);
        log.setResBody(resBody);
        if(null == log.getResMillsecond()){
            log.setEndTime(System.currentTimeMillis());
            log.setResMillsecond(log.getEndTime() - log.getStartTime());
        }
        this.post(log);
    }

    /**
     *
     * @param log
     */
    @Async("saveLogAsyncExecutor")
    @Override
    public void saveErrorLogAsync(Log log, Exception e) {
        if (!enabledLog) {
            return;
        }

        log.setState(3);
        if (levelLog > log.getState()) {
            return;
        }
        String resBody = JSONUtil.toJsonStr("错误信息：{" + e.getMessage() + "}, 堆栈信息：") + JSONUtil.toJsonStr(e);
        log.setErrorMsg(e.getMessage());
        log.setSrevice(serviceCode);
        log.setSreviceName(serviceName);
        // log.setInterfaceTime(date);
        log.setResBody(resBody);
        if(null == log.getResMillsecond()){
            log.setEndTime(System.currentTimeMillis());
            log.setResMillsecond(log.getEndTime() - log.getStartTime());
        }
        this.post(log);
    }

    /**
     * @param point
     * @param date
     */
    @Override
    @Async("saveLogAsyncExecutor")
    public void handleLogBefore(JoinPoint point) {
        if (!enabledLog) {
            return;
        }
        if (levelLog > 1) {
            return;
        }
        this.saveByJoinPoint(point, 1, "1", this.argsArrayToString(point), null);
    }

    /**
     * @param point
     * @param ret
     * @param date
     */
    @Override
    @Async("saveLogAsyncExecutor")
    public void handleLogAfterReturning(JoinPoint point, Object ret) {
        if (!enabledLog) {
            return;
        }
        if (levelLog > 1) {
            return;
        }
        this.saveByJoinPoint(point, 1, "2", this.argsArrayToString(point), JSONUtil.toJsonStr(ret));
    }

    @Override
    @Async("saveLogAsyncExecutor")
    public void handleLogAfterThrowing(JoinPoint point, Exception exc) {
        if (!enabledLog) {
            return;
        }
        if (levelLog > 3) {
            return;
        }

        int state = this.getExcLevel(exc);
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        AopLog aopLog = method.getAnnotation(AopLog.class);
        String body = this.argsArrayToString(point);
        if (aopLog.classType() == 1) {
            this.saveByJoinPoint(point, state, "2", body, JSONUtil.toJsonStr(this.controllerErrorRetrun(exc)));
        }
        if (aopLog.classType() == 2) {
            String resBody = JSONUtil.toJsonStr("错误信息：{" + exc.getMessage() + "}, 堆栈信息：") + JSONUtil.toJsonStr(exc);
            this.saveByJoinPoint(point, state, "2", body, resBody);
        }

    }

    /**
     * @param point
     * @param state   接口状态 1-正常 2-警告 3-异常
     * @param type    接口类型 1-请求 2-返回
     * @param date    接口时间
     * @param body    请求body
     * @param resBody 返回body
     */
    public void saveByJoinPoint(JoinPoint point, int state, String type, String body, String resBody) {

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        AopLog aopLog = method.getAnnotation(AopLog.class);
        String methodName = signature.getName();
        String className = point.getTarget().getClass().getName();

        Log log = new Log();
        log.setCompanyId(this.getValue(point, aopLog, "companyId"));
        log.setUserId(this.getValue(point, aopLog, "userId"));
        log.setOrderId(this.getValue(point, aopLog, "orderId"));
        log.setInterfacePath(methodName + "." + className);
        log.setInterfaceName(aopLog.interfaceName());
        log.setState(state);
        log.setType(type);
        log.setLogName(aopLog.logName());
        log.setExternalSystemInterfaceName(aopLog.eInterfaceName());
        log.setExternalSystemInterfacePaht(aopLog.eInterfacePath());
        log.setSrevice(serviceCode);
        log.setSreviceName(serviceName);
        log.setInterfaceTime(new Date());
        log.setBody(body);
        log.setResBody(resBody);

        this.post(log);

    }

    /**
     * 请求日志服务
     *
     * @param req
     */
    public void post(Log req) {

        try {
            String json = JsonUtil.toJson(req);
            String encrypt = CipherUtil.encrypt(json, "ipath$%^afj@2024");
            String body = HttpRequest.post(url).header(Header.CONTENT_TYPE, "application/json; charset=utf-8")
                    .timeout(60000) // 超时，毫秒
                    .body(encrypt).execute().body();
            // log.error("请求日志服务成功:{}", CipherUtil.decrypt(encrypt, "ipath$%^afj@2024"));
        } catch (Exception e) {
            log.error("请求日志服务异常:{}", e.toString());
        }
    }

    private BaseResponse controllerErrorRetrun(final Exception e) {
        if (null == e) {
            return null;
        }
        String msg = e.getMessage() == null ? "" : e.getMessage();
        if (e instanceof NullPointerException) {
            msg = OrderConstant.ERORR_SYSTEM_BUSY;
        } else if (e instanceof HttpMessageNotReadableException) {
            msg = OrderConstant.ERORR_HTTPMESSAGENOTREADABLEEXCEPTION;
        } else if (e instanceof IllegalArgumentException) {
            msg = OrderConstant.ERORR_SYSTEM_BUSY;
        } else if (e instanceof UncategorizedSQLException || e instanceof BadSqlGrammarException) {
            msg = OrderConstant.ERORR_SYSTEM_BUSY;
        } else if ((e instanceof BusinessException)) {

        } else if ((e instanceof CustomException)) {
            if (msg.length() > OrderConstant.ERORR_MSG_MAX_LENGTH) {
                msg = OrderConstant.ERORR_SYSTEM_BUSY;
            }
        } else if ((e instanceof ExternSystemErrorCodeException)) {
            // 其他异常
        } else {
            if (msg.length() > OrderConstant.ERORR_MSG_MAX_LENGTH) {
                msg = OrderConstant.ERORR_SYSTEM_BUSY;
            }
        }

        BaseResponse resp = BaseResponse.Builder.build(null, msg);
        if (e instanceof CustomException) {
            CustomException customException = (CustomException) e;
            resp.setCode(customException.getCode());
        }
        return resp;
    }

    /**
     * 判断异常等级(接口状态)
     *
     * @param e
     * @return 1-正常 2-警告 3-异常
     */
    private int getExcLevel(final Exception e) {
        if (e == null) {
            return 1;
        }
        if (e instanceof NullPointerException) {
            return 3;
        } else if (e instanceof HttpMessageNotReadableException) {
            return 2;
        } else if (e instanceof IllegalArgumentException) {
            return 2;
        } else if (e instanceof UncategorizedSQLException || e instanceof BadSqlGrammarException) {
            return 3;
        } else if ((e instanceof BusinessException)) {
            return 2;
        } else if ((e instanceof CustomException)) {
            return 2;
        } else if ((e instanceof ExternSystemErrorCodeException)) {
            return 3;
        } else {
            return 3;
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(final JoinPoint point) {
        JSONObject jsonObject = new JSONObject();
        Object[] args = point.getArgs();
        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(((MethodSignature) point.getSignature()).getMethod());
        for (int i = 0; i < parameterNames.length; i++) {
            jsonObject.set(parameterNames[i], args[i]);
        }
        return jsonObject.toString();
    }

    /**
     * 解析参数
     *
     * @param point
     * @param aopLog
     * @param key
     * @return
     */
    public Long getValue(final JoinPoint point, AopLog aopLog, String key) {

        if (StringUtils.equals(key, "companyId") && StringUtils.isBlank(aopLog.companyId())) {
            return null;
        }

        if (StringUtils.equals(key, "userId") && StringUtils.isBlank(aopLog.userId())) {
            return null;
        }

        if (StringUtils.equals(key, "orderId") && StringUtils.isBlank(aopLog.orderId())) {
            return null;
        }

        // 通过joinPoint获取被注解方法
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        // 创建解析器
        SpelExpressionParser parser = new SpelExpressionParser();
        // 获取表达式
        Expression expression = null;
        if (StringUtils.equals(key, "companyId")) {
            expression = parser.parseExpression(aopLog.companyId());
        }

        if (StringUtils.equals(key, "userId")) {
            expression = parser.parseExpression(aopLog.userId());
        }

        if (StringUtils.equals(key, "orderId")) {
            expression = parser.parseExpression(aopLog.orderId());
        }

        // 设置解析上下文(有哪些占位符，以及每种占位符的值)
        EvaluationContext context = new StandardEvaluationContext();
        // 获取参数值
        Object[] args = point.getArgs();
        // 获取运行时参数的名称
        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        // 解析,获取替换后的结果
        Object result = expression.getValue(context);
        if (result == null) {
            return null;
        }
        return Long.valueOf(result.toString());
    }

    @Override
    public Log getLog(Long companyId, Long userId, Long orderId, InterfaceEnum interfaceEnum) {
        Log log = new Log();
        log.setSrevice(serviceCode);
        log.setSreviceName(serviceName);
        log.setCompanyId(companyId);
        log.setUserId(userId);
        log.setOrderId(orderId);
        log.setLogCode(interfaceEnum.getLogCode());
        log.setLogName(interfaceEnum.getLogName());
        log.setInterfaceTime(new Date());
        log.setInterfaceName(interfaceEnum.getInterfaceName());
        log.setInterfacePath(interfaceEnum.getInterfacePath());
        log.setType(interfaceEnum.getType());
        log.setMethod(interfaceEnum.getMethod());
        log.setStartTime(System.currentTimeMillis());
        if (null != companyId) {
            CacheCompanyInfo companyInfo = cacheService.getCompanyInfo(companyId);
            if (null != companyInfo) {
                log.setCompanyName(companyInfo.getCompanyName());
            }
        }

        return log;
    }

    @Override
    public Log getLog(Long companyId, Long userId, Long orderId, InterfaceEnum interfaceEnum,String traceId){
        Log log = new Log();
        log.setTraceId(traceId);
        log.setSrevice(serviceCode);
        log.setSreviceName(serviceName);
        log.setCompanyId(companyId);
        log.setUserId(userId);
        log.setOrderId(orderId);
        log.setLogCode(interfaceEnum.getLogCode());
        log.setLogName(interfaceEnum.getLogName());
        log.setInterfaceTime(new Date());
        log.setInterfaceName(interfaceEnum.getInterfaceName());
        log.setInterfacePath(interfaceEnum.getInterfacePath());
        log.setType(interfaceEnum.getType());
        log.setMethod(interfaceEnum.getMethod());
        log.setStartTime(System.currentTimeMillis());
        if (null != companyId) {
            CacheCompanyInfo companyInfo = cacheService.getCompanyInfo(companyId);
            if (null != companyInfo) {
                log.setCompanyName(companyInfo.getCompanyName());
            }
        }

        return log;
    }
}
