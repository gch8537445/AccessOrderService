package com.ipath.orderflowservice.config;

import com.ipath.orderflowservice.order.bean.constant.OrderConstant;
import lombok.Data;

/**
 * 调用外部系统返回错误编码处理异常
 */
@Data
public class ExternSystemErrorCodeException extends RuntimeException {

    private String externSystemName;
    private String externSystemInterfaceName;
    private String externSystemMessage;
    private Boolean alarm;

    public ExternSystemErrorCodeException(String externSystemName, String externSystemInterfaceName, String externSystemMessage, String orderMessage) {
        super(orderMessage);
        this.externSystemName = externSystemName;
        this.externSystemInterfaceName = externSystemInterfaceName;
        this.externSystemMessage = externSystemMessage;
        this.alarm = externSystemMessage.length() > OrderConstant.ERORR_MSG_MAX_LENGTH ? true : false;
    }

    public ExternSystemErrorCodeException(String message) {
        super(message);
        this.externSystemName = "";
        this.externSystemInterfaceName = "";
        this.externSystemMessage = "";
        this.alarm = false;
    }

    public String alarmToString() {
        return "调用服务名称：" + externSystemName + ">>接口：" + externSystemInterfaceName;
    }


}
