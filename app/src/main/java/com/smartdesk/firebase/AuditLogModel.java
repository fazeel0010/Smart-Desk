package com.smartdesk.firebase;


import com.google.firebase.firestore.ServerTimestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AuditLogModel {


    public String mobile;
    public String userRole;
    @ServerTimestamp
    public Date date;
    public String action;
    public String details;
    public MobileDetails mobileDetails = new MobileDetails();

    public AuditLogModel() {
        mobileDetails = new MobileDetails();
    }

    public AuditLogModel(String details, String action, String mobile, String userRole, MobileDetails mobileDetails) {
        this.details = details;
        this.action = action;
        this.mobile = mobile;
        this.userRole = userRole;
        try {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = dateFormat.format(date);
            this.date = date;
        } catch (Exception ex) {
        }
        this.mobileDetails = new MobileDetails();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public MobileDetails getMobileDetails() {
        return mobileDetails;
    }

    public void setMobileDetails(MobileDetails mobileDetails) {
        this.mobileDetails = mobileDetails;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return date.toString() + mobile+","+userRole+","+action+","+details+","+mobileDetails.model+","+mobileDetails.brand + ","+mobileDetails.appVersion+","+mobileDetails.model+";";
    }
}
