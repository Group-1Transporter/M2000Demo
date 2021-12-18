package com.example.m2000example.models;

public class TempData {

    private String ids;
    private String responce;

    public TempData(String ids, String responce) {
        this.ids = ids;
        this.responce = responce;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getResponce() {
        return responce;
    }

    public void setResponce(String responce) {
        this.responce = responce;
    }
}
