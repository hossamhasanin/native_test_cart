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
    private MutableLiveData<ChatViewState> _viewstate = new MutableLiveData(new ChatViewState(true , false, false, "" , new ArrayList<>()));
    LiveData<ChatViewState> viewstate = _viewstate;
    MutableLiveData<Integer> savedScrollPos = new MutableLiveData<>(0);

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
                    List<MessageWrapper> messages = new ArrayList<>();
                    if (_viewstate.getValue().getMessages().isEmpty()) {
                        for (int j = value.getDocuments().size() - 1; j >= 0; j--) {
                            DocumentSnapshot snapshot = value.getDocuments().get(j);
                            messages.add(new MessageWrapper(Message.fromDocument(snapshot) , MessageWrapper.MESSAGE));
                        }
                    } else {
                        messages = _viewstate.getValue().getMessages();
                        for (int j = value.getDocumentChanges().size() - 1; j >= 0; j--) {
                            DocumentChange change = value.getDocumentChanges().get(j);
                            // check if the created time is different then modify it
                            if (messages.get(messages.size() - 1).getMessage().getMessage()
                                    .equals(Message.fromDocument(change.getDocument()).getMessage())
                                    && !messages.get(messages.size() - 1).getMessage().getCreatedAt()
                                    .equals(Message.fromDocument(change.getDocument()).getCreatedAt())){

                                if (messages.contains(new MessageWrapper(Message.fromDocument(change.getDocument()) , MessageWrapper.MESSAGE))) {
                                    Log.v("koko" , "edit time");
                                    messages.get(messages.size() - 1).getMessage().setCreatedAt(Message.fromDocument(change.getDocument()).getCreatedAt());
                                }
                            } else {
                                if (!messages.contains(new MessageWrapper(Message.fromDocument(change.getDocument()) , MessageWrapper.MESSAGE))) {
                                    Log.v("koko" , "add to list");
                                    messages.add(new MessageWrapper(Message.fromDocument(change.getDocument()), MessageWrapper.MESSAGE));

                                }
                            }
                        }
                    }
                    _viewstate.postValue(_viewstate.getValue().copy(messages, false, null , null ,""));
                } else {
                    _viewstate.postValue(_viewstate.getValue().copy(new ArrayList<>(), false, null , null ,"No messages !"));
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

    public void loadMore(Integer otherSeller){
        if (_viewstate.getValue().getNoMore()) return;

        List<MessageWrapper> loadingMoreL = _viewstate.getValue().getMessages();
        loadingMoreL.add(0 , new MessageWrapper(null , MessageWrapper.LOADING));
        _viewstate.postValue(_viewstate.getValue().copy(loadingMoreL , null , true , null , null));

        dataSource.getMoreMessages(_viewstate.getValue().getMessages().get(1).getMessage(), otherSeller).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               List<DocumentSnapshot> docs = task.getResult().getDocuments();
               List<MessageWrapper> messageWrappers = _viewstate.getValue().getMessages();
               messageWrappers.remove(0);
               if (!docs.isEmpty()){
                   if (docs.size() == 1 && messageWrappers.contains(new MessageWrapper(Message.fromDocument(docs.get(0)) , MessageWrapper.MESSAGE)))
                   {
                       Log.e("koko" , "no more data");
                       _viewstate.postValue(_viewstate.getValue().copy(_viewstate.getValue().getMessages(), false, false , true ,""));
                   } else {
                       for (DocumentSnapshot snapshot : docs) {
                           Message messageL = Message.fromDocument(snapshot);
                           if (!messageWrappers.contains(new MessageWrapper(Message.fromDocument(snapshot), MessageWrapper.MESSAGE)))
                               messageWrappers.add(0, new MessageWrapper(messageL, MessageWrapper.MESSAGE));
                       }
                       savedScrollPos.postValue(docs.size() - 1);
                       Log.e("koko", "got data " + docs.size());
                       _viewstate.postValue(_viewstate.getValue().copy(messageWrappers, false, false, null, ""));
                   }
               } else {
                   Log.e("koko" , "no more data");
                   _viewstate.postValue(_viewstate.getValue().copy(_viewstate.getValue().getMessages(), false, false , true ,""));
               }
           } else {
               Log.e("koko" , "no more data");
               _viewstate.postValue(_viewstate.getValue().copy(_viewstate.getValue().getMessages(), false, false , true ,""));
           }
        });
    }

    public void setDataSource(ChatDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
