package com.smartdesk.model.chat;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MessageChatDTO implements Comparable<MessageChatDTO> {
    @ServerTimestamp
    Date date;

    String message;
    String senderDocumentID;
    String recieverDocuementID;

    public MessageChatDTO() {
    }

    public MessageChatDTO(Date date, String message, String senderDocumentID, String recieverDocuementID) {
        this.date = date;
        this.message = message;
        this.senderDocumentID = senderDocumentID;
        this.recieverDocuementID = recieverDocuementID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderDocumentID() {
        return senderDocumentID;
    }

    public void setSenderDocumentID(String senderDocumentID) {
        this.senderDocumentID = senderDocumentID;
    }

    public String getRecieverDocuementID() {
        return recieverDocuementID;
    }

    public void setRecieverDocuementID(String recieverDocuementID) {
        this.recieverDocuementID = recieverDocuementID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(MessageChatDTO o) {
        try {
            return o.getDate().compareTo(this.getDate());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
