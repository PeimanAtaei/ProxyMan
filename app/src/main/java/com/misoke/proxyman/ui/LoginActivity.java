package com.misoke.proxyman.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.misoke.proxyman.BaseActivity;
import com.misoke.proxyman.R;
import com.misoke.proxyman.models.AuthModel;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.UserModel;
import com.misoke.proxyman.network.AuthApi;
import com.misoke.proxyman.utils.Helpers;
import com.misoke.proxyman.utils.SessionManager;


public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private SessionManager sessionManager;

    private AppCompatEditText inputPhoneNumber;
    private AppCompatEditText inputPassword;
    private AppCompatButton btnLogin;
    private AppCompatTextView btnLinkRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        setupViews();
        listeners();

        Helpers.getRemoteAddress(sessionManager);
    }

    private void initialize() {
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
    }

    private void setupViews() {
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLinkRegister = findViewById(R.id.btnLinkRegister);
    }

    private void listeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String phoneNumber = inputPhoneNumber.getText().toString();
                String password = inputPassword.getText().toString();

                if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(password)) {
                    login(phoneNumber, password);
                }
            }
        });

        btnLinkRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });
    }

    private void login(String phoneNumber, String password) {

        progressDialog.setMessage(getString(R.string.login_dialog_message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        AuthModel authModel = new AuthModel();
        authModel.setPhoneNumber(phoneNumber);
        authModel.setPassword(password);
        authModel.setRemoteAddress(sessionManager.getUserRemoteAddress());

        AuthApi authApi = new AuthApi();
        authApi.login(authModel, new AuthApi.OnLoginListener() {

            @Override
            public void onLogin(UserModel userModel) {
                sessionManager.saveUserModel(userModel);

                if (!userModel.isActive()) {
                    sessionManager.setPayment(false);
                    Intent mainIntent = new Intent(LoginActivity.this, PaymentActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    sessionManager.setPayment(true);
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onError(ErrorModel errorModel) {
                Toast.makeText(LoginActivity.this, "onError: " + errorModel.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
