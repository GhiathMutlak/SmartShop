package com.applefish.smartshop.classes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Amro on 17/12/2016.
 */

public class Store implements Parcelable{

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

    protected Store(Parcel in) {
        id = in.readInt();
        storeName = in.readString();
        logoUrl = in.readString();
        logo = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

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

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(id);
        parcel.writeString(storeName);
        parcel.writeString(logoUrl);
        parcel.writeParcelable(logo, i);
        
    }
}
