package com.smartdesk.model.payment_history;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PaymentHistoryDTO implements Comparable<PaymentHistoryDTO> {

    String company;
    String model;

    String amount;

    String documentID;

    @ServerTimestamp
    Date dateFomat;


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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public Date getDateFomat() {
        return dateFomat;
    }

    public void setDateFomat(Date dateFomat) {
        this.dateFomat = dateFomat;
    }

    @Override
    public int compareTo(PaymentHistoryDTO o) {
        try {
            return o.getDateFomat().compareTo(this.getDateFomat());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
