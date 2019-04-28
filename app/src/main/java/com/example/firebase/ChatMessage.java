package com.example.firebase;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String urlImage;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser,String urlImage) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.urlImage=urlImage;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
