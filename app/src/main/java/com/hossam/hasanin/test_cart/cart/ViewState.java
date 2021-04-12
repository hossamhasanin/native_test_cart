package com.hossam.hasanin.test_cart.cart;

import com.hossam.hasanin.test_cart.models.Store;

import java.util.List;

public class ViewState {
    private Boolean loading;
    private String error;
    private List<Store> stores;
    private int totalItemCount;
    private double totalPrice;

    public ViewState(Boolean loading, String error, List<Store> stores, int totalItemCount, double totalPrice) {
        this.loading = loading;
        this.error = error;
        this.stores = stores;
        this.totalItemCount = totalItemCount;
        this.totalPrice = totalPrice;
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


    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    ViewState copy(List<Store> stores , Boolean loading , String error , Integer totalItemCount, Double totalPrice){
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

        int _totalItemCount = this.totalItemCount;
        if (totalItemCount != null){
            _totalItemCount = totalItemCount;
        }

        double _totalPrice = this.totalPrice;
        if (totalPrice != null){
            _totalPrice = totalPrice;
        }
        return new ViewState(_loading , _error , _stores, _totalItemCount, _totalPrice);
    }
}
