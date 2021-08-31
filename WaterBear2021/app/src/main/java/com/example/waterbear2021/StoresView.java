package com.example.waterbear2021;

import com.google.firebase.firestore.Exclude;

public class StoresView extends UserProfile{

    private String StoreID, title, descript, image;

    public StoresView(String title, String descript, String image, String storeID) {
        this.title = title;
        this.descript = descript;
        this.StoreID = storeID;
        this.image = image;
    }

    public StoresView() {
    }

    @Exclude
    public String getStoreID() {
        return StoreID;
    }

    public void setStoreID(String storeID) {
        StoreID = storeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
