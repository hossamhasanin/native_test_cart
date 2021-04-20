package com.hossam.hasanin.test_cart.chat.datasource;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.hossam.hasanin.test_cart.models.Message;

public interface ChatDataSource {
    Query listenToChat(Integer otherSellerId);

    Task<DocumentReference> sendMessage(String message , Integer sendTo);

}
