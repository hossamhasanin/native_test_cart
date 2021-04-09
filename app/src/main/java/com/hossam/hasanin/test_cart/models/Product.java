package com.hossam.hasanin.test_cart.models;

public class Product {
    private String id;
    private String title;
    private double price;
    private String productImage;
    private int itemCount;

    public Product(String id, String title, double price, String productImage, int itemCount) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.productImage = productImage;
        this.itemCount = itemCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
