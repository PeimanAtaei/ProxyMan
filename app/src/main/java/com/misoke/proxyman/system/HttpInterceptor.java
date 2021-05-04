package com.misoke.proxyman.system;


import android.content.Context;

import com.misoke.proxyman.utils.SessionManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class HttpInterceptor implements Authenticator {

    private static final String TAG = "HttpInterceptor";

    private SessionManager sessionManager;
    private Context context;

    public HttpInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (sessionManager.isAuthorized()) {

        } else {
            return null;
        }
        return null;
    }
}

//https://stackoverflow.com/questions/45031620/okhttp-refresh-expired-token-when-multiple-requests-are-sent-to-the-server/45081473