package com.smartdesk.model.signup;

import com.smartdesk.constants.Constants;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SignupConsumerDTO {

    String workerName;
    String workerPhone;
    String workerGender;
    String workerPassword;
    Double workerLat;
    Double workerLng;

    String city;

    String profilePicture;

    Integer role = Constants.consumerRole;
    String uuID;
    String userStatus;

    @ServerTimestamp
    Date registrationDate;

    //Online status
    Boolean isHired;
    String workerDocumentID;

    public Boolean getHired() {
        return isHired;
    }

    public void setHired(Boolean hired) {
        isHired = hired;
    }

    public String getWorkerDocumentID() {
        return workerDocumentID;
    }

    public void setWorkerDocumentID(String workerDocumentID) {
        this.workerDocumentID = workerDocumentID;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerPhone() {
        return workerPhone;
    }

    public void setWorkerPhone(String workerPhone) {
        this.workerPhone = workerPhone;
    }

    public String getWorkerGender() {
        return workerGender;
    }

    public void setWorkerGender(String workerGender) {
        this.workerGender = workerGender;
    }

    public String getWorkerPassword() {
        return workerPassword;
    }

    public void setWorkerPassword(String workerPassword) {
        this.workerPassword = workerPassword;
    }

    public Double getWorkerLat() {
        return workerLat;
    }

    public void setWorkerLat(Double workerLat) {
        this.workerLat = workerLat;
    }

    public Double getWorkerLng() {
        return workerLng;
    }

    public void setWorkerLng(Double workerLng) {
        this.workerLng = workerLng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getUuID() {
        return uuID;
    }

    public void setUuID(String uuID) {
        this.uuID = uuID;
    }
}
