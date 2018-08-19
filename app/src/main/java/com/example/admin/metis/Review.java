package com.example.admin.metis;

import android.net.Uri;

public class Review {

    private String name;
    private Uri image;
    private String review;
    private int rate;


    public Review(String name, Uri image, String review, int rate) {
        this.name = name;
        this.image = image;
        this.review = review;
        this.rate = rate;
    }

    public Review(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
