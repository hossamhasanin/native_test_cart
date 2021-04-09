package com.hossam.hasanin.test_cart.datasources;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hossam.hasanin.test_cart.models.Product;

public class FirebaseDataSource implements MainDataSource {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    @Override
    public Task<QuerySnapshot> getAllProducts() {
        Query query = firestore.collection("carts");
        return query.get();
    }

    @Override
    public Task<Void> updateProduct(Product product) {
        DocumentReference query = firestore.collection("carts").document(product.getId());

        return query.update("itemCount" , product.getItemCount());
    }
}
