package com.hossam.hasanin.test_cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
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

        final ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        NetworkRequest networkRequest = builder.build();
        connectivityManager.registerNetworkCallback(networkRequest , new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                if (!viewModel.isConnected.getValue()) {
                    Toast.makeText(getBaseContext(), "Internet connected ", Toast.LENGTH_SHORT).show();
                    viewModel.isConnected.postValue(true);
                    if (viewModel.viewstate.getValue().getStores().isEmpty()){
                        viewModel.getProducts();
                    }
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                viewModel.isConnected.postValue(false);
                Toast.makeText(getBaseContext() , "Internet lost " , Toast.LENGTH_SHORT).show();
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
                StoresAdapter adapter = new StoresAdapter(viewState.getStores(), listener, viewModel.isConnected);
                recStores.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recStores.setAdapter(adapter);
            }
            tvPrice.setText(String.valueOf(viewState.getTotalPrice()));
            tvItemCount.setText(String.valueOf(viewState.getTotalItemCount()));
        });


        btDelete.setOnClickListener(view -> {
            showWantToRemoveDialog(getBaseContext());
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