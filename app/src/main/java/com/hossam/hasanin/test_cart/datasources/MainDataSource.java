package com.hossam.hasanin.test_cart.datasources;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.hossam.hasanin.test_cart.models.Product;
import com.hossam.hasanin.test_cart.models.Store;

import java.util.List;

public interface MainDataSource {
    Task<QuerySnapshot> getAllProducts();
    Task<Void> updateProduct(Product product);
    Task<Void> removeProduct(Product product);
    Task<Void> deleteCart(List<Store> stores);
    Task<Void> addProduct(Product product);

    Task<QuerySnapshot> getProduct(String productName);

}
