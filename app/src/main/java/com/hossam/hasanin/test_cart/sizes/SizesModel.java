package com.hossam.hasanin.test_cart.sizes;

public class SizesModel {

    private Integer id;

    private String size;

    private Integer size_id;

    private Integer quantity;


    public SizesModel(Integer size_id, Integer quantity) {
        this.size_id = size_id;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getSize_id() {
        return size_id;
    }

    public void setSize_id(Integer size_id) {
        this.size_id = size_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}