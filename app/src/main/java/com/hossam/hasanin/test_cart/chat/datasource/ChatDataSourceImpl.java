package com.hossam.hasanin.test_cart.chat.datasource;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hossam.hasanin.test_cart.models.Message;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatDataSourceImpl implements ChatDataSource {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Integer sellerId;

    public ChatDataSourceImpl(Integer sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public Query listenToChat(Integer otherSellerId) {
        Query query = firestore.collection("chats")
                .whereArrayContains("users" , sellerId.toString() + " "+ otherSellerId.toString())
                .orderBy("createdAt" , Query.Direction.DESCENDING)
                .limit(11);

        return query;
    }

    public Task<DocumentReference> sendMessage(Message message, Integer sendTo) {
        CollectionReference query = firestore.collection("chats");

        Log.v("koko" , message.getMessage());
        Map<String , Object> map = new HashMap<String, Object>(){{
            put("id" , "");
            put("senderId" , sellerId);
            put("users" , Arrays.asList(sellerId.toString() + " " + sendTo.toString(), sendTo.toString() + " " + sellerId.toString()));
            put("message" , message.getMessage());
            put("type" , message.getType());
            put("createdAt" , FieldValue.serverTimestamp());
        }};
        return query.add(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                query.document(task.getResult().getId()).update("id" , task.getResult().getId());
            }
        });

    }

    @Override
    public Task<QuerySnapshot> getMoreMessages(Message message, Integer otherSellerId) {
        Query query = firestore.collection("chats")
                .whereArrayContains("users" , sellerId.toString() + " "+ otherSellerId.toString())
                .orderBy("createdAt" , Query.Direction.DESCENDING)
                .orderBy("id")
                .startAfter(new Timestamp(new Date(message.getCreatedAt())), message.getId())
                .limit(11);

        return query.get();
    }
}
