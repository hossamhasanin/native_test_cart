package com.hossam.hasanin.test_cart.chat.chats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.hossam.hasanin.test_cart.chat.datasource.ChatDataSource;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatUsersViewModel extends ViewModel {

    private ChatDataSource dataSource;
    private MutableLiveData<ChatUsersViewState> _viewstate = new MutableLiveData(new ChatUsersViewState(true , false, false, "" , new ArrayList<>()));
    LiveData<ChatUsersViewState> viewstate = _viewstate;
    MutableLiveData<Integer> savedScrollPos = new MutableLiveData<>(0);

    public void getChats(String lastId , UserChat user){
        if (!lastId.isEmpty() && _viewstate.getValue().getNoMore()) return;

        if (!_viewstate.getValue().getLoadingMore() && !lastId.isEmpty()){
            _viewstate.getValue().getChats().add(new ChatUserWrapper(null, null , ChatUserWrapper.LOADING));
            _viewstate.postValue(_viewstate.getValue().copy(_viewstate.getValue().getChats() , null , true , null , null));
        }

        dataSource.getChats(lastId).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                List<ChatUserWrapper> userChats = _viewstate.getValue().getChats();
                if (!lastId.isEmpty()) userChats.remove(userChats.size() - 1);

                if (!docs.isEmpty()){
                    Map<String , Object> fUserMap = (Map<String, Object>) docs.get(0).get(user.getId().toString());
                    String fId = (String) docs.get(0).get("id");
                    UserChat fUserChat = new UserChat(Long.valueOf((Long) fUserMap.get(UserChat.ID)).intValue() , (String) fUserMap.get(UserChat.NAME) , (String) fUserMap.get(UserChat.IMAGE));
                    ChatUserWrapper fUserWrapper = new ChatUserWrapper(fId, fUserChat , ChatUserWrapper.CHAT);

                    if (docs.size() == 1 && userChats.contains(fUserWrapper)) {
                        _viewstate.postValue(_viewstate.getValue().copy(userChats , false , false , true , null));
                    } else {

                        if (!lastId.isEmpty()) savedScrollPos.postValue(userChats.size());

                        for (DocumentSnapshot snapshot : docs) {
                            Map<String, Object> userMap = (Map<String, Object>) snapshot.get(user.getId().toString());
                            String id = (String) snapshot.get("id");
                            UserChat userChat = new UserChat(Long.valueOf((Long) userMap.get(UserChat.ID)).intValue(), (String) userMap.get(UserChat.NAME), (String) userMap.get(UserChat.IMAGE));
                            ChatUserWrapper wrapper = new ChatUserWrapper(id, userChat, ChatUserWrapper.CHAT);

                            userChats.add(wrapper);
                        }


                        _viewstate.postValue(_viewstate.getValue().copy(userChats, false, false, null, null));
                    }
                } else {
                    _viewstate.postValue(_viewstate.getValue().copy(userChats , false , false , lastId.isEmpty() ? null : true , lastId.isEmpty() ? "No chats" : null));
                }
            } else {
                _viewstate.postValue(_viewstate.getValue().copy(null , false , false , lastId.isEmpty() ? null : true , "Error !"));
            }
        });
    }

    public void setDataSource(ChatDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
