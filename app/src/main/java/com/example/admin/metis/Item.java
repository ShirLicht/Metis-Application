package com.example.admin.metis;

import android.net.Uri;

public class Item {

    enum ITEM_TYPE {TOPIC, HEADER, PRODUCT };

    private String name;
    private String price;
    private ITEM_TYPE item_type;
    private String amount;
    private Uri userImage;

    public Item(String name, String price, ITEM_TYPE item_type,String amount){
        this.name = name;
        this.price = price;
        this.item_type = item_type;
        this.amount = amount;
    }

    public void setUserImage(Uri userImage){
        this.userImage = userImage;
    }

    public Uri getUserImage(){
        return userImage;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ITEM_TYPE getProductType() {
        return item_type;
    }


}
