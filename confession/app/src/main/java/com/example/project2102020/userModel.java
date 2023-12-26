package com.example.project2102020;

public class userModel {

    String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    String userName;
    String uid;
    public userModel(String gender,String userName,String uid){
        this.userName=userName;
        this.uid=uid;
        this.gender=gender;
    }
}
