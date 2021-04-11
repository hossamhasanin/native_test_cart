package com.hossam.hasanin.test_cart.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.models.Product;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private final List<Product> products;
    private final StorePriceChanged storeChanged;
    private int storePos;
    private LiveData<Boolean> isConnected;




    public ProductsAdapter(List<Product> products, StorePriceChanged storeChanged, int storePos, LiveData<Boolean> isConnected) {
        this.products = products;
        this.storeChanged = storeChanged;
        this.storePos = storePos;
        this.isConnected = isConnected;
    }

    @NonNull
    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cart, parent, false);

        return new ProductsAdapter.ViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, final int position) {
        Product product = products.get(position);
        holder.tvProductName.setText(product.getProductName());
        holder.tvPrice.setText(String.valueOf(product.getProductPrice()));
        holder.tvItemCount.setText(String.valueOf(product.getProductQuantity()));
        Glide.with(holder.itemView.getContext()).load(Uri.parse(product.getProductImage())).into(holder.imProductImage);

        holder.btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected.getValue()) return;
                int itemCount = product.getProductQuantity();
                itemCount += 1;
                product.setProductQuantity(itemCount);

                storeChanged.priceChanged(storePos , position);
            }
        });

        holder.btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected.getValue()) return;
                int itemCount = product.getProductQuantity();
                if (itemCount == 1) {
                    showWantToRemoveDialog(holder.itemView.getContext() , position);
                } else {
                    itemCount -= 1;
                    product.setProductQuantity(itemCount);
                    storeChanged.priceChanged(storePos , position);
                }
            }
        });
    }

    private void showWantToRemoveDialog(Context context , int pos){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to remove this item ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    storeChanged.removeProduct(pos , storePos);
                    dialogInterface.dismiss();
                }).setNegativeButton("No" , (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
        dialog.show();
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
