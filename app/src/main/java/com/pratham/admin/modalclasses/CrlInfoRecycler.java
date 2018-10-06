package com.pratham.admin.modalclasses;

public class CrlInfoRecycler {
    String crlId;
    String crlName;
    String crlUserName;

    public CrlInfoRecycler(String crlUserName) {
        this.crlUserName = crlUserName;
    }

    public CrlInfoRecycler(String crlId, String crlName, String crlUserName) {
        this.crlId = crlId;
        this.crlName = crlName;
        this.crlUserName = crlUserName;
    }

    @Override
    public String toString() {
        return  crlUserName ;
    }

    public String getCrlId() {
        return crlId;
    }

    public void setCrlId(String crlId) {
        this.crlId = crlId;
    }

    public String getCrlName() {
        return crlName;
    }

    public void setCrlName(String crlName) {
        this.crlName = crlName;
    }

    public String getCrlUserName() {
        return crlUserName;
    }

    public void setCrlUserName(String crlUserName) {
        this.crlUserName = crlUserName;
    }
}
