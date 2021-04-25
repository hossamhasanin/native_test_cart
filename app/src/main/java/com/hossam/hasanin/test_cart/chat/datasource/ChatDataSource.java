package com.hossam.hasanin.test_cart.chat.datasource;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hossam.hasanin.test_cart.models.Message;
import com.hossam.hasanin.test_cart.models.UserChat;

public interface ChatDataSource {
    Query listenToChat(Integer sendToId , String chatId);

    Task<QuerySnapshot> getChats(String lastId);

    Task<QuerySnapshot> findChatId(Integer sendToId);

    Task<Void> sendMessage(Message message , UserChat sendTo , MutableLiveData<String> chatId);

    Task<QuerySnapshot> getMoreMessages(Message message , String chatId);

    Task<Void> updateUsers(UserChat sendTo  , String chatId);

    Task<Void> deleteChat(String chatId);
    Task<Void> deleteMessage(String messageId , String chatId);

}
