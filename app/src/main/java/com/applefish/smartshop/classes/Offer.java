package com.applefish.smartshop.classes;

import java.util.Date;

/**
 * Created by Ghiath on 12/22/2016.
 */

public class Offer {

    private int id;
    private String title;
    private Date date;
    private int numberOfViews;
    private String PDF_URL;
    private String coverURL;
    private int numberOfPages;
    private int storeID;

    public Offer() {

    }

    public Offer(String title, int id, Date date, int numberOfViews, String PDF_URL, String coverURL, int numberOfPages, int storeID) {
        this.title = title;
        this.id = id;
        this.date = date;
        this.numberOfViews = numberOfViews;
        this.PDF_URL = PDF_URL;
        this.coverURL = coverURL;
        this.numberOfPages = numberOfPages;
        this.storeID = storeID;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPDF_URL() {
        return PDF_URL;
    }

    public void setPDF_URL(String PDF_URL) {
        this.PDF_URL = PDF_URL;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
}

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }
}
