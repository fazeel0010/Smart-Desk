package com.smartdesk.model.hire_worker;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class WorkerRequestDTO {
    String company;
    String model;

    String name;
    String number;
    String profileImage;


    String workerDocumentID;
    String consumerDocumentID;

    String deleteMainRequestDocID;

    String workerStatus;

    String amount;

    Boolean hired;

    public Boolean getHired() {
        return hired;
    }

    public void setHired(Boolean hired) {
        this.hired = hired;
    }

    @ServerTimestamp
    Date registrationDate;

    public String getDeleteMainRequestDocID() {
        return deleteMainRequestDocID;
    }

    public void setDeleteMainRequestDocID(String deleteMainRequestDocID) {
        this.deleteMainRequestDocID = deleteMainRequestDocID;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getWorkerDocumentID() {
        return workerDocumentID;
    }

    public void setWorkerDocumentID(String workerDocumentID) {
        this.workerDocumentID = workerDocumentID;
    }

    public String getConsumerDocumentID() {
        return consumerDocumentID;
    }

    public void setConsumerDocumentID(String consumerDocumentID) {
        this.consumerDocumentID = consumerDocumentID;
    }

    public String getWorkerStatus() {
        return workerStatus;
    }

    public void setWorkerStatus(String workerStatus) {
        this.workerStatus = workerStatus;
    }
}
