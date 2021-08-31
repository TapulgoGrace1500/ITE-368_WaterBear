package com.example.waterbear2021;

public class DeliveryModel {

    private String Store_ID, Delivery_ID, delivery_firstName, delivery_lastName, delivery_contactNumber, delivery_email, delivery_password;

    public DeliveryModel(String store_ID, String delivery_ID, String delivery_firstName, String delivery_lastName, String delivery_contactNumber, String delivery_email, String delivery_password) {
        this.Store_ID = store_ID;
        this.Delivery_ID = delivery_ID;
        this.delivery_firstName = delivery_firstName;
        this.delivery_lastName = delivery_lastName;
        this.delivery_contactNumber = delivery_contactNumber;
        this.delivery_email = delivery_email;
        this.delivery_password = delivery_password;
    }

    public String getStore_ID() {
        return Store_ID;
    }

    public void setStore_ID(String store_ID) {
        this.Store_ID = store_ID;
    }

    public String getDelivery_ID() {
        return Delivery_ID;
    }

    public void setDelivery_ID(String delivery_ID) {
        this.Delivery_ID = delivery_ID;
    }

    public String getDelivery_firstName() {
        return delivery_firstName;
    }

    public void setDelivery_firstName(String delivery_firstName) {
        this.delivery_firstName = delivery_firstName;
    }

    public String getDelivery_lastName() {
        return delivery_lastName;
    }

    public void setDelivery_lastName(String delivery_lastName) {
        this.delivery_lastName = delivery_lastName;
    }

    public String getDelivery_contactNumber() {
        return delivery_contactNumber;
    }

    public void setDelivery_contactNumber(String delivery_contactNumber) {
        this.delivery_contactNumber = delivery_contactNumber;
    }

    public String getDelivery_email() {
        return delivery_email;
    }

    public void setDelivery_email(String delivery_email) {
        this.delivery_email = delivery_email;
    }

    public String getDelivery_password() {
        return delivery_password;
    }

    public void setDelivery_password(String delivery_password) {
        this.delivery_password = delivery_password;
    }
}
