package com.ipath.orderflowservice.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.bean.CustomException;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.enums.InterfaceEnum;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.constant.OrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.LRUMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestControllerAdvice
public class OrderControllerExceptionHandler {

    /**
     * 异常缓存
     **/
    public static LRUMap OrderExceptionCache = new LRUMap(5);

    @Autowired
    private LogService logService;

    @ExceptionHandler
    public BaseResponse handler(HttpServletRequest req, HttpServletResponse res, Exception e) {

        if (res.getStatus() == HttpStatus.BAD_REQUEST.value()) {
            log.info("修改返回状态值为200");
            res.setStatus(HttpStatus.OK.value());
        }

        String msg = e.getMessage();
        boolean alarm = false;
        String alarmTitle = "";
        String alarmMsg = null;
        int max_stack_trace_depth = -1;
        // 空指针异常
        if (e instanceof NullPointerException) {
            msg = OrderConstant.ERORR_SYSTEM_BUSY;
            alarm = true;
            alarmTitle = OrderConstant.ERORR_ALARMTITLE_NULLPOINTEREXCEPTION;
            max_stack_trace_depth = 5;

            Log activityLog = logService.getLog(null, null, null, InterfaceEnum.ORDER_RUNTIME_EXCEPTION);
            activityLog.setInterfacePath(activityLog.getInterfacePath() + "NullPointerException");
            activityLog.setInterfaceName(activityLog.getInterfaceName() + "空指针");
            logService.saveErrorLogAsync(activityLog, e);
            // 参数错误异常
        } else if (e instanceof HttpMessageNotReadableException) {
            msg = OrderConstant.ERORR_HTTPMESSAGENOTREADABLEEXCEPTION;
            max_stack_trace_depth = 1;
            // 内部参数错误
        } else if (e instanceof IllegalArgumentException) {
            msg = OrderConstant.ERORR_SYSTEM_BUSY;
            alarm = true;
            alarmTitle = OrderConstant.ERORR_ALARMTITLE_ILLEGALARGUMENTEXCEPTION;
            Log activityLog = logService.getLog(null, null, null, InterfaceEnum.ORDER_RUNTIME_EXCEPTION);
            activityLog.setInterfacePath(activityLog.getInterfacePath() + "IllegalArgumentException");
            activityLog.setInterfaceName(activityLog.getInterfaceName() + "内部参数错误");
            logService.saveErrorLogAsync(activityLog, e);
            // 数据库异常 e instanceof PSQLException
        } else if (e instanceof UncategorizedSQLException || e instanceof BadSqlGrammarException) {
            msg = OrderConstant.ERORR_SYSTEM_BUSY;
            alarm = true;
            alarmTitle = OrderConstant.ERORR_ALARMTITLE_BADSQLGRAMMAREXCEPTION;
            max_stack_trace_depth = 20;
            Log activityLog = logService.getLog(null, null, null, InterfaceEnum.ORDER_RUNTIME_EXCEPTION);
            activityLog.setInterfacePath(
                    activityLog.getInterfacePath() + "UncategorizedSQLException|BadSqlGrammarException");
            activityLog.setInterfaceName(activityLog.getInterfaceName() + "数据库异常");
            logService.saveErrorLogAsync(activityLog, e);
            // 业务异常
        } else if ((e instanceof BusinessException)) {

            max_stack_trace_depth = 1;
            // 自定义code异常
        } else if ((e instanceof CustomException)) {
            if (msg.length() > OrderConstant.ERORR_MSG_MAX_LENGTH) {
                msg = OrderConstant.ERORR_SYSTEM_BUSY;
                alarm = true;
                alarmTitle = OrderConstant.ERORR_ALARMTITLE_CustomException;

                Log activityLog = logService.getLog(null, null, null, InterfaceEnum.ORDER_RUNTIME_EXCEPTION);
                activityLog.setInterfacePath(activityLog.getInterfacePath() + "CustomException");
                activityLog.setInterfaceName(activityLog.getInterfaceName() + "自定义code异常");
                logService.saveErrorLogAsync(activityLog, e);
            }
            // 调用外部系统返回错误编码处理异常
        } else if ((e instanceof ExternSystemErrorCodeException)) {
            alarmTitle = OrderConstant.ERORR_ALARMTITLE_EXTERNSYSTEMERRORCODEEXCEPTION;
            alarm = ((ExternSystemErrorCodeException) e).getAlarm();
            alarmMsg = ((ExternSystemErrorCodeException) e).getExternSystemMessage();
            if (alarm) {
                Log activityLog = logService.getLog(null, null, null, InterfaceEnum.ORDER_RUNTIME_EXCEPTION);
                activityLog.setInterfacePath(activityLog.getInterfacePath() + "ExternSystemErrorCodeException");
                activityLog.setInterfaceName(activityLog.getInterfaceName() + "调用外部系统返回错误");
                logService.saveErrorLogAsync(activityLog, e);
            }
            // 其他异常
        } else {
            if (msg.length() > OrderConstant.ERORR_MSG_MAX_LENGTH) {
                msg = OrderConstant.ERORR_SYSTEM_BUSY;
                alarm = true;
                alarmTitle = OrderConstant.ERORR_ALARMTITLE_OTHEREXCEPTION;

                Log activityLog = logService.getLog(null, null, null, InterfaceEnum.ORDER_RUNTIME_EXCEPTION);
                activityLog.setInterfacePath(activityLog.getInterfacePath() + "Other");
                activityLog.setInterfaceName(activityLog.getInterfaceName() + "其他异常");
                logService.saveErrorLogAsync(activityLog, e);
            }
            max_stack_trace_depth = 1;
        }

        if (max_stack_trace_depth != -1 && e.getStackTrace().length > max_stack_trace_depth) {
            StackTraceElement[] stackTraceElements = new StackTraceElement[max_stack_trace_depth];
            System.arraycopy(e.getStackTrace(), 0, stackTraceElements, 0, max_stack_trace_depth);
            e.setStackTrace(stackTraceElements);
        }

        if (max_stack_trace_depth != -1 && e.getCause() != null && e.getCause().getStackTrace() != null
                && e.getCause().getStackTrace().length > max_stack_trace_depth) {
            StackTraceElement[] stackTraceElements = new StackTraceElement[max_stack_trace_depth];
            System.arraycopy(e.getCause().getStackTrace(), 0, stackTraceElements, 0, max_stack_trace_depth);
            e.getCause().setStackTrace(stackTraceElements);
        }

        log.error(msg, e);

        if (alarm) {
            this.addExceptionCache(alarmTitle, e, alarmMsg);
        }

        BaseResponse resp = BaseResponse.Builder.build(null, msg);
        if (e instanceof CustomException) {
            CustomException customException = (CustomException) e;
            resp.setCode(customException.getCode());
            resp.setData(customException.getData());
        }

        return resp;
    }

    /**
     * 同步添加到异常缓存
     */
    public synchronized void addExceptionCache(String alarmTitle, Exception e, String alarmMsg) {
        String content = StrUtil.format("时间：{},msg信息：{},Exception信息:{}",
                DateUtil.now(),
                null == alarmMsg ? e.getMessage() : alarmMsg,
                e.toString());
        OrderExceptionCache.put(alarmTitle, content);
    }
}
