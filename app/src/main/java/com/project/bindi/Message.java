package com.project.bindi;

public class Message {
    private String MessageId;
    private String senderId;
    private String receiverId;
    private String message;
    private String voiceMessageLink;

    public Message() {
    }

    public static final String parentLocation="Messages";

    public Message(String messageId, String senderId, String receiverId, String message, String voiceMessageLink) {
        MessageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.voiceMessageLink = voiceMessageLink;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVoiceMessageLink() {
        return voiceMessageLink;
    }

    public void setVoiceMessageLink(String voiceMessageLink) {
        this.voiceMessageLink = voiceMessageLink;
    }
}
