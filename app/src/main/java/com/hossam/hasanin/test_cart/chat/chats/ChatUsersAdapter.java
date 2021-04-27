package com.hossam.hasanin.test_cart.chat.chats;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.adapters.StorePriceChanged;
import com.hossam.hasanin.test_cart.chat.messages.ChatAdapter;
import com.hossam.hasanin.test_cart.models.Product;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatUserWrapper> users;


    public ChatUsersAdapter(List<ChatUserWrapper> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ChatUserWrapper.CHAT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_user_item, parent, false);
            return new ChatUsersAdapter.UserViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_item, parent, false);
            return new ChatUsersAdapter.LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
       if (holder.getItemViewType() == ChatUserWrapper.CHAT){
           ((UserViewHolder) holder).bind(users.get(position).getUserChat());
       }
    }

    @Override
    public int getItemViewType(int position) {
        ChatUserWrapper user = users.get(position);

        return user.getType();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<ChatUserWrapper> users) {
        this.users = users;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder{
        CircleImageView chatUserImage;
        TextView chatUserName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            chatUserImage = itemView.findViewById(R.id.im_chat_user_image);
            chatUserName = itemView.findViewById(R.id.tv_chat_user_name);

        }

        void bind(UserChat userChat){
            Glide.with(chatUserImage.getContext()).load(userChat.getUserImage()).into(chatUserImage);
            chatUserName.setText(userChat.getName());
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder{


        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }
}
