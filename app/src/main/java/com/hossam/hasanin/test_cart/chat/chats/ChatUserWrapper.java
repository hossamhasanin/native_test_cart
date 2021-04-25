package com.hossam.hasanin.test_cart.chat.chats;

import com.hossam.hasanin.test_cart.models.UserChat;

import java.util.Objects;

public class ChatUserWrapper {

    private String id;
    private UserChat userChat;
    private int type;

    public ChatUserWrapper(String id, UserChat userChat, int type) {
        this.id = id;
        this.userChat = userChat;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserChat getUserChat() {
        return userChat;
    }

    public void setUserChat(UserChat userChat) {
        this.userChat = userChat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatUserWrapper)) return false;
        ChatUserWrapper that = (ChatUserWrapper) o;
        return id.equals(that.id) && type == that.type &&
                userChat.getId().equals(that.userChat.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userChat, type);
    }

    public static final int LOADING = 1;
    public static final int CHAT = 2;
}

