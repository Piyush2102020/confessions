package com.example.project2102020;

public class model {

    String message;
    long likes;
    long comments;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    String time;
    String id;
    Boolean isLiked;

    String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public  model(String message, long likes, long comments, String time, Boolean isLiked, String id, String image){

        this.image=image;
        this.comments=comments;
        this.id=id;
        this.message=message;
        this.time=time;
        this.isLiked=isLiked;
        this.likes=likes;
    }
}
