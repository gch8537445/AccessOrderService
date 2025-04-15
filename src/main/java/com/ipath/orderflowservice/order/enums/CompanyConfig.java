package com.ipath.orderflowservice.order.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public enum CompanyConfig {


    DISPATCH_MODE("1091", "用车配置-可选派车模式"),
    AUTO_COMPLAINT_MODE("1092", "用车配置-自动投诉设置"),
    SWITCH_PREPAY("prepaysettings.enabled", "基础设置-大额预付开关"),
    PREPAY_AMOUNT("prepaysettings.maxamountlimit", "基础设置-大额预付阈值"),
    EXPENSE_SETTINGS("expensesettings", "基础设置-费用配置"),
    SWITCH_FREE_UPGRADE("freeupgradesettings.isopen", "VIP配置项-免费升舱-开关"),
    UPGRADE_DELAY_TYPE("freeupgradesettings.calltype", "VIP配置项-免费升舱-呼叫类型"),
    UPGRADE_DELAY_SETTING("freeupgradesettings.calldelay", "VIP配置项-免费升舱-呼叫设置"),
    SWITCH_HAND_SHAKE("handshakesettings.isopen", "VIP配置项-全平台握手-开关"),
    ESTIAMTE_DISCOUNT("803", "预估规则-预估价干预"),
    AUTO_COMPLAINT_FOR_CANCEL_FEE("long_fee", "长途费");
    

    private String code;
    private String name;

    CompanyConfig(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static CompanyConfig getByName(String name) {
        return Stream.of(values()).filter(e -> StringUtils.equals(e.name, name)).findFirst().orElse(null);
    }

    public static CompanyConfig getByCode(String code) {
        return Stream.of(values()).filter(e -> StringUtils.equals(e.code, code)).findFirst().orElse(null);
    }
}
