package com.hossam.hasanin.test_cart.chat;

import com.hossam.hasanin.test_cart.models.Message;

import java.util.Objects;

public class MessageWrapper {

    private Message message;
    private int type;
    private UploadingImageState uploadingImageState;


    public MessageWrapper(Message message, int type, UploadingImageState uploadingImageState) {
        this.message = message;
        this.type = type;
        this.uploadingImageState = uploadingImageState;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UploadingImageState getUploadingImageState() {
        return uploadingImageState;
    }

    public void setUploadingImageState(UploadingImageState uploadingImageState) {
        this.uploadingImageState = uploadingImageState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageWrapper)) return false;
        MessageWrapper that = (MessageWrapper) o;
        return type == that.type &&
                message.getMessage().equals(that.message.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, type);
    }

    public static final int LOADING = 1;
    public static final int MESSAGE = 2;
    public static final int IMAGE = 3;
}

enum UploadingImageState{
    UPLOADING , UPLOAD_DONE , CANCELLED
}
