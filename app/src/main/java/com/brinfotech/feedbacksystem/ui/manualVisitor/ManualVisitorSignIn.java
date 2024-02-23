package com.brinfotech.feedbacksystem.ui.manualVisitor;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.brinfotech.feedbacksystem.R;
import com.brinfotech.feedbacksystem.baseClasses.BaseActivity;
import com.brinfotech.feedbacksystem.data.getStaffList.StaffListParamModel;
import com.brinfotech.feedbacksystem.data.getStaffList.StaffListRequestModel;
import com.brinfotech.feedbacksystem.data.getStaffList.StaffListResponseModel;
import com.brinfotech.feedbacksystem.data.nameList.NameListDataModel;
import com.brinfotech.feedbacksystem.data.visitor.LoginVisitorParamModel;
import com.brinfotech.feedbacksystem.data.visitor.LoginVisitorRequestModel;
import com.brinfotech.feedbacksystem.data.visitor.LoginVisitorResponseModel;
import com.brinfotech.feedbacksystem.helpers.ConstantClass;
import com.brinfotech.feedbacksystem.helpers.PreferenceKeys;
import com.brinfotech.feedbacksystem.helpers.StringUtils;
import com.brinfotech.feedbacksystem.network.RetrofitClient;
import com.brinfotech.feedbacksystem.network.RetrofitInterface;
import com.brinfotech.feedbacksystem.network.utils.WebApiHelper;
import com.brinfotech.feedbacksystem.ui.StaffNameListAdapter;
import com.brinfotech.feedbacksystem.ui.VisitorSurnameListAdapter;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManualVisitorSignIn extends BaseActivity {

    @BindView(R.id.edtFirstName)
    EditText edtFirstName;

    @BindView(R.id.edtSurName)
    EditText edtSurName;

    @BindView(R.id.edtOrganization)
    EditText edtOrganization;

    @BindView(R.id.edtStaff)
    AutoCompleteTextView edtStaff;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSignIn)
    ImageView imgSignIn;

    @BindView(R.id.loutStaffView)
    LinearLayout loutStaffView;

    ArrayList<NameListDataModel> arrStaffList = new ArrayList<NameListDataModel>();
    String selectedStaffID = "";

    public String gdprMessage = "<p><b>WELCOME TO Nikwax</b></p>\n" +
            "<p><br />WHILST ON OUR PREMISES THE FOLLOWING REGULATIONS MUST BE OBSERVED</p>\n" +
            "<p><br />&bull; <b>HEALTH &amp; SAFETY</b><br />All visitors are subject to the Health &amp; Safety at Work Act 1974 and other site regulations</p>\n" +
            "<p>&bull; <b>FIRE &amp; EMERGENCY</b><br />In the case of emergency, all Visitors must accompany their Host and report to Assembly Point.<br />DO NOT USE THE LIFTS.</p>\n" +
            "<p>&bull; <b>PACKAGES</b><br />Packages and Cases must not be left unattended.</p>\n" +
            "<p>&bull; <b>SMOKING</b><br />It is against the law to smoke on these premises</p>\n" +
            "<p>&bull; <b>PROPERTY</b><br />The company accepts no responsibility for any loss or damage to visitor&rsquo;s property</p>\n" +
            "<p><br />PLEASE SIGN OUT BEFORE LEAVING THE PREMISES</p>" +
            "<br /><b>GDPR Compliance Message</b>\n" +
            "<br /><br />• We collect basic information for the purposes of Health and Safety, and for archiving.\n" +
            "<br /><br />• We are required to know who is present on our premises in case of an emergency evacuation.\n" +
            "<br /><br />• We are required to know who has been present on our premises for our own information.\n" +
            "<br /><br />• Your details are kept archived and encrypted on our premises and on UK based cloud servers for backup.";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setOnClickListener(this::onClick);
        imgSignIn.setOnClickListener(this::onClick);
        loutStaffView.setOnClickListener(this::onClick);

        getStaffRecords();

        edtStaff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                hideKeyBoard();
                for (int i = 0; i < arrStaffList.size(); i++) {
                    if (arrStaffList.get(i).getName().toLowerCase().equals
                            (edtStaff.getText().toString().toLowerCase().trim())) {
                        selectedStaffID = arrStaffList.get(i).getId();
                        edtStaff.setText(arrStaffList.get(i).getName());
                        hideKeyBoard();
                        break;
                    }
                }

            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_normal_visitor_sign_in;
    }

    @Override
    public void onClick(View view) {

        if (view == imgBack) {
            finish();
        }

        if (view == imgSignIn) {
            validateSignIn();
        }
    }


    public void showDisclaimerDialog() {
        final Dialog dialog = new Dialog(ManualVisitorSignIn.this);
        // Include dialog.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_disclaimer);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TextView dis_txt = dialog.findViewById(R.id.dis_txt);
        dis_txt.setText(Html.fromHtml(gdprMessage));

        LinearLayout accept = dialog.findViewById(R.id.accept);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);

        // if decline button is clicked, close the custom dialog
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNormalVisitor();
                dialog.dismiss();
            }
        });
        LinearLayout declineButton = dialog.findViewById(R.id.decline);

        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        dialog.show();
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
                            edtOrganization.clearFocus();
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

    private void setUpDataAdapter() {
        edtStaff.setThreshold(2);
        StaffNameListAdapter visitorSurnameListAdapter = new StaffNameListAdapter(this, arrStaffList);
        edtStaff.setAdapter(visitorSurnameListAdapter);
    }

    private StaffListRequestModel getStaffSelectionRequest() {

        String siteID = Prefs.getString(PreferenceKeys.SITE_ID, "0");
        StaffListRequestModel requestModel = new StaffListRequestModel();
        StaffListParamModel staffListParamModel = new StaffListParamModel();
        staffListParamModel.setCompany_id(WebApiHelper.COMPANY_ID);
        staffListParamModel.setStaff_status(ConstantClass.STAFF_ALL);
        staffListParamModel.setSite_id(siteID);
        requestModel.setParam(staffListParamModel);
        return requestModel;
    }


    void staffListDialog() {
        final Dialog dialog = new Dialog(ManualVisitorSignIn.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.searchview);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ListView lv = dialog.findViewById(R.id.listView1);
        TextView header = dialog.findViewById(R.id.header);
        header.setText("Select Staff");
        Button btn = dialog.findViewById(R.id.cancel);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ManualVisitorSignIn.this, android.R.layout.select_dialog_singlechoice);


        for (int i = 0; i < arrStaffList.size(); i++) {
            arrayAdapter.add(arrStaffList.get(i).getName());
        }

        lv.setAdapter(arrayAdapter);
        //SEARCH
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strName = parent.getItemAtPosition(position).toString();

                for (int i = 0; i < arrStaffList.size(); i++) {
                    if (arrStaffList.get(i).getName().equalsIgnoreCase(strName)) {
                        edtStaff.setText(arrStaffList.get(i).getName());
                        selectedStaffID = arrStaffList.get(i).getId();
                        break;
                    }
                }

                dialog.dismiss();
            }
        });

        EditText sv = (EditText) dialog.findViewById(R.id.search);
        sv.setHint("Search Name or scroll down");
        sv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                arrayAdapter.getFilter().filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //BUTTON
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void validateSignIn() {
        if (StringUtils.checkEmptyEditText(edtFirstName)) {
            showToastMessage("Please enter first name");
        } else if (StringUtils.checkEmptyEditText(edtSurName)) {
            showToastMessage("Please enter sur name");
        } else if (StringUtils.checkEmptyEditText(edtOrganization)) {
            showToastMessage("Please enter your organization");
        } else if (selectedStaffID.equals("")) {
            showToastMessage("Please select your staff");
        } else {
            showDisclaimerDialog();

        }
    }

    private void insertNormalVisitor() {
        showProgressBar();

        RetrofitInterface apiService = RetrofitClient.getRetrofit().create(RetrofitInterface.class);
        apiService.loginVisitor(getRequest()).enqueue(new Callback<LoginVisitorResponseModel>() {
            @Override
            public void onResponse(Call<LoginVisitorResponseModel> call, Response<LoginVisitorResponseModel> response) {
                hideProgressBar();
                if (response.isSuccessful()) {
                    LoginVisitorResponseModel responseModel = response.body();
                    if (responseModel != null)
                        if (responseModel.getStatus().equals(ConstantClass.RESPONSE_SUCCESS_SIGN_IN)
                                || responseModel.getStatus().equals(ConstantClass.RESPONSE_SUCCESS_SIGN_OUT)) {
                            openThankYouActivity(responseModel.getStatus(), responseModel.getData().getName());
                            finish();
                        } else {
                            showErrorMessage();
                        }
                } else {
                    showErrorMessage();
                }

            }

            @Override
            public void onFailure(Call<LoginVisitorResponseModel> call, Throwable t) {
                t.printStackTrace();
                hideProgressBar();
                showErrorMessage();
            }
        });
    }

    private LoginVisitorRequestModel getRequest() {
        LoginVisitorRequestModel requestModel = new LoginVisitorRequestModel();
        LoginVisitorParamModel paramModel = new LoginVisitorParamModel();
        paramModel.setFirst_name(edtFirstName.getText().toString().trim());
        paramModel.setSur_name(edtSurName.getText().toString().trim());
        paramModel.setVisitor_organization(edtOrganization.getText().toString().trim());
        paramModel.setCompany_id(WebApiHelper.COMPANY_ID);
        paramModel.setStaff_id(selectedStaffID);
        paramModel.setSite_id(Prefs.getString(PreferenceKeys.SITE_ID, "0"));
        requestModel.setParam(paramModel);
        return requestModel;
    }
}
