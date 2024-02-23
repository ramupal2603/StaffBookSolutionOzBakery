package com.brinfotech.feedbacksystem.ui.manualStaff;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.brinfotech.feedbacksystem.R;
import com.brinfotech.feedbacksystem.baseClasses.BaseActivity;
import com.brinfotech.feedbacksystem.data.getStaffList.StaffListParamModel;
import com.brinfotech.feedbacksystem.data.getStaffList.StaffListRequestModel;
import com.brinfotech.feedbacksystem.data.getStaffList.StaffListResponseModel;
import com.brinfotech.feedbacksystem.data.nameList.NameListDataModel;
import com.brinfotech.feedbacksystem.helpers.ConstantClass;
import com.brinfotech.feedbacksystem.helpers.PreferenceKeys;
import com.brinfotech.feedbacksystem.network.RetrofitClient;
import com.brinfotech.feedbacksystem.network.RetrofitInterface;
import com.brinfotech.feedbacksystem.network.utils.WebApiHelper;
import com.brinfotech.feedbacksystem.ui.NameListAdapter;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManualStaffSignInSignOut extends BaseActivity {

    @BindView(R.id.edtStaffName)
    AutoCompleteTextView edtStaffName;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSignIn)
    TextView imgSignIn;

    @BindView(R.id.imgSignOut)
    TextView imgSignOut;

    ArrayList<NameListDataModel> arrStaffList = new ArrayList<NameListDataModel>();
    String selectedStaffID = "";
    String staffStatus = ConstantClass.STAFF_ALL;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_staff_manual_sign_in_out;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntentData();

        registerClickListener();

        getStaffRecords();

        setUpDataAdapter();
    }

    private void getStaffRecords() {

        showProgressBar();

        RetrofitInterface apiService = RetrofitClient.getRetrofit().create(RetrofitInterface.class);
        apiService.getStaffList(getStaffSelectionRequest()).enqueue(new Callback<StaffListResponseModel>() {
            @Override
            public void onResponse(Call<StaffListResponseModel> call, Response<StaffListResponseModel> response) {
                hideProgressBar();
                if (response.isSuccessful()) {
                    StaffListResponseModel responseModel = response.body();
                    if (responseModel != null) {
                        if (responseModel.getStatus().equals(ConstantClass.RESPONSE_SUCCESS)) {
                            arrStaffList.clear();
                            arrStaffList.addAll(responseModel.getData());
                            setUpDataAdapter();
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }

                } else {
                    showErrorMessage();
                }
            }

            @Override
            public void onFailure(Call<StaffListResponseModel> call, Throwable t) {

            }
        });

    }

    private StaffListRequestModel getStaffSelectionRequest() {

        String siteID = Prefs.getString(PreferenceKeys.SITE_ID, "0");
        StaffListRequestModel requestModel = new StaffListRequestModel();
        StaffListParamModel staffListParamModel = new StaffListParamModel();
        staffListParamModel.setCompany_id(WebApiHelper.COMPANY_ID);
        staffListParamModel.setStaff_status(staffStatus);
        staffListParamModel.setSite_id(siteID);
        requestModel.setParam(staffListParamModel);
        return requestModel;
    }

    private void setUpDataAdapter() {
        edtStaffName.setThreshold(2);
        NameListAdapter nameListAdapter = new NameListAdapter(this, arrStaffList);
        edtStaffName.setAdapter(nameListAdapter);
    }

    private void registerClickListener() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSignInOutMethodForManual(selectedStaffID);
            }
        });

        imgSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSignInOutMethodForManual(selectedStaffID);
            }
        });

        edtStaffName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                hideKeyBoard();
                for (int i = 0; i < arrStaffList.size(); i++) {
                    if (arrStaffList.get(i).getName().toLowerCase().equals
                            (edtStaffName.getText().toString().toLowerCase().trim())) {
                        selectedStaffID = arrStaffList.get(i).getId() + "@" + arrStaffList.get(i).getUser_type();
                        break;
                    }
                }
            }
        });
    }


    private void getIntentData() {
        int actionType = getIntent().getIntExtra(ConstantClass.EXTRAA_SIGN_IN_OUT, 0);
        if (actionType == ConstantClass.REQUEST_SIGN_IN) {
            imgSignIn.setVisibility(View.VISIBLE);
            imgSignOut.setVisibility(View.GONE);
            staffStatus = ConstantClass.STAFF_SIGNED_OUT;
        } else if (actionType == ConstantClass.REQUEST_SIGN_OUT) {
            imgSignIn.setVisibility(View.GONE);
            imgSignOut.setVisibility(View.VISIBLE);
            staffStatus = ConstantClass.STAFF_SIGNED_IN;
        }
    }
}
