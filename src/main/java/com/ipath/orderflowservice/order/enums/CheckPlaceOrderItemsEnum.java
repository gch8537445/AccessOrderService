package com.ipath.orderflowservice.order.enums;

import java.util.stream.Stream;

public enum CheckPlaceOrderItemsEnum {

    CHECK_1100("1100","base地限制");

    private String code;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getcode() {
        return code;
    }

    public void setcode(String code) {
        this.code = code;
    }

    CheckPlaceOrderItemsEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static CheckPlaceOrderItemsEnum getByCode(String code) {
        return Stream.of(values()).filter(e -> e.code.equals(code)).findFirst().orElse(null);
    }
}
