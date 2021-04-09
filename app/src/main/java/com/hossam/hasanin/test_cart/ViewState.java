package com.hossam.hasanin.test_cart;

import com.hossam.hasanin.test_cart.models.Store;

import java.util.List;

public class ViewState {
    private Boolean loading;
    private String error;
    private List<Store> stores;

    public ViewState(Boolean loading, String error, List<Store> stores) {
        this.loading = loading;
        this.error = error;
        this.stores = stores;
    }

    public Boolean getLoading() {
        return loading;
    }

    public void setLoading(Boolean loading) {
        this.loading = loading;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    ViewState copy(List<Store> stores , Boolean loading , String error){
        List<Store> _stores = this.stores;
        if (stores != null){
            _stores = stores;
        }
        Boolean _loading = this.loading;
        if (loading != null){
            _loading = loading;
        }

        String _error = this.error;
        if (error != null){
            _error = error;
        }
        return new ViewState(_loading , _error , _stores);
    }
}
