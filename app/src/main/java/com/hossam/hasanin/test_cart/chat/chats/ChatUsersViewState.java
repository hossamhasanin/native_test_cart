package com.hossam.hasanin.test_cart.chat.chats;


import com.hossam.hasanin.test_cart.chat.messages.MessageWrapper;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.util.List;

public class ChatUsersViewState {
    private Boolean loading;
    private Boolean loadingMore;
    private Boolean noMore;
    private String error;
    private List<ChatUserWrapper> chats;

    public ChatUsersViewState(Boolean loading, Boolean loadingMore, Boolean noMore, String error, List<ChatUserWrapper> chats) {
        this.loading = loading;
        this.loadingMore = loadingMore;
        this.noMore = noMore;
        this.error = error;
        this.chats = chats;
    }

    public Boolean getLoading() {
        return loading;
    }

    public void setLoading(Boolean loading) {
        this.loading = loading;
    }

    public Boolean getLoadingMore() {
        return loadingMore;
    }

    public void setLoadingMore(Boolean loadingMore) {
        this.loadingMore = loadingMore;
    }

    public Boolean getNoMore() {
        return noMore;
    }

    public void setNoMore(Boolean noMore) {
        this.noMore = noMore;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<ChatUserWrapper> getChats() {
        return chats;
    }

    public void setChats(List<ChatUserWrapper> chats) {
        this.chats = chats;
    }

    ChatUsersViewState copy(List<ChatUserWrapper> chats, Boolean loading , Boolean loadingMore , Boolean noMore , String error){
        List<ChatUserWrapper> _chats = this.chats;
        if (chats != null){
            _chats = chats;
        }
        Boolean _loading = this.loading;
        if (loading != null){
            _loading = loading;
        }

        Boolean _loadingMore = this.loadingMore;
        if (loadingMore != null){
            _loadingMore = loadingMore;
        }

        Boolean _noMore = this.noMore;
        if (noMore != null){
            _noMore = noMore;
        }

        String _error = this.error;
        if (error != null){
            _error = error;
        }

        return new ChatUsersViewState(_loading , _loadingMore, _noMore, _error , _chats);
    }

}
