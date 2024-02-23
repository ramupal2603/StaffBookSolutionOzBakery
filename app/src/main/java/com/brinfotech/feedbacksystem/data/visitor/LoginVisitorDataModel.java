package com.brinfotech.feedbacksystem.data.visitor;

public class LoginVisitorDataModel {
    String visitor_id;
    String company_name;
    String name;
    String staff_name;
    String visitor_organization;

    public String getVisitor_id() {
        return visitor_id;
    }

    public void setVisitor_id(String visitor_id) {
        this.visitor_id = visitor_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public void setStaff_name(String staff_name) {
        this.staff_name = staff_name;
    }

    public String getVisitor_organization() {
        return visitor_organization;
    }

    public void setVisitor_organization(String visitor_organization) {
        this.visitor_organization = visitor_organization;
    }
}
