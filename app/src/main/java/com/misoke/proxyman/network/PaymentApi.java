package com.misoke.proxyman.network;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.Gson;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.PaymentResponseModel;
import com.misoke.proxyman.utils.Constants;
import com.misoke.proxyman.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentApi {

    private SessionManager sessionManager;

    public PaymentApi(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public interface OnBuyListener {
        void onBuy(String url);
        void onError(ErrorModel errorModel);
    }

    public interface OnCheckListener {
        void onPaid(PaymentResponseModel paymentResponseModel);
        void onError(ErrorModel errorModel);
    }

    public interface OnFreeTestListener {
        void onProxy(String url);
        void onError(ErrorModel errorModel);
    }

    public interface OnCheckFreeTestListener {
        void onCheck(boolean freeTest);
        void onError(ErrorModel errorModel);
    }

    public void buy(int plan, int discount, final OnBuyListener onBuyListener) {
        AndroidNetworking.get(Constants.BASE_API + "/payments/buy")
                .addQueryParameter("plan", String.valueOf(plan))
                .addQueryParameter("discount", String.valueOf(discount))
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Bearer " + sessionManager.getUserAccessToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            onBuyListener.onBuy(response.getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        ErrorModel errorModel =  new Gson().fromJson(error.getErrorBody(), ErrorModel.class);
                        onBuyListener.onError(errorModel);
                    }
                });
    }

    public void checkPayment(final OnCheckListener onCheckListener) {
        AndroidNetworking.get(Constants.BASE_API + "/payments/checkPayment")
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Bearer " + sessionManager.getUserAccessToken())
                .build()
                .getAsObject(PaymentResponseModel.class, new ParsedRequestListener<PaymentResponseModel>() {

                    @Override
                    public void onResponse(PaymentResponseModel paymentResponseModel) {
                       onCheckListener.onPaid(paymentResponseModel);
                    }

                    @Override
                    public void onError(ANError error) {
                        ErrorModel errorModel =  new Gson().fromJson(error.getErrorBody(), ErrorModel.class);
                        onCheckListener.onError(errorModel);
                    }
                });
    }
}
