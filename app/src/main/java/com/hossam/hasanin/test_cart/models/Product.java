package com.hossam.hasanin.test_cart.models;

import java.util.HashMap;
import java.util.Map;

public class Product {
    private String productId;
    private String productName;
    private double productPrice;
    private String productImage;
    private int productQuantity;

    public Product(){}
    public Product(String productId, String productName, double productPrice, String productImage, int productQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.productQuantity = productQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Map<String , Object> toMap(){
        return new HashMap<String , Object>(){
            {
                put("productId" , productId);
                put("productName" , productName);
                put("productImage" , productImage);
                put("productPrice" , productPrice);
                put("productQuantity" , productQuantity);
            }
        };
    }
}
