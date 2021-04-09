package com.hossam.hasanin.test_cart;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartViewModel extends ViewModel {
    private MainDataSource dataSource;
    private MutableLiveData<ViewState> _viewstate = new MutableLiveData();
    LiveData<ViewState> viewstate = _viewstate;

    public CartViewModel(){
        dataSource = new FirebaseDataSource();
        _viewstate.postValue(new ViewState(true , "" , new ArrayList<Store>()));
        getProducts();
    }

    private void getProducts(){
        dataSource.getAllProducts().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    Map<String , Store> stores = new HashMap<>();
                    if (task.getResult().isEmpty()){
                        _viewstate.postValue(_viewstate.getValue().copy(new ArrayList<Store>(), false, null));
                    } else {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            Map<String, Object> map = snapshot.getData();
                            Product product = new Product((String) map.get("id") , (String) map.get("title"), ((Long) map.get("price")).doubleValue(), (String) map.get("productImage"), ((Long) map.get("itemCount")).intValue());
                            String storeName = (String) map.get("storeName");
                            String storeImage = (String) map.get("storeImage");
                            if (stores.containsKey(storeName)) {
                                Store store = stores.get(storeName);
                                store.setStoreName(storeName);
                                store.setStoreImage(storeImage);
                                store.getProducts().add(product);
                            } else {
                                List<Product> products = new ArrayList<>();
                                products.add(product);
                                Store store = new Store(products, storeName, storeImage);
                                stores.put(storeName, store);
                            }
                        }
                        List<Store> finishedStores = new ArrayList<>(stores.values());
                        _viewstate.postValue(_viewstate.getValue().copy(finishedStores, false, null));
                    }
                } else {
                    _viewstate.postValue(_viewstate.getValue().copy(null , false , "There is an error happend"));
                }
            }
        });
    }

    public void updateProduct(Product product){
        dataSource.updateProduct(product);
    }

}
