package com.brinfotech.feedbacksystem.ui.qrCodeScannerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.brinfotech.feedbacksystem.R;
import com.brinfotech.feedbacksystem.baseClasses.BaseActivity;
import com.brinfotech.feedbacksystem.data.signINOut.ScanQrCodeResponseModel;
import com.brinfotech.feedbacksystem.data.signINOut.SignInOutParamsModel;
import com.brinfotech.feedbacksystem.data.signINOut.SignInOutRequestModel;
import com.brinfotech.feedbacksystem.helpers.ConstantClass;
import com.brinfotech.feedbacksystem.helpers.PreferenceKeys;
import com.brinfotech.feedbacksystem.network.RetrofitClient;
import com.brinfotech.feedbacksystem.network.RetrofitInterface;
import com.brinfotech.feedbacksystem.network.utils.NetworkUtils;
import com.brinfotech.feedbacksystem.network.utils.WebApiHelper;
import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrCodeScannerViewActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {

    private static final int REQUEST_QR_CODE_SCANNER = 10000;
    private static final String[] CAMERA_AND_STORAGE =
            {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int MY_CAMERA_REQUEST_CODE = 1000;
    @BindView(R.id.scannerView)
    CodeScannerView qrCodeScanner;
    CodeScanner mCodeScanner;
    @BindView(R.id.loutStaffSignIn)
    LinearLayout loutStaffSignIn;

    @BindView(R.id.loutVisitorSignIn)
    LinearLayout loutVisitorSignIn;

    /*@BindView(R.id.loutSignOut)
    LinearLayout loutSignOut;*/
    List<BarcodeFormat> arrFormatList = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeScannerView();


        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleResult(result.getText());
                    }
                });
            }
        });

        loutVisitorSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserSelectionActivity(ConstantClass.USER_TYPE_VISITOR);
//                openManualVisitorSignInView();
            }
        });
        loutStaffSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserSelectionActivity(ConstantClass.USER_TYPE_STAFF);
//                openManualStaffView(ConstantClass.REQUEST_SIGN_IN);
            }
        });



        /*loutSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserSelectionActivity(ConstantClass.REQUEST_SIGN_OUT);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EasyPermissions.hasPermissions(QrCodeScannerViewActivity.this, CAMERA_AND_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_camera),
                    MY_CAMERA_REQUEST_CODE,
                    CAMERA_AND_STORAGE);
        } else {
            stopScanner();
            startScanner();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScanner();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeScannerView() {
        arrFormatList.add(BarcodeFormat.QR_CODE);
        mCodeScanner = new CodeScanner(this, qrCodeScanner);

        // Parameters (default values)
        mCodeScanner.setCamera(CodeScanner.CAMERA_FRONT);// or CAMERA_FRONT or specific camera id

        mCodeScanner.setFormats(arrFormatList);// ex. listOf(BarcodeFormat.QR_CODE)
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mCodeScanner.setScanMode(ScanMode.SINGLE);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFlashEnabled(false);
        qrCodeScanner.getRootView();
    }



    @Override
    protected int getLayoutResource() {
        return R.layout.activity_scan_qr_code;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void handleResult(String scannedId) {
        if (!scannedId.isEmpty()) {
            if (NetworkUtils.isNetworkConnected(getContext())) {
                stopScanner();
                callSignInOutMethod(scannedId);
            } else {
                showNoNetworkMessage();
            }

        }
    }


    private void callSignInOutMethod(String scannedId) {
        printLogMessage("userID", "" + scannedId);

        showProgressBar();

        RetrofitInterface apiService = RetrofitClient.getRetrofit().create(RetrofitInterface.class);
        apiService.scanQRCodeSignInOut(getSignInOutRequest(scannedId)).enqueue(new Callback<ScanQrCodeResponseModel>() {
            @Override
            public void onResponse(Call<ScanQrCodeResponseModel> call, Response<ScanQrCodeResponseModel> response) {
                hideProgressBar();
                if (response.isSuccessful()) {
                    ScanQrCodeResponseModel responseModel = response.body();
                    if (responseModel != null)
                        if (responseModel.getStatus().equals(ConstantClass.RESPONSE_SUCCESS_SIGN_IN)
                                || responseModel.getStatus().equals(ConstantClass.RESPONSE_SUCCESS_SIGN_OUT)) {
                            openThankYouActivity(responseModel.getStatus(), responseModel.getVisitor_details().get(0).getUser_name());
                        } else {
                            showInvalidQrCodeMessage();
                            restartPreview();
                        }
                } else {
                    showErrorMessage();
                    restartPreview();
                }

            }

            @Override
            public void onFailure(Call<ScanQrCodeResponseModel> call, Throwable t) {
                t.printStackTrace();
                hideProgressBar();
                showErrorMessage();
                restartPreview();
            }
        });
    }

    private void showInvalidQrCodeMessage() {
        showToastMessage(getActivity().getResources().getString(R.string.qr_code_not_valid_for_sign_in_out));
    }

    private void restartPreview() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startScanner();
            }
        }, 2000);
    }

    private void startScanner() {
        mCodeScanner.startPreview();
    }

    private void stopScanner() {
        mCodeScanner.releaseResources();
        mCodeScanner.stopPreview();
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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            startScanner();

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantClass.USER_TYPE_STAFF && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                int optionSelected = data.getIntExtra(ConstantClass.EXTRAA_SELECTED_OPTION_TYPE, ConstantClass.REQUEST_SIGN_IN);
                openManualStaffView(optionSelected);
            }
        }

        if (requestCode == ConstantClass.USER_TYPE_VISITOR && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                int optionSelected = data.getIntExtra(ConstantClass.EXTRAA_SELECTED_OPTION_TYPE, ConstantClass.REQUEST_SIGN_IN);
                if (optionSelected == ConstantClass.REQUEST_SIGN_IN) {
                    openManualVisitorSignInView();
                } else {
                    openManualVisitorSignOutView();
                }

            }
        }


    }
}
