package com.misoke.proxyman.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.misoke.proxyman.BaseActivity;
import com.misoke.proxyman.R;
import com.misoke.proxyman.models.ErrorModel;
import com.misoke.proxyman.models.InvitationCodeModel;
import com.misoke.proxyman.models.PaymentResponseModel;
import com.misoke.proxyman.models.ResponseModel;
import com.misoke.proxyman.network.AgentApi;
import com.misoke.proxyman.network.PaymentApi;
import com.misoke.proxyman.utils.SessionManager;


public class PaymentActivity extends BaseActivity {


    private static final String TAG = "PaymentActivity";


    private Toolbar toolbar;
    private AppCompatButton paymentSilverButton;
    private AppCompatButton paymentGoldButton;

    private SessionManager sessionManager;
    private PaymentApi paymentApi;

    private AlertDialog alertDialog;

    private String currentUrl;


    @Override
    protected void onStart() {
        super.onStart();
        Uri data = getIntent().getData();
        if (data != null) {
            checkPayment();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initialize();
        setupViews();
        listeners();
    }

    private void initialize() {
        sessionManager = new SessionManager(this);
        paymentApi = new PaymentApi(sessionManager);
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        paymentSilverButton = findViewById(R.id.payment_silver_button);
        paymentGoldButton = findViewById(R.id.payment_gold_button);

        TextView mTitle =  toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTextColor(0xFFFFFFFF);
        mTitle.setText(getString(R.string.toolbar_payment));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void openDialogInvitationCode() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_invitation_code, viewGroup, false);

        final EditText invitationCodeEditText = dialogView.findViewById(R.id.invitationCodeEditText);
        Button saveInvitationCodeButton = dialogView.findViewById(R.id.saveInvitationCodeButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);


        saveInvitationCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String invitationCode = invitationCodeEditText.getText().toString();
                if (!TextUtils.isEmpty(invitationCode)) {
                    hideKeyboard(invitationCodeEditText);
                    checkInvitationCodeFromServer(invitationCode);
                } else {
                    goToPay(0);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void hideKeyboard(EditText myEditText) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
        }
    }

    private boolean checkInvitationCode() {
        String invitationCode = sessionManager.getUserInvitationCode();
        return !invitationCode.contains(SessionManager.STRING_PREF_UNAVAILABLE) && !invitationCode.isEmpty();
    }

    private void checkInvitationCodeFromServer(final String invitationCode) {
        final ProgressDialog progressInvitationCode = new ProgressDialog(this);
        progressInvitationCode.setMessage(getString(R.string.payment_connection_description));
        progressInvitationCode.setCanceledOnTouchOutside(false);
        progressInvitationCode.show();

        AgentApi agentApi = new AgentApi(sessionManager);
        InvitationCodeModel invitationCodeModel = new InvitationCodeModel();
        invitationCodeModel.setUserId(sessionManager.getUserId());
        invitationCodeModel.setInvitationCode(invitationCode);

        agentApi.checkInvitationCode(invitationCodeModel, new AgentApi.OnCheckInvitationCode() {

            @Override
            public void onCheck(ResponseModel responseModel) {
                if (!responseModel.isError()) {
                    goToPay(1);
                    progressInvitationCode.dismiss();
                    sessionManager.setUserInvitationCode(invitationCode);
                } else {
                    progressInvitationCode.dismiss();
                    Toast.makeText(PaymentActivity.this, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ErrorModel errorModel) {
                progressInvitationCode.dismiss();
                Toast.makeText(PaymentActivity.this, errorModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listeners() {
        paymentSilverButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkInvitationCode()) {
                    sessionManager.setPlan(1);
                    goToPay(1);
                } else {
                    openDialogInvitationCode();
                }
            }
        });

        paymentGoldButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkInvitationCode()) {
                    sessionManager.setPlan(3);
                    goToPay(1);
                } else {
                    openDialogInvitationCode();
                }
            }
        });


    }


    private void openProxy(final String url) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.check_out_dialog, viewGroup, false);

        Button btnConnected = dialogView.findViewById(R.id.btnConnected);

        btnConnected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                try {
                    startActivity(intent);
                } catch(ActivityNotFoundException e){
                    Toast.makeText(PaymentActivity.this, "Auww!! No Activity can open this URI!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void goToPay(int discount) {
        final ProgressDialog progressPay = new ProgressDialog(this);
        progressPay.setMessage(getString(R.string.payment_connection_description));
        progressPay.setCanceledOnTouchOutside(false);
        progressPay.show();

        paymentApi.buy(sessionManager.getPlan(), discount, new PaymentApi.OnBuyListener() {

            @Override
            public void onBuy(String url) {
                progressPay.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }

            @Override
            public void onError(ErrorModel errorModel) {
                Toast.makeText(PaymentActivity.this, "" + errorModel.getMessage(), Toast.LENGTH_SHORT).show();
                progressPay.dismiss();
            }
        });
    }


    private void checkPayment() {
        paymentApi.checkPayment(new PaymentApi.OnCheckListener() {

            @Override
            public void onPaid(PaymentResponseModel paymentResponseModel) {
                if (paymentResponseModel.isPaid()) {
                    Toast.makeText(PaymentActivity.this, paymentResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                    sessionManager.setPayment(true);
                    Intent mainIntent = new Intent(PaymentActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, paymentResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                    sessionManager.setPayment(false);
                }
            }

            @Override
            public void onError(ErrorModel errorModel) {
                Toast.makeText(PaymentActivity.this, errorModel.getMessage(), Toast.LENGTH_SHORT).show();
                sessionManager.setPayment(false);
            }
        });
    }
}
