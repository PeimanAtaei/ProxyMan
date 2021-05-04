package com.misoke.proxyman.utils;


import com.google.gson.Gson;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.ProxyModel;
import com.misoke.proxyman.models.RemoteAddressModel;
import com.misoke.proxyman.models.ResponseModel;
import com.misoke.proxyman.network.UserApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Helpers {

    private static final String TAG = "Helpers";

    public static String bindAddress(ProxyModel proxyModel) {
        return (Constants.PROXY_SCHEMA + "?server=") + proxyModel.getHost() +
                "&port=" + proxyModel.getPort() +
                "&secret=" + proxyModel.getSecret();
    }

    public static synchronized void getRemoteAddress(final SessionManager sessionManager) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL ip = new URL("https://api.ipify.org/?format=json");
                    BufferedReader in = new BufferedReader(new InputStreamReader(ip.openStream()));
                    final RemoteAddressModel remoteAddressModel = new Gson().fromJson(in.readLine(), RemoteAddressModel.class);
                    sessionManager.saveRemoteAddress(remoteAddressModel.getIp());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void changeRemoteAddress(final SessionManager sessionManager) throws InterruptedException {
        UserApi userApi = new UserApi(sessionManager);
        getRemoteAddress(sessionManager);
        Thread.sleep(400);

        userApi.changeRemoteAddress(sessionManager.getUserRemoteAddress(), new UserApi.OnChangeRemoteAddressListener() {

            @Override
            public void onChange(ResponseModel responseModel) {

            }

            @Override
            public void onError(ErrorModel errorModel) {

            }
        });
    }
}
