package com.hossam.hasanin.test_cart.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private String id;
    private Integer senderId;
    private String message;
    private int type;
    private Long createdAt;

    public Message(String id ,Integer senderId, String message, int type, Long createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.message = message;
        this.type = type;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static Message fromDocument(DocumentSnapshot snapshot){
        Map<String , Object> map = snapshot.getData();
        return new Message((String) snapshot.get("id") , snapshot.getLong("senderId").intValue(), (String) map.get("message") , snapshot.getLong("type").intValue(), map.get("createdAt") != null ? ((Timestamp) map.get("createdAt")).toDate().getTime(): 0);
    }

    public static final int TEXT_MESS = 0;
    public static final int VOICE_MESS = 1;
    public static final int PICTURE_MESS = 2;

}
