package com.hossam.hasanin.test_cart.images_rec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hossam.hasanin.test_cart.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

public class RecTestActivity extends AppCompatActivity implements ImageSliderClickListener {

    private int currentImageHolder = -1;
    private ArrayList<String> productImages;
    private ImagesAdapter imagesAdapter;
    private RecyclerView slider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_test);

        productImages = new ArrayList<>();
        productImages.add("");
        productImages.add("");
        productImages.add("");

        imagesAdapter = new ImagesAdapter(productImages , this);

        slider = findViewById(R.id.rec_slider);

        slider.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));

        slider.setAdapter(imagesAdapter);

    }



    @Override
    public void onImageClicked(Integer position) {
        currentImageHolder = position;
        crop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of CropImageActivity
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                productImages.set(currentImageHolder , result.getUri().toString());

//                if (currentImageHolder == productImages.size()-1){
//                    productImages.add("");
//                }

                imagesAdapter.notifyDataSetChanged();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }


    // this function to filter the list from the empty images before adding it to the database
    private ArrayList<String> filterTheProductImages(ArrayList<String> productImages){
        ArrayList<String> filtered = new ArrayList<>();
        for (String im : productImages){
            if (!im.equals("")){
                filtered.add(im);
            }
        }
        return filtered;
    }

    private void crop() {
        CropImage
                .activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Crop Image")
                .setFlipHorizontally(false)
                .setFlipVertically(false)
                .setAllowFlipping(false)
                .setAllowRotation(false)
                .start(this);

//        mProgressBar.setVisibility(View.GONE);
//        toggleLoginButton(true);
    }


}