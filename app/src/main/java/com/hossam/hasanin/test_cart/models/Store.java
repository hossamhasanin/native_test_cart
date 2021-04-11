package com.hossam.hasanin.test_cart.models;

import java.util.List;

public class Store {
    private String storeId;
    private List<Product> products;
    private String storeName;
    private String storeImage;
    private int totalItemCount;
    private double totalPrice;

    public Store(String storeId, List<Product> products, String storeName, String storeImage, int totalItemCount, double totalPrice) {
        this.storeId = storeId;
        this.products = products;
        this.storeName = storeName;
        this.storeImage = storeImage;
        this.totalItemCount = totalItemCount;
        this.totalPrice = totalPrice;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
