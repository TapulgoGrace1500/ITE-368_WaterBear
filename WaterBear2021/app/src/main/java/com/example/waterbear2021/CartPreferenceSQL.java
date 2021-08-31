package com.example.waterbear2021;

public class CartPreferenceSQL{

    private String cartID, buyerID, buyerName, buyerAddress, storeID, storeName, productType, productID, productName, status;
    private Double productPrice;
    private Double TotalPrice;
    private int quantity;

    public CartPreferenceSQL(String cartID, String buyerID, String buyerName, String buyerAddress, String storeID, String storeName, String productType, String productID, String productName,
                             String status, int quantity, Double productPrice, Double TotalPrice) {
        this.cartID = cartID;
        this.buyerID = buyerID;
        this.storeID = storeID;
        this.productID = productID;
        this.productType = productType;
        this.productName = productName;
        this.status = status;
        this.quantity = quantity;
        this.productPrice = productPrice;
        this.TotalPrice = TotalPrice;
        this.buyerName = buyerName;
        this.storeName = storeName;
        this.buyerAddress = buyerAddress;
    }

    public CartPreferenceSQL(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
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

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        TotalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
