package com.hossam.hasanin.test_cart.deep_link_product;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hossam.hasanin.test_cart.datasources.FirebaseDataSource;
import com.hossam.hasanin.test_cart.datasources.MainDataSource;
import com.hossam.hasanin.test_cart.models.Product;

import java.util.Map;

public class DeepLinkViewModel extends ViewModel {
    private MutableLiveData<Product> _product = new MutableLiveData<>(new Product(null , null , 0.0 , null , 0));
    LiveData<Product> product = _product;
    private MainDataSource dataSource = new FirebaseDataSource("");
    Boolean previousConnectionStatus = true;


    public void getProduct(String productId){
        dataSource.getProduct(productId).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (!task.getResult().getDocuments().isEmpty()){
                    Product product = task.getResult().getDocuments().get(0).toObject(Product.class);
                    _product.postValue(product);
                } else {
                    _product.postValue(null);
                }
            } else {
                task.getException().printStackTrace();
                _product.postValue(null);
            }
        });
    }
}
