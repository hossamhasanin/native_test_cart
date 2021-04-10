package com.hossam.hasanin.test_cart.adapters;

import com.hossam.hasanin.test_cart.models.Product;
import com.hossam.hasanin.test_cart.models.Store;

import java.util.List;

public interface UpdateItemNumListener {
    void updateItemNum(Product product , List<Store> stores);
    void removeProduct(Product product , List<Store> stores);
}
