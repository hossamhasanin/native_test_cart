package com.hossam.hasanin.test_cart.chat.messages;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.models.Message;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_LOADING = 3;

    private static final int VIEW_TYPE_UPLOADING_IMAGE = 4;
    private static final int VIEW_TYPE_DONE_UPLOAD_IMAGE = 5;
    private static final int VIEW_TYPE_CANCELED_UPLOAD_IMAGE = 6;
    private static final int VIEW_TYPE_RECEIVED_IMAGE = 7;

    private Context mContext;
    private List<MessageWrapper> messages;
    private UserChat otherUser;
    private DeleteMessageListener listener;

    public ChatAdapter(Context context, List<MessageWrapper> messageList, UserChat otherUser, DeleteMessageListener listener) {
        mContext = context;
        messages = messageList;
        this.otherUser = otherUser;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) messages.get(position).getMessage();
        int type = messages.get(position).getType();
        if (type == MessageWrapper.MESSAGE) {
            if (message.getType() == Message.TEXT_MESS) {
                if (!message.getSenderId().equals(otherUser.getId())) {
                    // If the current user is the sender of the message
                    return VIEW_TYPE_MESSAGE_SENT;
                } else {
                    // If some other user sent the message
                    return VIEW_TYPE_MESSAGE_RECEIVED;
                }
            } else {
                // Delete it when you build the rest of view types
                return VIEW_TYPE_MESSAGE_SENT;
            }
        } else if (type == MessageWrapper.IMAGE){
            UploadingImageState uploadingImageState = messages.get(position).getUploadingImageState();

            if (uploadingImageState != null) {
                if (uploadingImageState == UploadingImageState.UPLOADING){
                    return VIEW_TYPE_UPLOADING_IMAGE;
                } else if (uploadingImageState == UploadingImageState.UPLOAD_DONE){
                    return VIEW_TYPE_DONE_UPLOAD_IMAGE;
                } else if (uploadingImageState == UploadingImageState.CANCELLED){
                    return VIEW_TYPE_CANCELED_UPLOAD_IMAGE;
                }
            }


            return VIEW_TYPE_RECEIVED_IMAGE;

        } else {
            return VIEW_TYPE_LOADING;
        }

    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_item, parent, false);
            return new LoadMoreHolder(view);
        } else if (viewType == VIEW_TYPE_UPLOADING_IMAGE || viewType == VIEW_TYPE_DONE_UPLOAD_IMAGE || viewType == VIEW_TYPE_CANCELED_UPLOAD_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_sent_item, parent, false);
            return new SentImageHolder(view);
        } else if (viewType == VIEW_TYPE_RECEIVED_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_received_item, parent, false);
            return new ReceivedImageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) messages.get(position).getMessage();

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message , otherUser);
                break;
            case VIEW_TYPE_LOADING:
                break;
            case VIEW_TYPE_UPLOADING_IMAGE:
                ((SentImageHolder) holder).bind(message , VIEW_TYPE_UPLOADING_IMAGE);
                break;
            case VIEW_TYPE_CANCELED_UPLOAD_IMAGE:
                ((SentImageHolder) holder).bind(message , VIEW_TYPE_CANCELED_UPLOAD_IMAGE);
                break;
            case VIEW_TYPE_DONE_UPLOAD_IMAGE:
                ((SentImageHolder) holder).bind(message , VIEW_TYPE_DONE_UPLOAD_IMAGE);
                break;
            case VIEW_TYPE_RECEIVED_IMAGE:
                ((ReceivedImageHolder) holder).bind(message);
                break;
        }
    }

    public void setMessages(List<MessageWrapper> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText , dateText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.tv_sent_message);
            timeText = itemView.findViewById(R.id.tv_time);
            dateText = itemView.findViewById(R.id.tv_date);


        }

        void bind(Message message) {
            messageText.setText(message.getMessage());

            timeText.setText(ChatAdapter.getTime(message.getCreatedAt()));
            dateText.setText(ChatAdapter.getDate(message.getCreatedAt()));

            messageText.setOnLongClickListener(view -> {
                listener.deleteMessage(message.getId());

                return true;
            });

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText , dateText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.tv_revieved_message);
            timeText = itemView.findViewById(R.id.tv_recieved_time);
            dateText = itemView.findViewById(R.id.tv_recieved_date);
            nameText = itemView.findViewById(R.id.tv_seller_name);


        }

        void bind(Message message , UserChat user) {
            messageText.setText(message.getMessage());
            nameText.setText(user.getName());

            timeText.setText(ChatAdapter.getTime(message.getCreatedAt()));
            dateText.setText(ChatAdapter.getDate(message.getCreatedAt()));
        }
    }

    private class LoadMoreHolder extends RecyclerView.ViewHolder {

        LoadMoreHolder(View itemView) {
            super(itemView);

        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {
        TextView tvUploadState ,timeText , dateText;
        ProgressBar uploading;
        ImageView sentImage;

        SentImageHolder(View itemView) {
            super(itemView);

            timeText = itemView.findViewById(R.id.tv_sent_im_time);
            dateText = itemView.findViewById(R.id.tv_sent_im_date);
            tvUploadState = itemView.findViewById(R.id.tv_upload_state);
            uploading = itemView.findViewById(R.id.bar_uploading);
            sentImage = itemView.findViewById(R.id.im_sent_image);


        }

        void bind(Message message , Integer uploadState) {

            timeText.setText(ChatAdapter.getTime(message.getCreatedAt()));
            dateText.setText(ChatAdapter.getDate(message.getCreatedAt()));

            if (uploadState == VIEW_TYPE_UPLOADING_IMAGE){
                uploading.setVisibility(View.VISIBLE);
                sentImage.setImageURI(Uri.parse(message.getMessage()));
            } else {
                uploading.setVisibility(View.GONE);
            }

            if (uploadState == VIEW_TYPE_DONE_UPLOAD_IMAGE || uploadState == VIEW_TYPE_CANCELED_UPLOAD_IMAGE){
                tvUploadState.setVisibility(View.VISIBLE);
                if (uploadState == VIEW_TYPE_DONE_UPLOAD_IMAGE) {
                    tvUploadState.setText("Done uploading");
                    Glide.with(sentImage.getContext()).load(message.getMessage()).into(sentImage);
                } else {
                    sentImage.setImageURI(Uri.parse(message.getMessage()));
                    tvUploadState.setText("Upload cancelled");
                }
            } else {
                tvUploadState.setVisibility(View.GONE);
            }

            sentImage.setOnLongClickListener(view -> {
                listener.deleteMessage(message.getId());
                return true;
            });
        }
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder {
        TextView timeText , dateText;
        ImageView receivedImage;

        ReceivedImageHolder(View itemView) {
            super(itemView);

            timeText = itemView.findViewById(R.id.tv_received_im_time);
            dateText = itemView.findViewById(R.id.tv_received_im_date);
            receivedImage = itemView.findViewById(R.id.im_received_image);


        }

        void bind(Message message) {

            timeText.setText(ChatAdapter.getTime(message.getCreatedAt()));
            dateText.setText(ChatAdapter.getDate(message.getCreatedAt()));
            Glide.with(receivedImage.getContext()).load(message.getMessage()).into(receivedImage);

        }
    }

    static String getDate(Long timestamp){
        return new SimpleDateFormat("dd MMM").format(new Date(timestamp));
    }

    static String getTime(Long timestamp){
        return new SimpleDateFormat("hh:mm").format(new Date(timestamp));
    }

}