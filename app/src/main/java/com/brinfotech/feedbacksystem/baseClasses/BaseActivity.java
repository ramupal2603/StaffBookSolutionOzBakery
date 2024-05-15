package com.brinfotech.feedbacksystem.baseClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.brinfotech.feedbacksystem.R;
import com.brinfotech.feedbacksystem.customClasses.ProgressLoader;
import com.brinfotech.feedbacksystem.data.signINOut.ScanQrCodeResponseModel;
import com.brinfotech.feedbacksystem.data.signINOut.SignInOutParamsModel;
import com.brinfotech.feedbacksystem.data.signINOut.SignInOutRequestModel;
import com.brinfotech.feedbacksystem.helpers.ConstantClass;
import com.brinfotech.feedbacksystem.helpers.DateTimeUtils;
import com.brinfotech.feedbacksystem.helpers.PreferenceKeys;
import com.brinfotech.feedbacksystem.network.RetrofitClient;
import com.brinfotech.feedbacksystem.network.RetrofitInterface;
import com.brinfotech.feedbacksystem.network.utils.WebApiHelper;
import com.brinfotech.feedbacksystem.ui.manualVisitor.ManualVisitorSignIn;
import com.brinfotech.feedbacksystem.ui.manualVisitor.ManualVisitorSignOut;
import com.brinfotech.feedbacksystem.ui.Utils;
import com.brinfotech.feedbacksystem.ui.manualStaff.ManualStaffSignInSignOut;
import com.brinfotech.feedbacksystem.ui.siteSelectionView.SiteSelectionViewActivity;
import com.brinfotech.feedbacksystem.ui.thankYouPage.ThankYouScreen;
import com.brinfotech.feedbacksystem.ui.userSelection.UserSelectionActivity;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    Unbinder unbinder = null;
    @Nullable
    @BindView(R.id.txtTime)
    TextView txtTime;
    @Nullable
    @BindView(R.id.txtDate)
    TextView txtDate;
    CountDownTimer newTimer;
    private ProgressLoader loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        unbinder = ButterKnife.bind(this);
        Utils.hideKeyBoard(getActivity());

        showTime();

        showDate();

        if (txtTime != null) {
            txtTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), SiteSelectionViewActivity.class));
                    finish();
                }
            });
        }

    }

    private void showDate() {
        if (txtDate != null) {
            String date = DateTimeUtils.getCurrentDate(getActivity());
            String displayDate = DateTimeUtils.getDesiredDateTime(date, "dd/MM/yyyy", "EEEE dd MMM YYYY");
            txtDate.setText(displayDate);

        }
    }

    private void showTime() {
        newTimer = new CountDownTimer(1000000000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (txtTime != null) {
                    txtTime.setText(String.format("%s", DateTimeUtils.getCurrentTime(getContext())));
                }

                showDate();
            }

            public void onFinish() {

            }
        };
        newTimer.start();
    }


    protected abstract int getLayoutResource();

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }

        if (newTimer != null) {
            newTimer.cancel();
        }
    }

    public void showToastMessage(String errorMessage) {

        try {
            Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(15);
            toast.show();
        } catch (Exception e) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public void printLogMessage(String tag, String errorMessage) {
        Log.e(tag, errorMessage);
    }

    public void showProgressBar() {

        //Check if Activity is null then close activity.
        if (getActivity() == null) {
            return;
        } else {
            //If loader instance is null then re-create object.
            if (loader == null) {
                loader = new ProgressLoader(getActivity());
            }

            //If progress bar is not showing then show progress bar.
            if (!loader.isShowing()) {
                loader.show();
            }
        }

    }

    public void hideProgressBar() {

        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }

    public void showAlertDialog(Context context, String message) {

        try {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage(message);
            builder1.setTitle(context.getResources().getString(R.string.app_name));
            builder1.setCancelable(true);
            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();

                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {

        }

    }

    public void showErrorMessage() {
        showToastMessage(getActivity().getResources().getString(R.string.something_went_wrong));
    }

    public void showNoSitesMessage() {
        showToastMessage(getActivity().getResources().getString(R.string.no_site_msg));
    }

    public void showNoNetworkMessage() {
        showToastMessage(getActivity().getResources().getString(R.string.no_internet_connection));
    }

    public boolean isUserLoggedIn() {
        return Prefs.getBoolean(PreferenceKeys.USER_LOGGED_IN, false);
    }

    public void openThankYouActivity(String status, String userName) {
        Intent intent = new Intent(getActivity(), ThankYouScreen.class);
        intent.putExtra(ConstantClass.EXTRAA_FORM_DATA, status);
        intent.putExtra(ConstantClass.EXTRAA_FORM_NAME, userName);
        startActivity(intent);
    }

    public void openUserSelectionActivity(int requestedFor) {
        Intent intent = new Intent(getActivity(), UserSelectionActivity.class);
        startActivityForResult(intent, requestedFor);
    }

    public void openManualStaffView(int action) {
        Intent intent = new Intent(getActivity(), ManualStaffSignInSignOut.class);
        intent.putExtra(ConstantClass.EXTRAA_SIGN_IN_OUT, action);
        startActivity(intent);
    }

    public void openManualVisitorSignInView() {
        Intent intent = new Intent(getActivity(), ManualVisitorSignIn.class);
        startActivity(intent);
    }

    public void openManualVisitorSignOutView() {
        Intent intent = new Intent(getActivity(), ManualVisitorSignOut.class);
        startActivity(intent);
    }

    public Context getContext() {
        return BaseActivity.this;
    }

    public BaseActivity getActivity() {
        return BaseActivity.this;
    }

    public void callSignInOutMethodForManual(String scannedId) {
        printLogMessage("userID", "" + scannedId);

        showProgressBar();

        RetrofitInterface apiService = RetrofitClient.getRetrofit().create(RetrofitInterface.class);
        apiService.manualSignInOut(getSignInOutRequest(scannedId)).enqueue(new Callback<ScanQrCodeResponseModel>() {
            @Override
            public void onResponse(Call<ScanQrCodeResponseModel> call, Response<ScanQrCodeResponseModel> response) {
                hideProgressBar();
                if (response.isSuccessful()) {
                    ScanQrCodeResponseModel responseModel = response.body();
                    if (responseModel != null)
                        if (responseModel.getStatus().equals(ConstantClass.RESPONSE_SUCCESS_SIGN_IN)
                                || responseModel.getStatus().equals(ConstantClass.RESPONSE_SUCCESS_SIGN_OUT)) {
                            openThankYouActivity(responseModel.getStatus(), responseModel.getVisitor_details().get(0).getUser_name());
                            getActivity().finish();
                        } else {
                            showErrorMessage();
                        }
                } else {
                    showErrorMessage();
                }

            }

            @Override
            public void onFailure(Call<ScanQrCodeResponseModel> call, Throwable t) {
                t.printStackTrace();
                hideProgressBar();
                showErrorMessage();
            }
        });
    }

    private SignInOutRequestModel getSignInOutRequest(String scannedId) {
        SignInOutRequestModel requestModel = new SignInOutRequestModel();
        SignInOutParamsModel paramsModel = new SignInOutParamsModel();
        paramsModel.setUser_id(scannedId);
        paramsModel.setDevice_type(WebApiHelper.DEVICE_TYPE_TAB);
        paramsModel.setSite_id(Prefs.getString(PreferenceKeys.SITE_ID, "1"));
        requestModel.setParam(paramsModel);

        return requestModel;
    }

    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}

