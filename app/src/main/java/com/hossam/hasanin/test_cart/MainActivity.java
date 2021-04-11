package com.hossam.hasanin.test_cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hossam.hasanin.test_cart.adapters.StoresAdapter;
import com.hossam.hasanin.test_cart.adapters.UpdateItemNumListener;
import com.hossam.hasanin.test_cart.models.Product;
import com.hossam.hasanin.test_cart.models.Store;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UpdateItemNumListener {

    ProgressBar barLoading;
    TextView tvMessage;
    TextView tvPrice;
    TextView tvItemCount;
    RecyclerView recStores;
    ImageButton btDelete;
    private CartViewModel viewModel;
    private UpdateItemNumListener listener = this;
    List<Store> l = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barLoading = findViewById(R.id.bar_loading);
        tvMessage = findViewById(R.id.tv_message);
        tvPrice = findViewById(R.id.tv_total_price);
        tvItemCount = findViewById(R.id.tv_total_items);
        recStores = findViewById(R.id.rec_stores);
        btDelete = findViewById(R.id.bt_delete);


        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        MainApplication application = (MainApplication) getApplication();

        viewModel.previousConnectionStatus = viewModel.getIsConnected().getValue();
        viewModel.setIsConnected(application.isConnected);

        viewModel.getIsConnected().observe(this, isConnected -> {
            if (isConnected){
                if (!viewModel.previousConnectionStatus) {
                    Toast.makeText(getBaseContext(), "Internet connected ", Toast.LENGTH_SHORT).show();
                    viewModel.previousConnectionStatus = true;
                    viewModel.getProducts();
                }
            } else {
                Toast.makeText(getBaseContext() , "Internet lost " , Toast.LENGTH_SHORT).show();
                viewModel.previousConnectionStatus = false;
            }
        });

        viewModel.viewstate.observe(this, viewState -> {
            Log.v("koko" , "view sate is "+viewState.getTotalPrice() + " , " + viewState.getTotalItemCount());
            if (viewState.getLoading()){
                // Show progress bar
                barLoading.setVisibility(View.VISIBLE);
            } else {
                barLoading.setVisibility(View.GONE);
            }

            if (!viewState.getError().isEmpty()){
                // Show error message
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(viewState.getError());
            } else{
                tvMessage.setVisibility(View.GONE);
            }

            if (viewState.getStores().isEmpty() && !viewState.getLoading() && viewState.getError().isEmpty()){
                // Show empty message
                recStores.setVisibility(View.GONE);
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText("No items in the cart");
            } else if (!viewState.getStores().isEmpty() && viewState.getStores() != l){
                // show recyclerview
                l = viewState.getStores();
                recStores.setVisibility(View.VISIBLE);
                if (tvMessage.getVisibility() == View.VISIBLE){
                    tvMessage.setVisibility(View.GONE);
                }
                StoresAdapter adapter = new StoresAdapter(viewState.getStores(), listener, viewModel.getIsConnected());
                recStores.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recStores.setAdapter(adapter);
            }
            tvPrice.setText(String.valueOf(viewState.getTotalPrice()));
            tvItemCount.setText(String.valueOf(viewState.getTotalItemCount()));
        });


        btDelete.setOnClickListener(view -> {
            showWantToRemoveDialog(view.getContext());
        });
    }

    @Override
    public void updateItemNum(Product product , List<Store> stores) {
        viewModel.calcTotalPriceCount(stores);
        viewModel.updateProduct(product);
    }

    @Override
    public void removeProduct(Product product , List<Store> stores) {
        viewModel.calcTotalPriceCount(stores);
        viewModel.removeProduct(product);
    }

    private void showWantToRemoveDialog(Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete the whole cart ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    viewModel.deleteCart();

                    Toast.makeText(getBaseContext(), "Deleted the cart successfully ", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }).setNegativeButton("No" , (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
        dialog.show();
    }

}