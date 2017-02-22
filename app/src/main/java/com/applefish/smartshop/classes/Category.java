package com.applefish.smartshop.classes;

/**
 * Created by Amro on 16/02/2017.
 */

public class Category {
    private int id;
    private String name;
    private String [] items;

    public Category(int id, String name, String[] items) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }
}
