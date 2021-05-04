package com.misoke.proxyman.network;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.ResponseModel;
import com.misoke.proxyman.models.TimeResponseModel;
import com.misoke.proxyman.utils.Constants;
import com.misoke.proxyman.utils.SessionManager;

import org.json.JSONObject;

public class UserApi {

    private SessionManager sessionManager;

    public UserApi(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public interface OnChangeRemoteAddressListener {
        void onChange(ResponseModel responseModel);
        void onError(ErrorModel errorModel);
    }

    public interface OnGetTimePaymentListener {
        void onGet(TimeResponseModel timeResponseModel);
        void onError(ErrorModel errorModel);
    }

    public interface OnSaveProxyListener {
        void onSave(ResponseModel responseModel);
        void onError(ErrorModel errorModel);
    }

    public void changeRemoteAddress(String remoteAddress, final OnChangeRemoteAddressListener onChangeRemoteAddressListener) {
        AndroidNetworking.post(Constants.BASE_API + "/users/changeRemoteAddress")
                .addBodyParameter("remoteAddress", remoteAddress)
                .setTag(this)
                .addHeaders("Authorization", "Bearer " + sessionManager.getUserAccessToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ResponseModel responseModel = new Gson().fromJson(response.toString(), ResponseModel.class);

                        if (!responseModel.isError()) {
                            onChangeRemoteAddressListener.onChange(responseModel);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        ErrorModel errorModel =  new Gson().fromJson(error.getErrorBody(), ErrorModel.class);
                        onChangeRemoteAddressListener.onError(errorModel);
                    }
                });
    }


    public void getTimePayment(final OnGetTimePaymentListener onGetTimePaymentListener) {
        AndroidNetworking.get(Constants.BASE_API + "/users/getTimePayment")
                .setTag(this)
                .addHeaders("Authorization", "Bearer " + sessionManager.getUserAccessToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        TimeResponseModel timeResponseModel = new Gson().fromJson(response.toString(), TimeResponseModel.class);
                        onGetTimePaymentListener.onGet(timeResponseModel);
                    }

                    @Override
                    public void onError(ANError error) {
                        ErrorModel errorModel =  new Gson().fromJson(error.getErrorBody(), ErrorModel.class);
                        onGetTimePaymentListener.onError(errorModel);
                    }
                });
    }

    public void saveProxy(String proxyId, final OnSaveProxyListener onSaveProxyListener) {
        AndroidNetworking.post(Constants.BASE_API + "/users/saveProxy")
                .addBodyParameter("proxy", proxyId)
                .setTag(this)
                .addHeaders("Authorization", "Bearer " + sessionManager.getUserAccessToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ResponseModel responseModel = new Gson().fromJson(response.toString(), ResponseModel.class);

                        if (!responseModel.isError()) {
                            onSaveProxyListener.onSave(responseModel);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        ErrorModel errorModel =  new Gson().fromJson(error.getErrorBody(), ErrorModel.class);
                        onSaveProxyListener.onError(errorModel);
                    }
                });
    }

}
