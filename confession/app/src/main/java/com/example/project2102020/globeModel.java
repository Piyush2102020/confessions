package com.example.project2102020;

public class globeModel {

    String name;
    String username;
    String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    String instagram;

    public globeModel(String name,String username,String image,String instagram){

        this.name=name;
        this.username=username;
        this.image=image;
        this.instagram=instagram;
    }
}
