package com.applefish.smartshop.classes;

import android.graphics.Bitmap;

/**
 * Created by Amro on 17/12/2016.
 */

public class Store {
    private int id;
    private String storeName;
    private Bitmap logo;

    public Store(int id,String storeName,Bitmap logo)
    {
        this.id=id;
        this.storeName=storeName;
        this.logo=logo;
    }
 public int getId(){return id;}
    public  String getStoreName(){return storeName;}
    public Bitmap getLogo(){return logo;}

}
