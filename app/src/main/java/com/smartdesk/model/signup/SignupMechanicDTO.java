package com.smartdesk.model.signup;

import com.smartdesk.constants.Constants;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SignupMechanicDTO {

    String workerName;
    String workerPhone;
    String workerEmail;
    String workerDob;
    String workerGender;
    String workerPassword;
    Double workerLat;
    Double workerLng;

    String shopLocation;

    String profilePicture;

    Integer role = Constants.workerRole;
    String uuID;
    String userStatus;

    Integer ratingUserCount;
    Integer ratingTotal;


    //Online status
    Boolean online;
    Boolean isHired;
    String workerDocumentID;

    //local variable
    String localDocuementID;
    Boolean havingShop;
    Double distance;

    @ServerTimestamp
    Date registrationDate;

    public String getWorkerDocumentID() {
        return workerDocumentID;
    }

    public void setWorkerDocumentID(String workerDocumentID) {
        this.workerDocumentID = workerDocumentID;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public SignupMechanicDTO() {
    }

    public SignupMechanicDTO(SignupMechanicDTO obj) {
        this.setWorkerName(obj.getWorkerName());
        this.setWorkerPhone(obj.getWorkerPhone());
        this.setWorkerDob(obj.getWorkerDob());
        this.setWorkerGender(obj.getWorkerGender());
        this.setWorkerPassword(obj.getWorkerPassword());
        this.setWorkerLat(obj.getWorkerLat());
        this.setWorkerLng(obj.getWorkerLng());

        this.setShopLocation(obj.getShopLocation());

        this.setProfilePicture(obj.getProfilePicture());

        this.setRole(obj.getRole());
        this.setUuID(obj.getUuID());
        this.setUserStatus(obj.getUserStatus());

        this.setRatingUserCount(obj.getRatingUserCount());
        this.setRatingTotal(obj.getRatingTotal());

        this.setLocalDocuementID(obj.getLocalDocuementID());

        this.setOnline(obj.getOnline());
        this.setHired(obj.getHired());

        this.setHavingShop(true);
        this.setDistance(obj.getDistance());
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

    public String getWorkerDob() {
        return workerDob;
    }

    public void setWorkerDob(String workerDob) {
        this.workerDob = workerDob;
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

    public String getShopLocation() {
        return shopLocation;
    }

    public void setShopLocation(String shopLocation) {
        this.shopLocation = shopLocation;
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

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public Integer getRatingUserCount() {
        return ratingUserCount;
    }

    public void setRatingUserCount(Integer ratingUserCount) {
        this.ratingUserCount = ratingUserCount;
    }

    public Integer getRatingTotal() {
        return ratingTotal;
    }

    public void setRatingTotal(Integer ratingTotal) {
        this.ratingTotal = ratingTotal;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Boolean getHired() {
        return isHired;
    }

    public void setHired(Boolean hired) {
        isHired = hired;
    }

    public String getLocalDocuementID() {
        return localDocuementID;
    }

    public void setLocalDocuementID(String localDocuementID) {
        this.localDocuementID = localDocuementID;
    }

    public Boolean getHavingShop() {
        return havingShop;
    }

    public void setHavingShop(Boolean havingShop) {
        this.havingShop = havingShop;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getWorkerEmail() {
        return workerEmail;
    }

    public void setWorkerEmail(String workerEmail) {
        this.workerEmail = workerEmail;
    }
}
