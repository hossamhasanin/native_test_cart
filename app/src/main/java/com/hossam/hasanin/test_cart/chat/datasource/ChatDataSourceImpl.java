package com.hossam.hasanin.test_cart.chat.datasource;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
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
                .orderBy("createdAt" , Query.Direction.ASCENDING);

        return query;
    }

    public Task<DocumentReference> sendMessage(String message, Integer sendTo) {
        CollectionReference query = firestore.collection("chats");

        Log.v("koko" , message);
        Map<String , Object> map = new HashMap<String, Object>(){{
            put("senderId" , sellerId);
            put("users" , Arrays.asList(sellerId.toString() + " " + sendTo.toString(), sendTo.toString() + " " + sellerId.toString()));
            put("message" , message);
            put("createdAt" , FieldValue.serverTimestamp());
        }};
        return query.add(map);

    }
}
