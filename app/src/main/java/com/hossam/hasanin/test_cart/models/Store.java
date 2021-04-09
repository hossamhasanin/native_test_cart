package com.hossam.hasanin.test_cart.models;

import java.util.List;

public class Store {
    private List<Product> products;
    private String storeName;
    private String storeImage;

    public Store(List<Product> products, String storeName, String storeImage) {
        this.products = products;
        this.storeName = storeName;
        this.storeImage = storeImage;
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
}
