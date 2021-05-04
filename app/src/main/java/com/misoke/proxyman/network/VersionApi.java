package com.misoke.proxyman.network;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.misoke.proxyman.models.AppModel;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.ProxyModel;
import com.misoke.proxyman.models.ResponseModel;
import com.misoke.proxyman.utils.Constants;
import com.misoke.proxyman.utils.FileHelper;
import com.misoke.proxyman.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class VersionApi {

    private SessionManager sessionManager;
    private Context context;

    public VersionApi(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    public interface OnGetLatestVersionListener {
        void onGet(AppModel appModel);
        void onError(ErrorModel errorModel);
    }

    public interface OnDownloadListener {
        void onGet(String path);
        void onError(ErrorModel errorModel);
    }

    public void getLatestVersion(final OnGetLatestVersionListener onGetLatestVersionListener) {
        AndroidNetworking.get(Constants.BASE_API + "/versions/latest")
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Bearer " + sessionManager.getUserAccessToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ResponseModel responseModel = new Gson().fromJson(response.toString(), ResponseModel.class);
                            AppModel appModel = new Gson().fromJson(response.getJSONObject("app").toString(), AppModel.class);

                            if (!responseModel.isError()) {
                                onGetLatestVersionListener.onGet(appModel);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ErrorModel errorModel =  new Gson().fromJson(anError.getErrorBody(), ErrorModel.class);
                        onGetLatestVersionListener.onError(errorModel);
                    }
                });
    }

    public void download(String url, final String dirPath, final String fileName, final OnDownloadListener onDownloadListener) {
        AndroidNetworking.download(url, dirPath, fileName)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {

                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {

                    }
                })
                .startDownload(new DownloadListener() {

                    @Override
                    public void onDownloadComplete() {
                        onDownloadListener.onGet(dirPath + fileName);
                    }

                        @Override
                        public void onError(ANError error) {
                            ErrorModel errorModel =  new Gson().fromJson(error.getErrorBody(), ErrorModel.class);
                            onDownloadListener.onError(errorModel);
                        }
                    });
                }
    }
