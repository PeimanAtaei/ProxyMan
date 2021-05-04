package com.misoke.proxyman.network;



import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.misoke.proxyman.models.AuthModel;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.ResponseModel;
import com.misoke.proxyman.models.UserModel;
import com.misoke.proxyman.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthApi {

    private static final String TAG = "AuthApi";

    public interface OnLoginListener {
        void onLogin(UserModel userModel);
        void onError(ErrorModel errorModel);
    }

    public interface OnRegisterListener {
        void onRegister(UserModel userModel);
        void onError(ErrorModel errorModel);
    }

    public void login(AuthModel authModel, final OnLoginListener onLoginListener) {

        AndroidNetworking.post(Constants.BASE_API + "/users/login")
                .addBodyParameter(authModel)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ResponseModel responseModel = new Gson().fromJson(response.toString(), ResponseModel.class);
                            UserModel userModel = new Gson().fromJson(response.getJSONObject("user").toString(), UserModel.class);

                            if (!responseModel.isError()) {
                                onLoginListener.onLogin(userModel);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ErrorModel errorModel =  new Gson().fromJson(anError.getErrorBody(), ErrorModel.class);
                        onLoginListener.onError(errorModel);
                    }
                });

    }

    public void register(AuthModel authModel, final OnRegisterListener onRegisterListener) {
        AndroidNetworking.post(Constants.BASE_API + "/users/register")
                .addBodyParameter(authModel)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ResponseModel responseModel = new Gson().fromJson(response.toString(), ResponseModel.class);
                            UserModel userModel = new Gson().fromJson(response.getJSONObject("user").toString(), UserModel.class);

                            if (!responseModel.isError()) {
                                onRegisterListener.onRegister(userModel);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ErrorModel errorModel =  new Gson().fromJson(anError.getErrorBody(), ErrorModel.class);
                        onRegisterListener.onError(errorModel);
                    }
                });

    }

    public void refreshToken() {

    }

}