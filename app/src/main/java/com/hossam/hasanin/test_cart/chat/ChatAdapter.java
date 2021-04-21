package com.hossam.hasanin.test_cart.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

    private Context mContext;
    private List<MessageWrapper> messages;
    private UserChat otherUser;

    public ChatAdapter(Context context, List<MessageWrapper> messageList, UserChat otherUser) {
        mContext = context;
        messages = messageList;
        this.otherUser = otherUser;
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

    static String getDate(Long timestamp){
        return new SimpleDateFormat("dd MMM").format(new Date(timestamp));
    }

    static String getTime(Long timestamp){
        return new SimpleDateFormat("hh:mm").format(new Date(timestamp));
    }

}