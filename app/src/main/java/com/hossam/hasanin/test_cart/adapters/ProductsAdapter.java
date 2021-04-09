package com.hossam.hasanin.test_cart.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.models.Product;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private final List<Product> products;
    private final StorePriceChanged storeChanged;
    private int storePos;



    private final UpdateItemNumListener listener;

    public ProductsAdapter(List<Product> products, StorePriceChanged storeChanged, int storePos, UpdateItemNumListener listener) {
        this.products = products;
        this.storeChanged = storeChanged;
        this.storePos = storePos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cart, parent, false);

        return new ProductsAdapter.ViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, final int position) {
        final Product product = products.get(position);
        holder.tvProductName.setText(product.getTitle());
        holder.tvPrice.setText(String.valueOf(product.getPrice()));
        holder.tvItemCount.setText(String.valueOf(product.getItemCount()));
        Glide.with(holder.itemView.getContext()).load(Uri.parse(product.getProductImage())).into(holder.imProductImage);

        holder.btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemCount = product.getItemCount();
                itemCount += 1;
                product.setItemCount(itemCount);
                products.set(position , product);
                listener.updateItemNum(product);
                storeChanged.priceChanged(products , storePos);
            }
        });

        holder.btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemCount = product.getItemCount();
                if (itemCount == 1) return;
                itemCount -= 1;
                product.setItemCount(itemCount);
                products.set(position , product);
                listener.updateItemNum(product);
                storeChanged.priceChanged(products , storePos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    public List<Product> getProducts() {
        return products;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvProductName;
        TextView tvItemCount;
        ImageView imProductImage;
        TextView tvPrice;
        Button btAdd;
        Button btRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvProductName = itemView.findViewById(R.id.tv_product_name);
            this.tvItemCount = itemView.findViewById(R.id.tv_item_count);
            this.imProductImage = itemView.findViewById(R.id.im_product_image);
            this.tvPrice = itemView.findViewById(R.id.tv_product_price);
            this.btAdd = itemView.findViewById(R.id.bt_add_item);
            this.btRemove = itemView.findViewById(R.id.bt_remove_item);


        }
    }
}
