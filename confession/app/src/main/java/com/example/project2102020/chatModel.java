package com.example.project2102020;

public class chatModel {


    String uid;

    String userName;
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String message;
    String myUid;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image;
    public chatModel (String userName,String message,String uid,String myUid,String image){


        this.image=image;
        this.myUid=myUid;
        this.uid=uid;
        this.message=message;
        this.userName=userName;
    }
}
