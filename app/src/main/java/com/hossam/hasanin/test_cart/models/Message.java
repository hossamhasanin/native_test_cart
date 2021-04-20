package com.hossam.hasanin.test_cart.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private Integer senderId;
    private String message;
    private Long createdAt;

    public Message(Integer senderId, String message, Long createdAt) {
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
    }


    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public static Message fromDocument(DocumentSnapshot snapshot){
        Map<String , Object> map = snapshot.getData();
        return new Message(snapshot.getLong("senderId").intValue(), (String) map.get("message") , map.get("createdAt") != null ? ((Timestamp) map.get("createdAt")).toDate().getTime(): 0);
    }

    Map<String , Object> toMap(){
        return new HashMap<String , Object>(){
            {
                put("senderId" , senderId);
                put("message" , message);
                put("createdAt" , FieldValue.serverTimestamp());
            }
        };
    }

}
