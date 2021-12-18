package com.example.m2000example.models;

public class Data {

    private int id;
    String title;
    private String firstLevelFlags;
    private String secondLevelFlags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstLevelFlags() {
        return firstLevelFlags;
    }

    public void setFirstLevelFlags(String firstLevelFlags) {
        this.firstLevelFlags = firstLevelFlags;
    }

    public String getSecondLevelFlags() {
        return secondLevelFlags;
    }

    public void setSecondLevelFlags(String secondLevelFlags) {
        this.secondLevelFlags = secondLevelFlags;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", firstLevelFlags='" + firstLevelFlags + '\'' +
                ", secondLevelFlags='" + secondLevelFlags + '\'' +
                '}';
    }
}
