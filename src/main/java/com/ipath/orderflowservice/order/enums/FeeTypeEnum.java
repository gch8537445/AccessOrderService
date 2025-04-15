package com.ipath.orderflowservice.order.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public enum FeeTypeEnum {


    start_fee("start_fee", "起步费", "Initial charge"),
    distance_fee("distance_fee", "里程费", "Mileage fee"),
    time_fee("time_fee", "时长费", "Time charge"),
    long_fee("long_fee", "长途费", "Long-distance fare"),
    over_distance_fee("over_distance_fee", "（平峰）超里程费", "(flat peak) extra mileage fee"),
    high_speed_fee("high_speed_fee", "高速费", "Highway fee"),
    insurance_fee("insurance_fee", "保险费", "premium"),
    high_service_fee("high_service_fee", "高速服务费", "High-speed service charge"),
    ordinary_fee("ordinary_fee", "普通时段", "Ordinary period");

    private String code;
    private String name;
    private String nameEn;

    FeeTypeEnum(String code, String name, String nameEn) {
        this.code = code;
        this.name = name;
        this.nameEn = nameEn;
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

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public static FeeTypeEnum getByName(String name) {
        return Stream.of(values()).filter(e -> StringUtils.equals(e.name, name)).findFirst().orElse(null);
    }

    public static FeeTypeEnum getByCode(String code) {
        return Stream.of(values()).filter(e -> StringUtils.equals(e.code, code)).findFirst().orElse(null);
    }
}
