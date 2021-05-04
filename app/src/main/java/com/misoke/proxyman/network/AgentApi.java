package com.misoke.proxyman.network;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.InvitationCodeModel;
import com.misoke.proxyman.models.ResponseModel;
import com.misoke.proxyman.utils.Constants;
import com.misoke.proxyman.utils.SessionManager;

import org.json.JSONObject;

public class AgentApi {

    private SessionManager sessionManager;

    public AgentApi(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public interface OnCheckInvitationCode {
        void onCheck(ResponseModel responseModel);
        void onError(ErrorModel errorModel);
    }

    public void checkInvitationCode(InvitationCodeModel invitationCodeModel, final AgentApi.OnCheckInvitationCode onCheckInvitationCode) {

        AndroidNetworking.post(Constants.BASE_API + "/agents/checkInvitationCode")
                .addBodyParameter(invitationCodeModel)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Bearer " + sessionManager.getUserAccessToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ResponseModel responseModel = new Gson().fromJson(response.toString(), ResponseModel.class);
                        onCheckInvitationCode.onCheck(responseModel);
                    }

                    @Override
                    public void onError(ANError anError) {
                        ErrorModel errorModel =  new Gson().fromJson(anError.getErrorBody(), ErrorModel.class);
                        onCheckInvitationCode.onError(errorModel);
                    }
                });

    }
}
