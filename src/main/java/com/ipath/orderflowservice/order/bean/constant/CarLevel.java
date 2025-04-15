package com.ipath.orderflowservice.order.bean.constant;

public enum CarLevel {

//    NORMAL("普通型", 1), COMFORTABLE("舒适型", 2), BUSINESS("商务型", 3), LUXURY("豪华型", 4), PRIOR("优享型", 9), BUS("巴士", 10);

    NORMAL("普通型","Economy", 1), COMFORTABLE("舒适型","Comfort", 2), PREMIUM("商务型","Premium", 3), LUXURY("豪华型","Luxury", 4), PRIOR("优享型","Premium", 9), BUS("巴士","Bus", 10);

    private String name;
    private String nameEn;
    private Integer code;

//    private CarLevel(String name, Integer code) {
//        this.name = name;
//        this.code = code;
//    }

    private CarLevel(String name,String name_en, Integer code) {
        this.name = name;
        this.nameEn=name_en;
        this.code = code;
    }

    public static String getName(Integer code) {
        for (CarLevel carLevel: CarLevel.values()) {
            if (carLevel.getCode().equals(code)) {
                return carLevel.name;
            }
        }
        return null;
    }

    public static String getName(Integer code,int language) {
        for (CarLevel carLevel: CarLevel.values()) {
            if (carLevel.getCode().equals(code)) {
                return language==1?carLevel.name: carLevel.nameEn;
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

    public String getNameEn() {
        return nameEn;
    }

    public void setNameeEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
