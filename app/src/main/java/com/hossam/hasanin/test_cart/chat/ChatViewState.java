package com.hossam.hasanin.test_cart.chat;


import java.util.List;

public class ChatViewState {
    private Boolean loading;
    private Boolean loadingMore;
    private Boolean noMore;
    private String error;
    private List<MessageWrapper> messages;

    public ChatViewState(Boolean loading, Boolean loadingMore, Boolean noMore, String error, List<MessageWrapper> messages) {
        this.loading = loading;
        this.loadingMore = loadingMore;
        this.noMore = noMore;
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

    public List<MessageWrapper> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageWrapper> messages) {
        this.messages = messages;
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

    ChatViewState copy(List<MessageWrapper> messages, Boolean loading , Boolean loadingMore , Boolean noMore , String error){
        List<MessageWrapper> _messages = this.messages;
        if (messages != null){
            _messages = messages;
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

        return new ChatViewState(_loading , _loadingMore, _noMore, _error , _messages);
    }

}
