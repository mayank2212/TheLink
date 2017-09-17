package org.thelink.LoginAndRegister;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import org.thelink.MainActivity;
import org.thelink.R;


    public class VerificationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, VerificationListener {

    private static final String TAG = Verification.class.getSimpleName();
    private Verification mVerification;
    TextView resend_timer;
    View parentLayout ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){

        }
        parentLayout = findViewById(android.R.id.content);
        resend_timer = (TextView) findViewById(R.id.resend_timer);
        resend_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResendCode();
            }
        });
        startTimer();
        enableInputField(true);
        initiateVerification();
    }

    void createVerification(String phoneNumber, boolean skipPermissionCheck, String countryCode) {
        if (!skipPermissionCheck && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0);
            //hideProgressBar();
        } else {
            mVerification = SendOtpVerification.createSmsVerification(this, phoneNumber, this, countryCode, true);
            mVerification.initiate();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "This application needs permission to read your SMS to automatically verify your "
                        + "phone, you may disable the permission once you have been verified.", Toast.LENGTH_LONG)
                        .show();
            }
            enableInputField(true);
        }
        initiateVerificationAndSuppressPermissionCheck();
    }

    void initiateVerification() {
        initiateVerification(false);
    }

    void initiateVerificationAndSuppressPermissionCheck() {
        initiateVerification(true);
    }

    void initiateVerification(boolean skipPermissionCheck) {
        Intent intent = getIntent();
        if (intent != null) {
            String phoneNumber = intent.getStringExtra(MainActivity.INTENT_PHONENUMBER);
            String countryCode = intent.getStringExtra(MainActivity.INTENT_COUNTRY_CODE);
            TextView phoneText = (TextView) findViewById(R.id.numberText);
            phoneText.setText("Verification code send to +" + countryCode + phoneNumber);
            createVerification(phoneNumber, skipPermissionCheck, countryCode);
        }
    }

    public void ResendCode() {
        startTimer();
        initiateVerificationAndSuppressPermissionCheck();
    }

    public void onSubmitClicked(View view) {

        String code = ((EditText) findViewById(R.id.inputCode)).getText().toString();
        if (!code.isEmpty()) {
            if (mVerification != null) {
                mVerification.verify(code);

                Snackbar.make(parentLayout, "Verifying...", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.red))
                        .show();
                enableInputField(false);
            }
        }
    }

    void enableInputField(boolean enable) {
        View container = findViewById(R.id.inputContainer);
        if (enable) {
            container.setVisibility(View.VISIBLE);
            EditText input = (EditText) findViewById(R.id.inputCode);
            input.requestFocus();
        } else {
            container.setVisibility(View.GONE);
        }
        TextView resend_timer = (TextView) findViewById(R.id.resend_timer);
        resend_timer.setClickable(false);
    }

    void hideProgressBarAndShowMessage(int message) {
       // hideProgressBar();
       /* TextView messageText = (TextView) findViewById(R.id.textView);
        messageText.setText(message);*/
    }

    /*void hideProgressBar() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.INVISIBLE);
        TextView progressText = (TextView) findViewById(R.id.progressText);
        progressText.setVisibility(View.INVISIBLE);
    }*/

  /*  void showProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.VISIBLE);
    }

    void showCompleted() {
        ImageView checkMark = (ImageView) findViewById(R.id.checkmarkImage);
        checkMark.setVisibility(View.VISIBLE);
    }
*/
    @Override
    public void onInitiated(String response) {
        Log.d(TAG, "Initialized!" + response);
    }

    @Override
    public void onInitiationFailed(Exception exception) {
        Log.e(TAG, "OTP initialization failed: " + exception.getMessage());
        Toast.makeText(getApplicationContext() , exception.getMessage() , Toast.LENGTH_LONG).show();
        Snackbar.make(parentLayout, "OTP initialization failed:", Snackbar.LENGTH_LONG)
                .setAction("Try Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      /*  Intent intent = new Intent(VerificationActivity.this , MainActivity.class);
                        startActivity(intent);
                        finish();*/
                        startActivity(new Intent(VerificationActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        finish();
                    }
                })
                .setDuration(Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.red))
                .show();
    }

    @Override
    public void onVerified(String response) {
        Log.d(TAG, "Verified!\n" + response);
        Snackbar.make(parentLayout, "Verified!\n", Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
        startActivity(new Intent(VerificationActivity.this, FbAndGoogle.class));
        overridePendingTransition(R.anim.enter_original, R.anim.exit_original);
        finish();
        //showCompleted();
    }

    @Override
    public void onVerificationFailed(Exception exception) {
        Log.e(TAG, "Verification failed: " + exception.getMessage());
        Snackbar.make(parentLayout, "Verification failed", Snackbar.LENGTH_LONG)
                .setAction("Try Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(R.color.red))
                .show();
        enableInputField(true);
    }

    private void startTimer() {
        resend_timer.setClickable(false);
        resend_timer.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.black));
        new CountDownTimer(60000, 1000) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round((float) ms / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) ms / 1000.0f);
                    resend_timer.setText("Resend via call ( " + secondsLeft + " )");
                }
            }

            public void onFinish() {
                resend_timer.setClickable(true);
                resend_timer.setText("Resend via call");
                resend_timer.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.black));
            }
        }.start();
    }

    @Override
    public void onBackPressed() {

    }
}