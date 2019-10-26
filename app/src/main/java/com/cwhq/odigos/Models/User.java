package com.cwhq.odigos.Models;

public class User {

    private String userID;
    private String name;
    private String email;
    private String phone;
    private String type;
    private String gender;
    private String imageLink;

    public  User(){
    }

    public User(String id, String name, String email, String phone, String type, String gender, String imageLink) {
        this.userID = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.type = type;
        this.gender = gender;
        this.imageLink = imageLink;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
