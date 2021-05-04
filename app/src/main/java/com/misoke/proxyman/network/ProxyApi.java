package com.misoke.proxyman.network;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.ProxyModel;
import com.misoke.proxyman.models.ResponseModel;
import com.misoke.proxyman.utils.Constants;
import com.misoke.proxyman.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


public class ProxyApi {

    private SessionManager sessionManager;

    public ProxyApi(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public interface OnGetProxyListener {
        void onGet(List<ProxyModel> proxyModel);
        void onError(ErrorModel errorModel);
    }



    public void getProxies(final OnGetProxyListener onGetProxyListener) {
        AndroidNetworking.get(Constants.BASE_API + "/proxies")
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Bearer " + sessionManager.getUserAccessToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ResponseModel responseModel = new Gson().fromJson(response.toString(), ResponseModel.class);

                            Type listType = new TypeToken<List<ProxyModel>>(){}.getType();

                            List<ProxyModel> proxyModel = new Gson().fromJson(response.getJSONArray("proxies").toString(), listType);

                            if (!responseModel.isError()) {
                                onGetProxyListener.onGet(proxyModel);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ErrorModel errorModel =  new Gson().fromJson(anError.getErrorBody(), ErrorModel.class);
                        onGetProxyListener.onError(errorModel);
                    }
                });
    }
}