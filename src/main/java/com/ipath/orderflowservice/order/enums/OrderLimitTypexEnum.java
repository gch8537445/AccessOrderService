package com.ipath.orderflowservice.order.enums;

import java.util.stream.Stream;

public enum OrderLimitTypexEnum {

    PLACEORDER_LIMIT_COMPANY("polc","下单限制公司配置"),
    PLACEORDER_AIRPORT_CITY_MAPPING("poacm","机场城市映射配置"),
    PLACEORDER_UPGRADE_CAR_LEVL("poucl","开启免费升舱的公司配置"),
    PLACEORDER_BIG_AMOUNT_PRE_PAY("pobapp","开启大额预付的公司配置"),
    PLACEORDER_CITY_CONVERT("pocc","开启城市转换的公司配置"),
    PLACEORDER_NOTIFY("pon","开启下单通知的公司配置"),
    SETTING_FEE("poccfee","开启配置费用公司配置"),
    PLACEORDER_LIMIT_ONE_H5("poloh5","h5一次登录只允许一单配置");


    private String type;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    OrderLimitTypexEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }


    public static OrderLimitTypexEnum getByCode(String type) {
        return Stream.of(values()).filter(e -> e.type.equals(type)).findFirst().orElse(null);
    }
}
