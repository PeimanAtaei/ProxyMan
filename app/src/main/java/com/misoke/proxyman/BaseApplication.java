package com.misoke.proxyman;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.misoke.proxyman.utils.SessionManager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends Application {

    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        sessionManager = new SessionManager(getApplicationContext());

        AndroidNetworking.initialize(getApplicationContext());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSansMobile(FaNum).ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }



}
