package com.misoke.proxyman.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.misoke.proxyman.R;
import com.misoke.proxyman.models.UserModel;

/**
 * for better management of preference in application
 * like authentication information
 */
public class SessionManager {

    private SharedPreferences mPreference;
    private Context mContext;
    public static final String STRING_PREF_UNAVAILABLE = "string preference unavailable";

    public SessionManager(Context context) {
        this.mContext = context;
        this.mPreference = this.mContext.getSharedPreferences("proxyman_preference", Context.MODE_PRIVATE);
    }


    public void saveUserModel(UserModel userModel) {
        mPreference.edit()
                .putString(this.mContext.getString(R.string.pref_user_id), userModel.getId())
                .putString(this.mContext.getString(R.string.pref_user_phoneNumber), userModel.getPhoneNumber())
                .putString(this.mContext.getString(R.string.pref_user_token), userModel.getToken())
                .putString(this.mContext.getString(R.string.pref_user_invitation_code), userModel.getInvitationCode())
                .putString(this.mContext.getString(R.string.pref_user_created_at), userModel.getCreatedAt())
                .putString(this.mContext.getString(R.string.pref_user_updated_at), userModel.getUpdatedAt())
                .putString(this.mContext.getString(R.string.pref_user_vip_type), userModel.getVipType())
                .apply();
    }

    public void saveRemoteAddress(String remoteAddress) {
        mPreference.edit()
                .putString(this.mContext.getString(R.string.pref_user_remoteAddress), remoteAddress)
                .apply();
    }

    public void setPayment(boolean payment) {
        mPreference.edit()
                .putBoolean(this.mContext.getString(R.string.pref_user_payment), payment)
                .apply();
    }

    public void setFreeTest(boolean freeTest) {
        mPreference.edit()
                .putBoolean(this.mContext.getString(R.string.pref_user_free_test), freeTest)
                .apply();
    }

    public void setUserInvitationCode(String invitationCode) {
        mPreference.edit()
                .putString(this.mContext.getString(R.string.pref_user_invitation_code), invitationCode)
                .apply();
    }

    public void setPlan(int plan) {
        mPreference.edit()
                .putInt(this.mContext.getString(R.string.pref_plan), plan)
                .apply();
    }


    /**
     * get access token
     *
     * @return
     */
    public String getUserAccessToken() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_token), STRING_PREF_UNAVAILABLE);
    }

    public String getUserId() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_id), STRING_PREF_UNAVAILABLE);
    }

    public String getUserPhoneNumber() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_phoneNumber), STRING_PREF_UNAVAILABLE);
    }

    public String getUserRemoteAddress() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_remoteAddress), STRING_PREF_UNAVAILABLE);
    }

    public String getUserInvitationCode() {
        return mPreference.getString(this.mContext.getString(R.string.pref_user_invitation_code), STRING_PREF_UNAVAILABLE);
    }

    public int getPlan() {
        return mPreference.getInt(this.mContext.getString(R.string.pref_plan), 0);
    }


    /**
     * detect is user sign in
     *
     * @return
     */
    public boolean isAuthorized() {
        return !getUserAccessToken().equals(STRING_PREF_UNAVAILABLE);
    }

    public boolean isPayment() {
        return mPreference.getBoolean(this.mContext.getString(R.string.pref_user_payment), false);
    }

    public boolean isFreeTest() {
        return mPreference.getBoolean(this.mContext.getString(R.string.pref_user_free_test), false);
    }

    /**
     * remove all prefs in logout
     */
    public void removeAllPrefs() {
        mPreference.edit().clear().apply();
    }
}
