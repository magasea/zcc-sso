package com.wensheng.sso.module.dao.mysql.auto.entity;

public class AmcDept {
    private Long id;

    private Long cmpyId;

    private String name;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCmpyId() {
        return cmpyId;
    }

    public void setCmpyId(Long cmpyId) {
        this.cmpyId = cmpyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}