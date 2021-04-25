package com.hossam.hasanin.test_cart.chat.datasource;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hossam.hasanin.test_cart.models.Message;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ChatDataSourceImpl implements ChatDataSource {
    public static final int DATA_LIMIT = 11;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private UserChat sendFrom;

    public ChatDataSourceImpl(UserChat sendFrom) {
        this.sendFrom = sendFrom;
    }

    @Override
    public Query listenToChat(Integer otherSellerId , String chatId) {
        Query query = firestore.collection("chats")
                .document(chatId).collection("messages")
                .orderBy("createdAt" , Query.Direction.DESCENDING)
                .limit(ChatDataSourceImpl.DATA_LIMIT);

        return query;
    }

    @Override
    public Task<QuerySnapshot> getChats(String lastId) {
        CollectionReference query = firestore.collection("chats");

        if (lastId.isEmpty()){
            return query.whereArrayContains("users" , sendFrom.getId().toString()).limit(ChatDataSourceImpl.DATA_LIMIT).get();
        } else {
            return query.whereArrayContains("users" , sendFrom.getId().toString()).orderBy("id").startAfter(lastId).limit(ChatDataSourceImpl.DATA_LIMIT).get();
        }

    }

    @Override
    public Task<QuerySnapshot> findChatId(Integer sendToId) {
        Query query = firestore.collection("chats")
                .whereArrayContains("users" , sendFrom.getId().toString() + " "+ sendToId.toString());


        return query.get();
    }

    public Task<Void> sendMessage(Message message , UserChat sendTo , MutableLiveData<String> chatId) {
        CollectionReference query = firestore.collection("chats");

        Map<String , Object> messageMap = new HashMap<String, Object>(){{
            put("id" , String.valueOf(new Date().getTime()));
            put("senderId" , sendFrom.getId());
            put("message" , message.getMessage());
            put("type" , message.getType());
            put("createdAt" , FieldValue.serverTimestamp());
        }};
        if (chatId.getValue().isEmpty()){
            String calcChatId = String.valueOf(new Date().getTime());
            Map<String , Object> map = new HashMap<String, Object>(){{
                put("users" , Arrays.asList(sendFrom.getId().toString() + " " + sendTo.getId().toString(),
                        sendTo.getId().toString() + " " + sendFrom.getId().toString() , sendTo.getId().toString() , sendFrom.getId().toString()));
                put("createdAt" , FieldValue.serverTimestamp());
                put("id" , calcChatId);
                put(sendFrom.getId().toString() , new HashMap<String , Object>(){{
                    put(UserChat.ID , sendTo.getId());
                    put(UserChat.NAME , sendTo.getName());
                }});
                put(sendTo.getId().toString() , new HashMap<String , Object>(){{
                    put(UserChat.ID , sendFrom.getId());
                    put(UserChat.NAME , sendFrom.getName());
                }});
            }};
            return query.document(calcChatId).set(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    chatId.postValue(calcChatId);

                    query.document(chatId.getValue()).collection("messages").document((String) messageMap.get("id")).set(messageMap);
                }
            });
        } else {
            Log.v("koko" , message.getMessage());

            return query.document(chatId.getValue()).collection("messages").document((String) messageMap.get("id")).set(messageMap);
        }

    }

    @Override
    public Task<QuerySnapshot> getMoreMessages(Message message, String chatId) {
        Query query = firestore.collection("chats").document(chatId).collection("messages")
                .orderBy("createdAt" , Query.Direction.DESCENDING)
                .orderBy("id")
                .startAfter(new Timestamp(new Date(message.getCreatedAt())), message.getId())
                .limit(11);

        return query.get();
    }

    @Override
    public Task<Void> updateUsers(UserChat sendTo, String chatId) {
        CollectionReference query = firestore.collection("chats");

        Map<String , Object> map = new HashMap<String, Object>(){{
            put(sendTo.getId().toString() , new HashMap<String , Object>(){{
                put(UserChat.ID , sendTo.getId());
                put(UserChat.NAME , sendTo.getName());
            }});
            put(sendFrom.getId().toString() , new HashMap<String , Object>(){{
                put(UserChat.ID , sendFrom.getId());
                put(UserChat.NAME , sendFrom.getName());
            }});
        }};

        return query.document(chatId).update(map);
    }

    @Override
    public Task<Void> deleteChat(String chatId) {
        CollectionReference query = firestore.collection("chats");

        return query.document(chatId).delete();
    }

    @Override
    public Task<Void> deleteMessage(String messageId, String chatId) {
        CollectionReference query = firestore.collection("chats");

        return query.document(chatId).collection("messages").document(messageId).delete();
    }
}
