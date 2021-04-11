package com.hossam.hasanin.test_cart.datasources;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hossam.hasanin.test_cart.models.Product;
import com.hossam.hasanin.test_cart.models.Store;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataSource implements MainDataSource {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    @Override
    public Task<QuerySnapshot> getAllProducts() {
        Query query = firestore.collection("carts");
        return query.get();
    }

    @Override
    public Task<Void> updateProduct(Product product) {
        DocumentReference query = firestore.collection("carts").document(product.getProductId());

        return query.update("productQuantity" , product.getProductQuantity());
    }

    @Override
    public Task<Void> removeProduct(Product product) {
        DocumentReference query = firestore.collection("carts").document(product.getProductId());

        return query.delete();
    }

    @Override
    public Task<Void> deleteCart(List<Store> stores) {
        CollectionReference query = firestore.collection("carts");

        return firestore.runTransaction(transaction -> {
            List<Product> productList = new ArrayList<>();
            for (Store store: stores) {
                productList.addAll(store.getProducts());
            }
            for (Product product : productList) {
                transaction.delete(query.document(product.getProductId()));
            }
            return null;
        });
    }
}
