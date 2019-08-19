package com.wensheng.sso.module.dao.mysql.auto.entity;

public class OauthCode {
    private String code;

    private byte[] authentication;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public byte[] getAuthentication() {
        return authentication;
    }

    public void setAuthentication(byte[] authentication) {
        this.authentication = authentication;
    }
}