package com.example.waterbear2021;

import java.text.DecimalFormat;

public class ProductList {
    private String sellerID, storeName, productID, productType, productName;
    private double productPrice;

    public ProductList(String sellerID, String storeName,String productID, String productType, String productName, double productPrice) {
        this.sellerID = sellerID;
        this.productID = productID;
        this.productType = productType;
        this.productName = productName;
        this.productPrice = productPrice;
        this.storeName = storeName;

    }
    public ProductList(){}

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
}
