package com.hossam.hasanin.test_cart.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.models.Product;
import com.hossam.hasanin.test_cart.models.Store;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.ViewHolder> implements StorePriceChanged {
    private final List<Store> stores;
    private final UpdateItemNumListener listener;

    public StoresAdapter(List<Store> stores, UpdateItemNumListener listener) {
        this.stores = stores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoresAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresAdapter.ViewHolder holder, int position) {
        Store store = stores.get(position);
        ProductsAdapter productsAdapter = new ProductsAdapter(store.getProducts(), this, position, listener);
        holder.storeName.setText(store.getStoreName());
        Glide.with(holder.itemView.getContext()).load(Uri.parse(store.getStoreImage())).into(holder.storeImage);
        holder.productsRec.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.productsRec.setAdapter(productsAdapter);
        holder.productsRec.setHasFixedSize(true);
        holder.productsRec.setNestedScrollingEnabled(false);

        double price = calcPrice(productsAdapter.getProducts());

        holder.tvPrice.setText(String.valueOf(price));
    }

    private double calcPrice(List<Product> products){
        double price = 0.0;
        for (Product product: products) {
            price += product.getPrice() * product.getItemCount();
        }
        return price;
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    @Override
    public void priceChanged(List<Product> products, int storePos) {
        stores.get(storePos).setProducts(products);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView storeImage;
        TextView storeName;
        RecyclerView productsRec;
        TextView tvPrice;
        TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.storeName = itemView.findViewById(R.id.store_name);
            this.storeImage = itemView.findViewById(R.id.store_im);
            this.productsRec = itemView.findViewById(R.id.products_rec);
            this.tvPrice = itemView.findViewById(R.id.tv_price);
            this.tvDate = itemView.findViewById(R.id.tv_created_at);
        }
    }


}