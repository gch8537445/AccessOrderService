package com.ipath.orderflowservice.order.enums;

import java.util.stream.Stream;

public enum CompanyLimitTypexEnum {

    /**
     * 金额管控_单次限额/个人单次额度
     */
    COMPANY_LIMIT_TYPE_1(1, "金额管控_单次限额/个人单次额度", "3005"),
    /**
     * 金额管控_日限额/个人当天总额度
     */
    COMPANY_LIMIT_TYPE_2(2, "金额管控_日限额/个人当天总额度", "3025"),
    /**
     * 金额管控_月限额
     */
    COMPANY_LIMIT_TYPE_3(3, "金额管控_月限额", "3051"),
    /**
     * 金额管控_申请单总限额
     */
    COMPANY_LIMIT_TYPE_4(4, "金额管控_申请单总限额", "3052"),
    /**
     * 次数限制_申请单周期内打车次数
     */
    COMPANY_LIMIT_TYPE_5(5, "次数限制_申请单周期内打车次数", "3053"),
    /**
     * 次数限制_月度打车次数
     */
    COMPANY_LIMIT_TYPE_6(6, "次数限制_月度打车次数", "3054"),
    /**
     * 地点管控/可用地点
     */
    COMPANY_LIMIT_TYPE_7(7, "地点管控/可用地点", "3011"),
    /**
     * 申请单有效期_开始时间/可用车起始时间
     */
    COMPANY_LIMIT_TYPE_8(8, "申请单有效期_开始时间/可用车起始时间", "3021"),
    /**
     * 申请单有效期_结束/可用车结束时间
     */
    COMPANY_LIMIT_TYPE_9(9, "申请单有效期_结束/可用车结束时间", "3022"),
    /**
     * 用车类型/用车类型
     */
    COMPANY_LIMIT_TYPE_10(10, "用车类型/可用车类型", "3002"),
    /**
     * 可用城市/可用城市
     */
    COMPANY_LIMIT_TYPE_11(11, "可用城市/可用城市", "3009"),
    /**
     * 金额管控_周额度
     */
    COMPANY_LIMIT_TYPE_12(12, "金额管控_周限额/周额度", "3057"),
    ;

    private int type;
    private String name;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    CompanyLimitTypexEnum(int type, String name, String key) {
        this.type = type;
        this.name = name;
        this.key = key;
    }

    public static CompanyLimitTypexEnum getByCode(int type) {
        return Stream.of(values()).filter(e -> e.type == type).findFirst().orElse(null);
    }
}
