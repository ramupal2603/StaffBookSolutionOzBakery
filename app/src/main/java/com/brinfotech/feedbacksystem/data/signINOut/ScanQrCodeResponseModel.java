package com.brinfotech.feedbacksystem.data.signINOut;

import java.util.ArrayList;

public class ScanQrCodeResponseModel {
    String log_id;
    String status;
    ArrayList<ScanQrCodeDataModel> visitor_details;

    public ArrayList<ScanQrCodeDataModel> getVisitor_details() {
        return visitor_details;
    }

    public void setVisitor_details(ArrayList<ScanQrCodeDataModel> visitor_details) {
        this.visitor_details = visitor_details;
    }

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
