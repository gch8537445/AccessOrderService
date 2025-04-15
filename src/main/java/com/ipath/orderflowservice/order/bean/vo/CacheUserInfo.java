package com.ipath.orderflowservice.order.bean.vo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class CacheUserInfo {
    private Long id;
    private Long companyId;
    private String nameCn;
    private String nameEn;
    private String staffCode;
    private String socialAppUserId;
    private String deviceId;
    private String phone;
    private String email;
    private Short gender;
    private Integer jobState;
    private Date leaveDate;
    private Short activityState;
    private Boolean delete;
    private String emergencyPhone;
    private Boolean canUseCar;
    private Short useCarMode;
    private Short language;
    private Long lineManagerId;
    private String lineManagerSocialAppUserId;
    private String managerCode;
    private Integer socialAppType;
    private Boolean acceptAgreement;
    private Boolean acceptBusinessAgreement;
    private Long accountId;
    private String mobileMenu;
    private BigDecimal personalQuota;
    private BigDecimal remainQuota;
    private Boolean isLetter;
    private Boolean lowCarbon;
    private Boolean isPrivacy;
    private String wxEntrustPayId;
    private Integer wxEntrustPayState;
    private String disableReason;
    private Date liftingTheBanDate;
    private String backupField1;
    private String backupField2;
    private String backupField3;
    private String backupField4;
    private String backupField5;
    private String backupField6;
    private String backupField7;
    private String backupField8;
    private String backupField9;
    private String backupField10;
}
