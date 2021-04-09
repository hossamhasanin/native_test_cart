package com.hossam.hasanin.test_cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hossam.hasanin.test_cart.adapters.StoresAdapter;
import com.hossam.hasanin.test_cart.adapters.UpdateItemNumListener;
import com.hossam.hasanin.test_cart.models.Product;

public class MainActivity extends AppCompatActivity implements UpdateItemNumListener {

    ProgressBar barLoading;
    TextView tvMessage;
    RecyclerView recStores;
    private CartViewModel viewModel;
    private UpdateItemNumListener listener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barLoading = findViewById(R.id.bar_loading);
        tvMessage = findViewById(R.id.tv_message);
        recStores = findViewById(R.id.rec_stores);


        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        viewModel.viewstate.observe(this, new Observer<ViewState>() {
            @Override
            public void onChanged(ViewState viewState) {
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

                if (viewState.getStores().isEmpty() && !viewState.getLoading()){
                    // Show empty message
                    recStores.setVisibility(View.GONE);
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText("No items in the cart");
                } else {
                    // show recyclerview
                    recStores.setVisibility(View.VISIBLE);
                    if (tvMessage.getVisibility() == View.VISIBLE){
                        tvMessage.setVisibility(View.GONE);
                    }
                    StoresAdapter adapter = new StoresAdapter(viewState.getStores(), listener);
                    recStores.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    recStores.setAdapter(adapter);
                }
            }
        });

    }

    @Override
    public void updateItemNum(Product product) {
        viewModel.updateProduct(product);
    }
}