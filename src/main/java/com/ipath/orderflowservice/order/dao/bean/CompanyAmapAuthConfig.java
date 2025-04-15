package com.ipath.orderflowservice.order.dao.bean;

import java.io.Serializable;

public class CompanyAmapAuthConfig implements Serializable {
    private Long id;

    private String companyAuthId;

    private String secretKey;

    private String eid;

    private String keyt;

    private Integer ent;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyAuthId() {
        return companyAuthId;
    }

    public void setCompanyAuthId(String companyAuthId) {
        this.companyAuthId = companyAuthId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getKeyt() {
        return keyt;
    }

    public void setKeyt(String keyt) {
        this.keyt = keyt;
    }

    public Integer getEnt() {
        return ent;
    }

    public void setEnt(Integer ent) {
        this.ent = ent;
    }
}