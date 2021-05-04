package com.misoke.proxyman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.misoke.proxyman.BaseActivity;
import com.misoke.proxyman.R;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.TimeResponseModel;
import com.misoke.proxyman.network.UserApi;
import com.misoke.proxyman.utils.SessionManager;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private SessionManager sessionManager;
    private UserApi userApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initialize();
        getTimePayment();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void initialize() {
        sessionManager = new SessionManager(this);
        userApi = new UserApi(sessionManager);
    }

    private void getTimePayment() {
        if (sessionManager.isAuthorized()) {
            userApi.getTimePayment(new UserApi.OnGetTimePaymentListener() {

                @Override
                public void onGet(TimeResponseModel timeResponseModel) {
                    if (timeResponseModel.isTime()) {
                        sessionManager.setPayment(true);
                    } else {
                        sessionManager.setPayment(false);
                    }
                }

                @Override
                public void onError(ErrorModel errorModel) {
                    sessionManager.setPayment(false);
                }
            });
        }
    }
}
