package com.brinfotech.feedbacksystem.ui.userSelection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.brinfotech.feedbacksystem.R;
import com.brinfotech.feedbacksystem.baseClasses.BaseActivity;
import com.brinfotech.feedbacksystem.helpers.ConstantClass;

import butterknife.BindView;

public class UserSelectionActivity extends BaseActivity {

    @BindView(R.id.txtSignIn)
    TextView txtSignIn;

    @BindView(R.id.txtSignOut)
    TextView txtSignOut;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_user_selection;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(ConstantClass.EXTRAA_SELECTED_OPTION_TYPE, ConstantClass.REQUEST_SIGN_IN);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        txtSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(ConstantClass.EXTRAA_SELECTED_OPTION_TYPE, ConstantClass.REQUEST_SIGN_OUT);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
