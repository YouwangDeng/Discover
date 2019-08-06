package com.youwangd.productsearch;

public class Product {
    private String img;
    private String title;
    private String zipcode;
    private String shipping;
    private String condition;
    private String price;
    private String itemId;
    private String daysLeft;
    private String viewURL;
    private String categotyId;

    public Product(String itemId, String categotyId, String img, String title, String shipping, String daysLeft, String price, String viewURL) {
        this.itemId = itemId;
        this.categotyId = categotyId;
        this.img = img;
        this.title = title;
        this.shipping = shipping;
        this.daysLeft = daysLeft;
        this.price = price;
        this.viewURL = viewURL;
    }

    public Product(String itemId, String img, String title, String zipcode, String shipping, String condition, String price) {
        this.itemId = itemId;
        this.img = img;
        this.title = title;
        this.zipcode = zipcode;
        this.shipping = shipping;
        this.condition = condition;
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public String getImg() {
        return this.img;
    }

    public String getTitle() {
        return this.title;
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public String getShipping() {
        return this.shipping;
    }

    public String getCondition() {
        return this.condition;
    }

    public String getPrice() {
        return this.price;
    }

    public String getDaysLeft() {
        return daysLeft;
    }

    public String getViewURL() {
        return viewURL;
    }

    public String getCategotyId() {
        return categotyId;
    }
}

