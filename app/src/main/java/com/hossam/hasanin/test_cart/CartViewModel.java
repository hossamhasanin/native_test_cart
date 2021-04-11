package com.hossam.hasanin.test_cart;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hossam.hasanin.test_cart.datasources.FirebaseDataSource;
import com.hossam.hasanin.test_cart.datasources.MainDataSource;
import com.hossam.hasanin.test_cart.models.Product;
import com.hossam.hasanin.test_cart.models.Store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartViewModel extends ViewModel {
    private MainDataSource dataSource;
    private MutableLiveData<ViewState> _viewstate = new MutableLiveData();
    LiveData<ViewState> viewstate = _viewstate;
    MutableLiveData<Boolean> isConnected = new MutableLiveData(true);



    public CartViewModel(){
        dataSource = new FirebaseDataSource();

        try{
            Boolean isConnected = isConnected();
            this.isConnected.postValue(isConnected);
            _viewstate.postValue(new ViewState(isConnected , isConnected ? "" : "No internet connection available" , new ArrayList<Store>(), 0, 0));

            if (isConnected){
                getProducts();
            }

        }catch ( InterruptedException | IOException e){
            Log.e("CartViewModel" , e.getMessage());
        }

    }

    public void getProducts(){
        dataSource.getAllProducts().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    Map<String , Store> stores = new HashMap<>();
                    if (task.getResult().isEmpty()){
                        _viewstate.postValue(_viewstate.getValue().copy(new ArrayList<Store>(), false, null , null , null));
                    } else {
                        int allItemsCount = 0;
                        double allItemPrice = 0.0;
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            Map<String, Object> map = snapshot.getData();
                            Product product = new Product((String) map.get("productId") , (String) map.get("productName"), ((Long) map.get("productPrice")).doubleValue(), (String) map.get("productImage"), ((Long) map.get("productQuantity")).intValue());
                            String storeName = (String) map.get("storeName");
                            String storeId = (String) map.get("storeId");
                            String storeImage = (String) map.get("storeImage");
                            allItemsCount += product.getProductQuantity();
                            allItemPrice += product.getProductQuantity() * product.getProductPrice();
                            if (stores.containsKey(storeId)) {
                                Store store = stores.get(storeId);
                                store.getProducts().add(product);
                                store.setTotalItemCount(store.getTotalItemCount() + product.getProductQuantity());
                                store.setTotalPrice(store.getTotalPrice() + (product.getProductPrice() * product.getProductQuantity()));
                            } else {
                                List<Product> products = new ArrayList<>();
                                products.add(product);
                                int totalItemCount = product.getProductQuantity();
                                double totalPrice = product.getProductQuantity() * product.getProductPrice();
                                Store store = new Store(storeId, products, storeName, storeImage, totalItemCount, totalPrice);
                                stores.put(storeId, store);
                            }
                        }
                        List<Store> finishedStores = new ArrayList<>(stores.values());
                        _viewstate.postValue(_viewstate.getValue().copy(finishedStores, false, null , allItemsCount , allItemPrice));
                    }
                } else {
                    _viewstate.postValue(_viewstate.getValue().copy(null , false , "There is an error happend" , null , null));
                }
            }
        });
    }

    public void updateProduct(Product product){
        dataSource.updateProduct(product);
    }

    public void removeProduct(Product product){
        dataSource.removeProduct(product);
    }

    public void deleteCart(){
        if (!isConnected.getValue()) return;
        dataSource.deleteCart(_viewstate.getValue().getStores());
        _viewstate.postValue(_viewstate.getValue().copy(new ArrayList<>() , null , null , null , null));
    }


    public void calcTotalPriceCount(List<Store> stores){
        double totalPrice = 0.0;
        int itemCount = 0;
        for (Store store: stores) {
            totalPrice += store.getTotalPrice();
            itemCount += store.getTotalItemCount();
        }
        _viewstate.postValue(_viewstate.getValue().copy(null , null , null , itemCount , totalPrice));
    }

    private boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }
}
