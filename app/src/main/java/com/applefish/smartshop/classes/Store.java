package com.applefish.smartshop.classes;

import android.graphics.Bitmap;

/**
 * Created by Amro on 17/12/2016.
 */

public class Store {

    private int id;
    private String storeName;
    private String logoUrl;
    private Bitmap logo;

    public Store() {

    }

    public Store(int id,String storeName,String logoUrl) {
        this.id=id;
        this.storeName=storeName;
        this.logoUrl = logoUrl;
    }

    public int getId() {
        return id;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }
}
