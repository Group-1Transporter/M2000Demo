package com.example.m2000example.models;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    public int id;
    public String menuName;
    public boolean hasChildren, isHidden;
    public Menu parentMenu;
    public List<Menu> childMenus;

    public Menu(int id, String menuName, boolean isHidden, boolean hasChildren, Menu parentMenu) {

        this.id = id;
        this.menuName = menuName;
        this.isHidden = isHidden;
        this.hasChildren = hasChildren;
        this.parentMenu = parentMenu;
        this.childMenus = new ArrayList<>();
    }

    public Menu(String menuName) {
        this.menuName = menuName;
    }
}
