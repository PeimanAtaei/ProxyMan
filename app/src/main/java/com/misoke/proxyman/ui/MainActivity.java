package com.misoke.proxyman.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.misoke.proxyman.BaseActivity;
import com.misoke.proxyman.models.AppModel;
import com.misoke.proxyman.models.ResponseModel;
import com.misoke.proxyman.network.UserApi;
import com.misoke.proxyman.network.VersionApi;
import com.misoke.proxyman.system.NetworkService;
import com.misoke.proxyman.R;
import com.misoke.proxyman.adapter.ProxyListAdapter;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.ProxyModel;
import com.misoke.proxyman.network.ProxyApi;
import com.misoke.proxyman.utils.Constants;
import com.misoke.proxyman.utils.Helpers;
import com.misoke.proxyman.utils.SessionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final String DIR_APP = "/proxyman/";

    private SessionManager sessionManager;
    private ProxyApi proxyApi;
    private UserApi userApi;
    private VersionApi versionApi;

    private RecyclerView proxyListView;
    private List<ProxyModel> proxyList;
    private ProxyListAdapter proxyListAdapter;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private SwipeRefreshLayout pullToRefresh;
    private FloatingActionButton helpBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();



        if (!sessionManager.isAuthorized()) {
            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
            finish();
            return;
        }

        if (!sessionManager.isPayment()) {
            Intent paymentIntent = new Intent(MainActivity.this, PaymentActivity.class);
            startActivity(paymentIntent);
            finish();
            return;
        }


        setupViews();
        getLatestApp();
        getProxies();
        listeners();
        networkService();
    }


    private void initialize() {
        sessionManager = new SessionManager(this);
        proxyApi = new ProxyApi(sessionManager);
        userApi = new UserApi(sessionManager);
        versionApi = new VersionApi(sessionManager);
        proxyList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
    }

    private void setupViews() {
        proxyListView = findViewById(R.id.proxyListView);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        toolbar = findViewById(R.id.toolbar);
        helpBtn = findViewById(R.id.helpBtn);

        proxyListAdapter = new ProxyListAdapter(proxyList);
        proxyListView.setLayoutManager(new LinearLayoutManager(this));
        proxyListView.setAdapter(proxyListAdapter);

        TextView mTitle =  toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTextColor(0xFFFFFFFF);
        mTitle.setText(getString(R.string.toolbar_main));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void listeners() {
        proxyListAdapter.setOnClickListener(new ProxyListAdapter.OnClickListener() {

            @Override
            public void onClick(ProxyModel proxyModel) {
                saveProxy(proxyModel.getId());
                String url = Helpers.bindAddress(proxyModel);
                openProxy(url);
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                proxyList.clear();
                getProxies();
                proxyListAdapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HelpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveProxy(String proxyId) {
        userApi.saveProxy(proxyId, new UserApi.OnSaveProxyListener() {

            @Override
            public void onSave(ResponseModel responseModel) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.proxy_open_message), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ErrorModel errorModel) {
                Toast.makeText(MainActivity.this, errorModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openProxy(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        try {
            startActivity(intent);
        } catch(ActivityNotFoundException e){
            Toast.makeText(MainActivity.this, getResources().getString(R.string.telegram_not_installed), Toast.LENGTH_SHORT).show();
        }
    }

    private void getProxies() {
        progressDialog.setMessage("در حال دریافت پراکسی");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        proxyApi.getProxies(new ProxyApi.OnGetProxyListener() {

            @Override
            public void onGet(List<ProxyModel> proxyModel) {
                progressDialog.dismiss();
                pullToRefresh.setRefreshing(false);
                proxyList.addAll(proxyModel);
                proxyListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(ErrorModel errorModel) {
                Toast.makeText(MainActivity.this, "StatusCode: " + errorModel.getStatusCode() + "Message: " + errorModel.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void getLatestApp() {
        // Request api and get latest version app -> /apps/getLatestVersion
        // Pass url to downloadManager
        // After show dialog
        // Click button and install app

        if (sessionManager.isAuthorized()) {
            versionApi.getLatestVersion(new VersionApi.OnGetLatestVersionListener() {

                @Override
                public void onGet(AppModel appModel) {
                   if (appModel.isStatus()) {
                       String fileName = Constants.APP_NAME + appModel.getVersionName() + Constants.APP_EXT;
                       download(Constants.BASE_URL + appModel.getUrl(), fileName);
                   }
                }

                @Override
                public void onError(ErrorModel errorModel) {
                    Toast.makeText(MainActivity.this, errorModel.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void download(String url, String fileName) {

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + DIR_APP;

        versionApi.download(url, dirPath, fileName, new VersionApi.OnDownloadListener() {

            @Override
            public void onGet(String path) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(ErrorModel errorModel) {
                Toast.makeText(MainActivity.this, errorModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void networkService() {
        Intent intent = new Intent(MainActivity.this, NetworkService.class);
        startService(intent);
    }
}
