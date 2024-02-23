package com.brinfotech.feedbacksystem.data.getStaffList;

import com.brinfotech.feedbacksystem.data.nameList.NameListDataModel;

import java.util.ArrayList;

public class StaffListResponseModel {
    String status;
    ArrayList<NameListDataModel> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<NameListDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<NameListDataModel> data) {
        this.data = data;
    }
}
