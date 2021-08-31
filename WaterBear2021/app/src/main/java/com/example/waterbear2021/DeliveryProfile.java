package com.example.waterbear2021;

public class DeliveryProfile {

    private String DeliveryId, OwnerId, FirstName, LastName, ContactNumber;

    public DeliveryProfile(String deliveryId, String ownerId, String firstName, String lastName, String contactNumber) {
        this.DeliveryId = deliveryId;
        this.OwnerId = ownerId;
        this.FirstName = firstName;
        this.LastName = lastName;
        this.ContactNumber = contactNumber;
    }
    public DeliveryProfile(){}

    public String getDeliveryId() {
        return DeliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        DeliveryId = deliveryId;
    }

    public String getOwnerId() {
        return OwnerId;
    }

    public void setOwnerId(String ownerId) {
        OwnerId = ownerId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }
}
