package com.hossam.hasanin.test_cart.chat;


import com.hossam.hasanin.test_cart.cart.ViewState;
import com.hossam.hasanin.test_cart.models.Message;
import com.hossam.hasanin.test_cart.models.Store;

import java.util.List;

public class ChatViewState {
    private Boolean loading;
    private String error;
    private List<Message> messages;

    public ChatViewState(Boolean loading, String error, List<Message> messages) {
        this.loading = loading;
        this.error = error;
        this.messages = messages;
    }

    public Boolean getLoading() {
        return loading;
    }

    public void setLoading(Boolean loading) {
        this.loading = loading;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    ChatViewState copy(List<Message> messages, Boolean loading , String error){
        List<Message> _messages = this.messages;
        if (messages != null){
            _messages = messages;
        }
        Boolean _loading = this.loading;
        if (loading != null){
            _loading = loading;
        }

        String _error = this.error;
        if (error != null){
            _error = error;
        }

        return new ChatViewState(_loading , _error , _messages);
    }

}
