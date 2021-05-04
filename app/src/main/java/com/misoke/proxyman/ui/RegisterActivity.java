package com.misoke.proxyman.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.misoke.proxyman.BaseActivity;
import com.misoke.proxyman.R;
import com.misoke.proxyman.models.AuthModel;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.UserModel;
import com.misoke.proxyman.network.AuthApi;
import com.misoke.proxyman.utils.Helpers;
import com.misoke.proxyman.utils.SessionManager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";
    private static final int PERMISSION_REQUEST_CODE = 200;

    private SessionManager sessionManager;

    private AppCompatEditText inputPhoneNumber;
    private AppCompatEditText inputPassword;
    private AppCompatEditText inputInvitationCode;
    private AppCompatButton btnRegister;
    private AppCompatTextView btnLinkLogin;

    private TelephonyManager telephonyManager;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialize();
        setupViews();
        listeners();

        Helpers.getRemoteAddress(sessionManager);

        if (!checkPermission()) {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean readStateAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (!readStateAccepted) {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }

                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(RegisterActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void initialize() {
        sessionManager = new SessionManager(this);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        progressDialog = new ProgressDialog(this);
    }

    private void setupViews() {
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        inputPassword = findViewById(R.id.inputPassword);
        inputInvitationCode = findViewById(R.id.inputInvitationCode);
        btnRegister = findViewById(R.id.btnRegister);
        btnLinkLogin = findViewById(R.id.btnLinkLogin);
    }

    private void listeners() {

        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String phoneNumber = inputPhoneNumber.getText().toString();
                String password = inputPassword.getText().toString();
                String invitationCode = inputInvitationCode.getText().toString();

                if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(password)) {
                    register(phoneNumber, password, invitationCode);
                }
            }
        });

        btnLinkLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    private void register(String phoneNumber, String password, String invitationCode) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        progressDialog.setMessage(getString(R.string.register_dialog_message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);


        String deviceID = telephonyManager.getDeviceId();

        AuthModel authModel = new AuthModel();
        authModel.setPhoneNumber(phoneNumber);
        authModel.setPassword(password);
        authModel.setRemoteAddress(sessionManager.getUserRemoteAddress());
        authModel.setDeviceId(deviceID);
        authModel.setInvitationCode(invitationCode);

        AuthApi authApi = new AuthApi();
        authApi.register(authModel, new AuthApi.OnRegisterListener() {

            @Override
            public void onRegister(UserModel userModel) {
                sessionManager.saveUserModel(userModel);
                sessionManager.setPayment(true);
                progressDialog.dismiss();

                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }

            @Override
            public void onError(ErrorModel errorModel) {
                Toast.makeText(RegisterActivity.this, "onError: " + errorModel.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
