package com.brinfotech.feedbacksystem.network;


import com.brinfotech.feedbacksystem.data.getStaffList.StaffListRequestModel;
import com.brinfotech.feedbacksystem.data.getStaffList.StaffListResponseModel;
import com.brinfotech.feedbacksystem.data.getVisitorList.VisitorListRequestModel;
import com.brinfotech.feedbacksystem.data.getVisitorList.VisitorListResponseModel;
import com.brinfotech.feedbacksystem.data.signINOut.ScanQrCodeResponseModel;
import com.brinfotech.feedbacksystem.data.signINOut.SignInOutRequestModel;
import com.brinfotech.feedbacksystem.data.siteList.SiteListResponseModel;
import com.brinfotech.feedbacksystem.data.siteList.SiteRequestModel;
import com.brinfotech.feedbacksystem.data.visitor.LoginVisitorRequestModel;
import com.brinfotech.feedbacksystem.data.visitor.LoginVisitorResponseModel;
import com.brinfotech.feedbacksystem.network.utils.WebApiHelper;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {


    @POST(WebApiHelper.SIGN_IN_OUT_QRCODE)
    Call<ScanQrCodeResponseModel> scanQRCodeSignInOut(@Body SignInOutRequestModel requestModel);

    @POST(WebApiHelper.SIGN_IN_OUT_MANUAL)
    Call<ScanQrCodeResponseModel> manualSignInOut(@Body SignInOutRequestModel requestModel);

    @POST(WebApiHelper.GET_SITE_LIST)
    Call<SiteListResponseModel> getSiteList(@Body SiteRequestModel requestModel);

    @POST(WebApiHelper.GET_STAFF_LIST)
    Call<StaffListResponseModel> getStaffList(@Body StaffListRequestModel requestModel);

    @POST(WebApiHelper.LOGIN_VISITOR)
    Call<LoginVisitorResponseModel> loginVisitor(@Body LoginVisitorRequestModel requestModel);

    @POST(WebApiHelper.GET_VISITOR_LIST)
    Call<VisitorListResponseModel> getVisitorList(@Body VisitorListRequestModel requestModel);

}
