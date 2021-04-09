package com.hossam.hasanin.test_cart.datasources;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.hossam.hasanin.test_cart.models.Product;

public interface MainDataSource {
    Task<QuerySnapshot> getAllProducts();
    Task<Void> updateProduct(Product product);

}
