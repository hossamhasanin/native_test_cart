package com.hossam.hasanin.test_cart.deep_link_product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hossam.hasanin.test_cart.MainApplication;
import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.cart.CartViewModel;

public class DeeoLinkActivity extends AppCompatActivity {

    DeepLinkViewModel viewModel;
    TextView tvMess;
    TextView tvTitle;
    TextView tvImage;
    ProgressBar barLoading;
    String productQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deeo_link);

        barLoading = findViewById(R.id.bar_loading);
        tvMess = findViewById(R.id.tv_mess);
        tvTitle = findViewById(R.id.tv_title);
        tvImage = findViewById(R.id.tv_image);

        viewModel = new ViewModelProvider(this).get(DeepLinkViewModel.class);
        MainApplication application = (MainApplication) getApplication();
        viewModel.previousConnectionStatus = application.isConnected.getValue();


        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null){
            productQuery = appLinkData.getLastPathSegment();

            viewModel.product.observe(this , product -> {
                if (product != null){
                    if (product.getProductId() != null) {
                        barLoading.setVisibility(View.GONE);
                        tvMess.setVisibility(View.GONE);

                        // those fields are virtual
                        tvTitle.setVisibility(View.VISIBLE);
                        tvImage.setVisibility(View.VISIBLE);
                        tvTitle.setText(product.getProductName());
                        tvImage.setText(product.getProductImage());
                    }
                } else {
                    tvImage.setVisibility(View.GONE);
                    tvTitle.setVisibility(View.GONE);
                    barLoading.setVisibility(View.GONE);
                    tvMess.setVisibility(View.VISIBLE);

                    tvMess.setText("No product found");
                }
            });

            if (application.isConnected.getValue()){
                viewModel.getProduct(productQuery);
            } else {
                tvImage.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                barLoading.setVisibility(View.GONE);
                tvMess.setVisibility(View.VISIBLE);

                tvMess.setText("No internet connection");
            }
        }

        application.isConnected.observe(this, isConnected -> {
            if (isConnected){
                if (!viewModel.previousConnectionStatus) {
                    Toast.makeText(getBaseContext(), "Internet connected ", Toast.LENGTH_SHORT).show();
                    viewModel.previousConnectionStatus = true;
                    viewModel.getProduct(productQuery);
                }
            } else {
                Toast.makeText(getBaseContext() , "Internet lost " , Toast.LENGTH_SHORT).show();
                viewModel.previousConnectionStatus = false;
            }
        });

    }
}