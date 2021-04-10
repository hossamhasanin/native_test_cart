package com.hossam.hasanin.test_cart.adapters;

import com.hossam.hasanin.test_cart.models.Product;

import java.util.List;

public interface StorePriceChanged{
    void priceChanged(int storePos , int productPos);
    void removeProduct(int productPos , int storePos);
}