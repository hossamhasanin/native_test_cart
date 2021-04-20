package com.hossam.hasanin.test_cart.deep_link_product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.hossam.hasanin.test_cart.MainApplication;
import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.models.Product;

import java.util.concurrent.Executor;

public class DeeoLinkActivity extends AppCompatActivity {

    DeepLinkViewModel viewModel;
    TextView tvMess;
    TextView tvTitle;
    TextView tvImage;
    ProgressBar barLoading;
    String productQuery;
    MainApplication application;

    private static final String TAG = "DeeoLinkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deeo_link);

        barLoading = findViewById(R.id.bar_loading);
        tvMess = findViewById(R.id.tv_mess);
        tvTitle = findViewById(R.id.tv_title);
        tvImage = findViewById(R.id.tv_image);

        viewModel = new ViewModelProvider(this).get(DeepLinkViewModel.class);
        application = (MainApplication) getApplication();
        viewModel.previousConnectionStatus = application.isConnected.getValue();


        viewModel.product.observe(this , product -> {
            if (product != null){
                if (product.getProductId() != null) {
                    showProductData(product);
                }
            } else {
                showErrMessage("No product found");
            }
        });

        getProduct();

        application.isConnected.observe(this, isConnected -> {
            if (isConnected){
                if (!viewModel.previousConnectionStatus) {
                    Toast.makeText(getBaseContext(), "Internet connected ", Toast.LENGTH_SHORT).show();
                    viewModel.previousConnectionStatus = true;
                    getProduct();
                }
            } else {
                Toast.makeText(getBaseContext() , "Internet lost " , Toast.LENGTH_SHORT).show();
                viewModel.previousConnectionStatus = false;
            }
        });

        findViewById(R.id.button).setOnClickListener(view -> {
            Uri u = viewModel.getLongSharedLink();

            Log.v(TAG , u.toString());

            viewModel.getShortLink().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Short link created
                    Uri shortLink = task.getResult().getShortLink();
                    Uri flowchartLink = task.getResult().getPreviewLink();

                    Log.v(TAG , shortLink.toString());
                    Log.v(TAG , flowchartLink.toString());
                } else {
                    task.getException().printStackTrace();
                }
            });;
        });

    }

    private void showProductData(Product product){
        barLoading.setVisibility(View.GONE);
        tvMess.setVisibility(View.GONE);

        // those fields are virtual
        tvTitle.setVisibility(View.VISIBLE);
        tvImage.setVisibility(View.VISIBLE);
        tvTitle.setText(product.getProductName());
        tvImage.setText(product.getProductImage());
    }

    private void showErrMessage(String message){
        tvImage.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        barLoading.setVisibility(View.GONE);
        tvMess.setVisibility(View.VISIBLE);

        tvMess.setText(message);
    }

    private void getProduct(){
        Log.v(TAG , getIntent().getData().toString());

        if (application.isConnected.getValue()){
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent().getData())
                    .addOnSuccessListener(this, pendingDynamicLinkData -> {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            productQuery = deepLink.getLastPathSegment();

                            viewModel.getProduct(productQuery);


                        } else {
                            // when no link found

                            showErrMessage("Not valid link");
                        }

                    });
        } else {
            showErrMessage("No internet connection");
        }


    }
}