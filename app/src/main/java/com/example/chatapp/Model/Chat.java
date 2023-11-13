package com.example.chatapp.Model;

public class Chat {
    String chatId;
    String sender;
    String receiver;
    String message;
    String imageUrl;
    boolean isseen;
    long publish;
    boolean clickOne;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message , boolean isseen , String imageUrl) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.imageUrl = imageUrl;
        this.isseen = isseen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getPublish() {
        return publish;
    }

    public void setPublish(long publish) {
        this.publish = publish;
    }

    public boolean isClickOne() {
        return clickOne;
    }

    public void setClickOne(boolean clickOne) {
        this.clickOne = clickOne;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
