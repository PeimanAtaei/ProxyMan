package com.misoke.proxyman.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.misoke.proxyman.utils.Helpers;
import com.misoke.proxyman.utils.NetworkUtil;
import com.misoke.proxyman.utils.SessionManager;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";
    private boolean isNetworkMobile = false;
    private boolean isNetworkWifi = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        SessionManager sessionManager = new SessionManager(context);
        int status = NetworkUtil.getConnectivityStatus(context);

        if (sessionManager.isAuthorized()) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (status == NetworkUtil.NETWORK_STATUS_WIFI) {
                    try {
                        Helpers.changeRemoteAddress(sessionManager);
                        isNetworkWifi = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (status == NetworkUtil.NETWORK_STATUS_MOBILE) {
                    try {
                        Helpers.changeRemoteAddress(sessionManager);
                        isNetworkMobile = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
