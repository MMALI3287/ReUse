package com.example.reuse;

public class ChatMessage {
    private String senderName;
    private String messageText;
    private long timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String senderName, String messageText, long timestamp) {
        this.senderName = senderName;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
