package com.example.admin.metis;

public class Product {

    enum PRODUCT_TYPE {TOPIC, HEADER, ITEM };

    private String name;
    private String price;
    private PRODUCT_TYPE product_type;
    private String amount;

    public Product(String name, String price, PRODUCT_TYPE product_type,String amount){
        this.name = name;
        this.price = price;
        this.product_type = product_type;
        this.amount = amount;
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

    public PRODUCT_TYPE getProductType() {
        return product_type;
    }


}
