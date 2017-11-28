package com.microtelecom.retrofittest.Model;

/**
 * Created by pratik on 11/27/2017.
 */

public class User {

    String name, email, phoneNo, photoURI, uid;

    boolean isLoggedIn;

    public User(String name, String email, String phoneNo, String photoURI, boolean isLoggedIn) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.photoURI = photoURI;
        this.isLoggedIn = isLoggedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
