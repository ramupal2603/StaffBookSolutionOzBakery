package com.brinfotech.feedbacksystem.ui.manualVisitor;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.brinfotech.feedbacksystem.R;
import com.brinfotech.feedbacksystem.baseClasses.BaseActivity;
import com.brinfotech.feedbacksystem.data.getVisitorList.VisitorListRequestModel;
import com.brinfotech.feedbacksystem.data.getVisitorList.VisitorListResponseModel;
import com.brinfotech.feedbacksystem.data.getVisitorList.VisitorParamModel;
import com.brinfotech.feedbacksystem.data.nameList.NameListDataModel;
import com.brinfotech.feedbacksystem.helpers.ConstantClass;
import com.brinfotech.feedbacksystem.helpers.PreferenceKeys;
import com.brinfotech.feedbacksystem.network.RetrofitClient;
import com.brinfotech.feedbacksystem.network.RetrofitInterface;
import com.brinfotech.feedbacksystem.network.utils.WebApiHelper;
import com.brinfotech.feedbacksystem.ui.VisitorSurnameListAdapter;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManualVisitorSignOut extends BaseActivity {

    @BindView(R.id.edtVisitorName)
    AutoCompleteTextView edtVisitorName;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSignOut)
    TextView imgSignOut;

    ArrayList<NameListDataModel> arrVisitorList = new ArrayList<NameListDataModel>();
    String selectedVisitorID = "";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_visitor_manual_sign_out;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerClickListener();

        getVisitorRecords();

        setUpDataAdapter();
    }

    private void getVisitorRecords() {

        showProgressBar();

        RetrofitInterface apiService = RetrofitClient.getRetrofit().create(RetrofitInterface.class);
        apiService.getVisitorList(getVisitorSelectionRequest()).enqueue(new Callback<VisitorListResponseModel>() {
            @Override
            public void onResponse(Call<VisitorListResponseModel> call, Response<VisitorListResponseModel> response) {
                hideProgressBar();
                if (response.isSuccessful()) {
                    VisitorListResponseModel responseModel = response.body();
                    if (responseModel != null) {
                        if (responseModel.getStatus().equals(ConstantClass.RESPONSE_SUCCESS)) {
                            arrVisitorList.clear();
                            arrVisitorList.addAll(responseModel.getData());
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
            public void onFailure(Call<VisitorListResponseModel> call, Throwable t) {

            }
        });

    }

    private VisitorListRequestModel getVisitorSelectionRequest() {

        String siteID = Prefs.getString(PreferenceKeys.SITE_ID, "0");
        VisitorListRequestModel requestModel = new VisitorListRequestModel();
        VisitorParamModel visitorParamModel = new VisitorParamModel();
        visitorParamModel.setCompany_id(WebApiHelper.COMPANY_ID);
        visitorParamModel.setSite_id(siteID);
        requestModel.setParam(visitorParamModel);
        return requestModel;
    }

    private void setUpDataAdapter() {
        edtVisitorName.setThreshold(2);
        VisitorSurnameListAdapter visitorSurnameListAdapter = new VisitorSurnameListAdapter(this, arrVisitorList);
        edtVisitorName.setAdapter(visitorSurnameListAdapter);
    }

    private void registerClickListener() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSignInOutMethodForManual(selectedVisitorID);
            }
        });

        edtVisitorName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                hideKeyBoard();
                for (int i = 0; i < arrVisitorList.size(); i++) {
                    if (arrVisitorList.get(i).getName().toLowerCase().equals
                            (edtVisitorName.getText().toString().toLowerCase().trim())) {
                        selectedVisitorID = arrVisitorList.get(i).getId() + "@" + arrVisitorList.get(i).getUser_type();
                        break;
                    }
                }
            }
        });
    }


}
