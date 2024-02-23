package com.brinfotech.feedbacksystem.data.visitor;

public class LoginVisitorResponseModel {
    String status;
    LoginVisitorDataModel data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LoginVisitorDataModel getData() {
        return data;
    }

    public void setData(LoginVisitorDataModel data) {
        this.data = data;
    }
}
