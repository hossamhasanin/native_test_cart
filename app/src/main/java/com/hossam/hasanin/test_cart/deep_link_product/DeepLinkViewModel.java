package com.hossam.hasanin.test_cart.deep_link_product;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.hossam.hasanin.test_cart.datasources.FirebaseDataSource;
import com.hossam.hasanin.test_cart.datasources.MainDataSource;
import com.hossam.hasanin.test_cart.models.Product;

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

    public Uri getLongSharedLink(){
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/"))
                .setDomainUriPrefix("https://hasanin.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        return dynamicLinkUri;
    }

    public Task<ShortDynamicLink> getShortLink(){
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.url.com/product/test"))
                .setDomainUriPrefix("https://hasanin.page.link")
                // Set parameters
                // ...
                .buildShortDynamicLink();

        return shortLinkTask;
    }

}
