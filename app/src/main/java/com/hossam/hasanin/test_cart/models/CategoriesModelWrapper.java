package com.hossam.hasanin.test_cart.models;

public class CategoriesModelWrapper {

    private CategoriesModel model;
    private boolean isSelected;

    public CategoriesModelWrapper(boolean isSelected, CategoriesModel model) {
        this.model = model;
        this.isSelected = isSelected;
    }

    public CategoriesModel getModel() {
        return model;
    }

    public void setModel(CategoriesModel model) {
        this.model = model;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
