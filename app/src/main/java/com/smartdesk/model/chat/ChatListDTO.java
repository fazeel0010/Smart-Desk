package com.smartdesk.model.chat;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatListDTO implements Comparable<ChatListDTO>{
    @ServerTimestamp
    Date date;

    String name;
    String profileUrl;
    String phoneNumber;
    String otherDocumentID;


    public ChatListDTO() {
    }

    public ChatListDTO(Date date, String name, String profileUrl, String phoneNumber, String otherDocumentID) {
        this.date = date;
        this.name = name;
        this.profileUrl = profileUrl;
        this.phoneNumber = phoneNumber;
        this.otherDocumentID = otherDocumentID;
    }

    public String getOtherDocumentID() {
        return otherDocumentID;
    }

    public void setOtherDocumentID(String otherDocumentID) {
        this.otherDocumentID = otherDocumentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(ChatListDTO o) {
        try {
            return o.getDate().compareTo(this.getDate());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
