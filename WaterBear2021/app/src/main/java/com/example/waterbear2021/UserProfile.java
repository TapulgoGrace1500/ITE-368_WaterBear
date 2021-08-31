package com.example.waterbear2021;

public class UserProfile {

    String FirstName, LastName, Address, ContactNumber, Email, Password;
    String UserID;

    public UserProfile(){

    }

    public UserProfile(String firstName, String lastName, String address, String contactNumber, String email, String password, String userID) {
        FirstName = firstName;
        LastName = lastName;
        Address = address;
        ContactNumber = contactNumber;
        Email = email;
        Password = password;
        UserID = userID;
    }

    public String getUserBuyerID() {
        return UserID;
    }

    public void setUserBuyerID(String userID) {
        UserID = userID;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
