package com.hossam.hasanin.test_cart.chat;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hossam.hasanin.test_cart.chat.datasource.ChatDataSource;
import com.hossam.hasanin.test_cart.models.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private ChatDataSource dataSource;
    private MutableLiveData<ChatViewState> _viewstate = new MutableLiveData(new ChatViewState(true , "" , new ArrayList<>()));
    LiveData<ChatViewState> viewstate = _viewstate;

    public void chatListener(Integer otherSellerId){
        dataSource.listenToChat(otherSellerId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (value != null) {
//
//                } else {
//                    _viewstate.postValue(_viewstate.getValue().copy(null, false, "No messages !"));
//                }
                if (!value.isEmpty() && !value.getDocuments().isEmpty()) {
                    List<Message> messages = new ArrayList<>();
                    if (_viewstate.getValue().getMessages().isEmpty()) {
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            messages.add(Message.fromDocument(snapshot));
                        }
                    } else {
                        messages = _viewstate.getValue().getMessages();
                        for (DocumentChange change : value.getDocumentChanges()) {
                            if (messages.get(messages.size() - 1).getMessage()
                                    .equals(Message.fromDocument(change.getDocument()).getMessage())
                                    && !messages.get(messages.size() - 1).getCreatedAt()
                                    .equals(Message.fromDocument(change.getDocument()).getCreatedAt())){
                                if (!messages.contains(Message.fromDocument(change.getDocument())))

                                    messages.get(messages.size() - 1).setCreatedAt(Message.fromDocument(change.getDocument()).getCreatedAt());
                            } else {
                                if (!messages.contains(Message.fromDocument(change.getDocument())))
                                    messages.add(Message.fromDocument(change.getDocument()));
                            }
                        }
                    }
                    _viewstate.postValue(_viewstate.getValue().copy(messages, false, ""));
                } else {
                    _viewstate.postValue(_viewstate.getValue().copy(new ArrayList<>(), false, "No messages !"));
                }
            }
        });
    }

    public void send(String message , Integer sendTo){
        dataSource.sendMessage(message , sendTo).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.v("koko" , "done send");

            } else {
                Log.e("koko" , task.getException().getMessage());
            }
        });
    }

    public void setDataSource(ChatDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
