package com.hossam.hasanin.test_cart.images_rec;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hossam.hasanin.test_cart.R;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    private ArrayList<String> images;
    private ImageSliderClickListener listener;

    public ImagesAdapter(ArrayList<String> images, ImageSliderClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_holder, parent, false);

        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String image = images.get(position);

        if (image == ""){
            holder.image.setBackgroundColor(holder.image.getResources().getColor(R.color.colorPrimary));
            holder.image.setImageURI(null);
        } else {
            holder.image.setBackgroundColor(holder.image.getResources().getColor(android.R.color.transparent));
            holder.image.setImageURI(Uri.parse(image));
        }

        holder.image.setOnClickListener(view -> {
            listener.onImageClicked(position);
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView image;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imv_image_holder);
        }
    }
}
