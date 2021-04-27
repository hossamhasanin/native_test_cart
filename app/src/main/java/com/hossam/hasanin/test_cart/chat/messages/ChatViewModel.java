package com.hossam.hasanin.test_cart.chat.messages;

import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hossam.hasanin.test_cart.chat.datasource.ChatDataSource;
import com.hossam.hasanin.test_cart.models.Message;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatViewModel extends ViewModel {

    private ChatDataSource dataSource;
    private MutableLiveData<ChatViewState> _viewstate = new MutableLiveData(new ChatViewState(true , false, false, "" , new ArrayList<>()));
    LiveData<ChatViewState> viewstate = _viewstate;
    MutableLiveData<Integer> savedScrollPos = new MutableLiveData<>(0);
    MutableLiveData<Integer> firstNewMessagePos = new MutableLiveData<>(0);
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private Map<String , Integer> uploadingImPos = new HashMap<>();
    MutableLiveData<String> chatId = new MutableLiveData<>(null);
    private ArrayList<String> deletedMess = new ArrayList<>();

    public void findChatId(UserChat sendTo){
        dataSource.findChatId(sendTo.getId()).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               if (!task.getResult().getDocuments().isEmpty()){
                   chatId.setValue(task.getResult().getDocuments().get(0).getId());
                   chatId.postValue(task.getResult().getDocuments().get(0).getId());
                    dataSource.updateUsers(sendTo , chatId.getValue());
               } else {
                   chatId.postValue("");
                   _viewstate.postValue(_viewstate.getValue().copy(new ArrayList<>(), false, null , null ,"No messages !"));
               }
           }else {
               chatId.postValue("");
               _viewstate.postValue(_viewstate.getValue().copy(new ArrayList<>(), false, null , null ,"Error !"));
           }
        });
    }

    public void chatListener(Integer otherSellerId){
        dataSource.listenToChat(otherSellerId , chatId.getValue()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            Message message = Message.fromDocument(snapshot);
                            if (message.getType() == Message.PICTURE_MESS){
                                messages.add(new MessageWrapper(message, MessageWrapper.IMAGE, message.getSenderId().equals(otherSellerId) ? null : UploadingImageState.UPLOAD_DONE));
                            } else if (message.getType() == Message.TEXT_MESS) {
                                messages.add(new MessageWrapper(message, MessageWrapper.MESSAGE, null));
                            }
                        }
                    } else {
                        messages = _viewstate.getValue().getMessages();

                        if (messages.size() > 5 &&
                                firstNewMessagePos.getValue() == 0 &&
                                Message.fromDocument(value.getDocumentChanges().get(0).getDocument()).getSenderId().equals(otherSellerId)) firstNewMessagePos.postValue(messages.size());

                        for (int j = value.getDocumentChanges().size() - 1; j >= 0; j--) {
                            DocumentChange change = value.getDocumentChanges().get(j);
                            Message gotMess = Message.fromDocument(change.getDocument());

                            if (!deletedMess.contains(gotMess.getId())) {

                                deletedMess.clear();

                                if (gotMess.getType() == Message.PICTURE_MESS) {
                                    MessageWrapper showedMess;
                                    if (!messages.contains(new MessageWrapper(gotMess, MessageWrapper.IMAGE, null))) {
                                        if (gotMess.getSenderId().equals(otherSellerId)) {
                                            showedMess = new MessageWrapper(gotMess, MessageWrapper.IMAGE, null);
                                            messages.add(showedMess);
                                        } else {
                                            if (uploadingImPos.containsKey(gotMess.getMessage())) {
                                                Integer messPos = uploadingImPos.get(gotMess.getMessage());
                                                showedMess = messages.get(messPos);
                                                showedMess.setUploadingImageState(UploadingImageState.UPLOAD_DONE);
                                                showedMess.setMessage(gotMess);
                                                messages.set(messPos, showedMess);
                                                uploadingImPos.remove(messPos);
                                            }
                                        }
                                    }
//                                _viewstate.postValue(_viewstate.getValue().copy(_viewstate.getValue().getMessages(), false, null , null ,""));
                                } else {
                                    // check if the created time is different then modify it
                                    if (messages.get(messages.size() - 1).getMessage().getMessage()
                                            .equals(gotMess.getMessage())
                                            && !messages.get(messages.size() - 1).getMessage().getCreatedAt()
                                            .equals(gotMess.getCreatedAt())) {

                                        if (messages.contains(new MessageWrapper(gotMess, MessageWrapper.MESSAGE, null))) {
                                            Log.v("koko", "edit time");
                                            messages.get(messages.size() - 1).getMessage().setCreatedAt(Message.fromDocument(change.getDocument()).getCreatedAt());
                                        }
                                    } else {
                                        if (!messages.contains(new MessageWrapper(gotMess, MessageWrapper.MESSAGE, null))) {
                                            Log.v("koko", "add to list");
                                            messages.add(new MessageWrapper(gotMess, MessageWrapper.MESSAGE, null));

                                        }
                                    }
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

    public void send(Message message , UserChat sendTo){
        if (chatId.getValue() == null) return;

        dataSource.sendMessage(message , sendTo, chatId).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.v("koko" , "done send");

            } else {
                Log.e("koko" , task.getException().getMessage());
            }
        });
    }

    public void loadMore(){
        if (_viewstate.getValue().getNoMore() || chatId.getValue() == null) return;

        List<MessageWrapper> loadingMoreL = _viewstate.getValue().getMessages();
        loadingMoreL.add(0 , new MessageWrapper(null , MessageWrapper.LOADING, null));
        _viewstate.postValue(_viewstate.getValue().copy(loadingMoreL , null , true , null , null));

        dataSource.getMoreMessages(_viewstate.getValue().getMessages().get(1).getMessage() , chatId.getValue()).addOnCompleteListener(task -> {
            List<MessageWrapper> messageWrappers = _viewstate.getValue().getMessages();
            messageWrappers.remove(0);
            if (task.isSuccessful()){
               List<DocumentSnapshot> docs = task.getResult().getDocuments();

               if (!docs.isEmpty()){
                   if (docs.size() == 1 && messageWrappers.contains(new MessageWrapper(Message.fromDocument(docs.get(0)) , MessageWrapper.MESSAGE, null)))
                   {
                       Log.e("koko" , "no more data");
                       _viewstate.postValue(_viewstate.getValue().copy(_viewstate.getValue().getMessages(), false, false , true ,""));
                   } else {
                       for (DocumentSnapshot snapshot : docs) {
                           Message messageL = Message.fromDocument(snapshot);
                           if (!messageWrappers.contains(new MessageWrapper(Message.fromDocument(snapshot), MessageWrapper.MESSAGE, null)))
                               messageWrappers.add(0, new MessageWrapper(messageL, MessageWrapper.MESSAGE, null));
                       }
                       savedScrollPos.postValue(docs.size() - 1);
                       Log.e("koko", "got data " + docs.size());
                       if (firstNewMessagePos.getValue() != 0) firstNewMessagePos.postValue(firstNewMessagePos.getValue() + docs.size());
                       _viewstate.postValue(_viewstate.getValue().copy(messageWrappers, false, false, null, ""));
                   }
               } else {
                   Log.e("koko" , "no more data");
                   _viewstate.postValue(_viewstate.getValue().copy(messageWrappers, false, false , true ,""));
               }
           } else {
               Log.e("koko" , "no more data error");
               task.getException().printStackTrace();
               _viewstate.postValue(_viewstate.getValue().copy(new ArrayList<>(), false, false , true ,"Error !"));
           }
        });
    }

    public void uploadImage(byte[] bitmapData , Uri uri , UserChat sendTo , UserChat sendFrom){
        if (chatId.getValue() == null) return;

        final Integer pos = _viewstate.getValue().getMessages().size();

        _viewstate.getValue().getMessages().add(new MessageWrapper(new Message("" , sendFrom.getId() , uri.toString() , Message.PICTURE_MESS , new Date().getTime()) , MessageWrapper.IMAGE , UploadingImageState.UPLOADING));

        savedScrollPos.postValue(pos);
        _viewstate.postValue(_viewstate.getValue().copy(_viewstate.getValue().getMessages(), false, null , null ,""));

        String randomName = UUID.randomUUID().toString();
        StorageReference imagePath = storageReference.child("images").child(randomName + ".jpg");
        UploadTask uploadTask = imagePath.putBytes(bitmapData);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imagePath.getDownloadUrl().addOnSuccessListener(imageUri -> {
                    String url = imageUri.toString();

                    Message message = new Message("" ,null , url , Message.PICTURE_MESS , null);

                    uploadingImPos.put(url , pos);

                    dataSource.sendMessage(message, sendTo , chatId);
                });
            } else {
                MessageWrapper showedMess =  _viewstate.getValue().getMessages().get(pos);
                showedMess.setUploadingImageState(UploadingImageState.CANCELLED);
                _viewstate.getValue().getMessages().set(pos , showedMess);
                _viewstate.postValue(_viewstate.getValue().copy(_viewstate.getValue().getMessages(), false, null , null ,""));
            }
        });
    }

    public void deleteMessage(String id , int pos){
        Log.v("koko" , "delete chat id "+ chatId.getValue());
        _viewstate.getValue().getMessages().remove(pos);
        _viewstate.postValue(_viewstate.getValue().copy(_viewstate.getValue().getMessages(), false, null , null ,""));
        deletedMess.add(id);
        dataSource.deleteMessage(id , chatId.getValue());
    }

    public void setDataSource(ChatDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
