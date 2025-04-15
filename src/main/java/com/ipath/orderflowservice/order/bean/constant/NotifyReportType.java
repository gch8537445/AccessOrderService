package com.ipath.orderflowservice.order.bean.constant;

import java.util.Arrays;

public enum NotifyReportType {

    SET_USE_CAR_REASON("setUserCarReason", 1);

    private String name;
    private Integer code;

    private NotifyReportType(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public static String getName(Integer code) {
        for (NotifyReportType carSource: NotifyReportType.values()) {
            if (carSource.getCode().equals(code)) {
                return carSource.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
