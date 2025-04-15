package com.ipath.orderflowservice.order.bean.param;

import lombok.Data;

@Data
public class CheckOrderParamResult {
    private Boolean success;
    private String msg;

    public static CheckOrderParamResult success() {
        CheckOrderParamResult result = new CheckOrderParamResult();
        result.setSuccess(true);
        result.setMsg("");
        return result;
    }

    public static CheckOrderParamResult error(String msg) {
        CheckOrderParamResult result = new CheckOrderParamResult();
        result.setSuccess(false);
        result.setMsg(msg);
        return result;
    }

}
