package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;

public class CityMapping implements Serializable {
    private Long id;

    private Long companyId;

    private String ourCityId;

    private String ourCityName;

    private String partnerCityId;

    private String partnerCityName;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getOurCityId() {
        return ourCityId;
    }

    public void setOurCityId(String ourCityId) {
        this.ourCityId = ourCityId;
    }

    public String getOurCityName() {
        return ourCityName;
    }

    public void setOurCityName(String ourCityName) {
        this.ourCityName = ourCityName;
    }

    public String getPartnerCityId() {
        return partnerCityId;
    }

    public void setPartnerCityId(String partnerCityId) {
        this.partnerCityId = partnerCityId;
    }

    public String getPartnerCityName() {
        return partnerCityName;
    }

    public void setPartnerCityName(String partnerCityName) {
        this.partnerCityName = partnerCityName;
    }
}