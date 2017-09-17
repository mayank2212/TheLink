package org.thelink;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msg91.sendotp.library.PhoneNumberFormattingTextWatcher;
import com.msg91.sendotp.library.PhoneNumberUtils;

import org.thelink.LoginAndRegister.VerificationActivity;
import org.thelink.Service.SongService;


/**
 *   no internt layouot
 * internet checks
 * service checking
 * removing toast and logs
 * retrying afteer no internt or timeout
 *
 *
 */


public class MainActivity extends AppCompatActivity {

    public static final String INTENT_PHONENUMBER = "phonenumber";
    public static final String INTENT_COUNTRY_CODE = "code";

    private EditText mPhoneNumber;
    private ImageButton mSmsButton;
    private String mCountryIso;
    private TextWatcher mNumberTextWatcher;
    RelativeLayout mainlayout , nointernet ;
    CheckBox checkBox ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        setContentView(R.layout.activity_main);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        TextView textView = (TextView)findViewById(R.id.textView11);

        assert textView != null;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.thelinkweb.com/termsandcondition.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        try {
            getSupportActionBar().hide();

        } catch (Exception e){

        }
        mainlayout = (RelativeLayout)findViewById(R.id.mainlayoutnumber);
        nointernet = (RelativeLayout) findViewById(R.id.nointernetnumber);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
       /* Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);*/
       mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        mSmsButton = (ImageButton) findViewById(R.id.smsVerificationButton);

        mCountryIso  = "IN";

                    resetNumberTextWatcher(mCountryIso);
                    // force update:
                    mNumberTextWatcher.afterTextChanged(mPhoneNumber.getText());
                    Log.e("country code" , mPhoneNumber.getText().toString());
                /*}*/
          /*  }
        });*/
        resetNumberTextWatcher(mCountryIso);

        tryAndPrefillPhoneNumber();
    }
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    nointernet.setVisibility(View.GONE);
                    mainlayout.setVisibility(View.VISIBLE);
                    Intent intent1 = new Intent(MainActivity.this  , SongService.class);
                    startService(intent1);
                }
                // you can start service over here
                if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    nointernet.setVisibility(View.GONE);
                    mainlayout.setVisibility(View.VISIBLE);
                }
                // stop service over here so it wont consume user data
            }else {
                mainlayout.setVisibility(View.GONE);
                nointernet.setVisibility(View.VISIBLE);
            }

        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mConnReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mConnReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
    }

    private void tryAndPrefillPhoneNumber() {
        if (checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumber.setText(manager.getLine1Number());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tryAndPrefillPhoneNumber();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "This application needs permission to read your phone number to automatically "
                        + "pre-fill it", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openActivity(String phoneNumber) {

        assert checkBox != null;
        if(checkBox.isChecked()) {
            Intent verification = new Intent(this, VerificationActivity.class);
            verification.putExtra(INTENT_PHONENUMBER, phoneNumber);
            verification.putExtra(INTENT_COUNTRY_CODE, "91");

            String prefenumber = phoneNumber.replaceAll("\\s", "");
            prefenumber = prefenumber.replaceAll("-", "");
            Log.e("number", prefenumber);
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("Number", prefenumber).commit();
            startActivity(verification);
            finish();
        } else {
            Toast.makeText(MainActivity.this, "Please agree to Terms & Condition", Toast.LENGTH_LONG).show();
        }

    }

    private void setButtonsEnabled(boolean enabled) {
        mSmsButton.setEnabled(enabled);
    }

    public void onButtonClicked(View view) {
        openActivity(getE164Number());
    }

    private void resetNumberTextWatcher(String countryIso) {

        if (mNumberTextWatcher != null) {
            mPhoneNumber.removeTextChangedListener(mNumberTextWatcher);
        }

        mNumberTextWatcher = new PhoneNumberFormattingTextWatcher(countryIso) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                super.beforeTextChanged(s, start, count, after);
            }

            @Override
            public synchronized void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (isPossiblePhoneNumber()) {
                    setButtonsEnabled(true);
                    mPhoneNumber.setTextColor(Color.BLACK);
                } else {
                    setButtonsEnabled(false);
                    mPhoneNumber.setTextColor(Color.RED);
                }
            }
        };

        mPhoneNumber.addTextChangedListener(mNumberTextWatcher);
    }

    private boolean isPossiblePhoneNumber() {
        return PhoneNumberUtils.isPossibleNumber(mPhoneNumber.getText().toString(), mCountryIso);
    }

    private String getE164Number() {
        Log.e("phone no ", mPhoneNumber.getText().toString());
        return mPhoneNumber.getText().toString().replaceAll("\\D", "").trim();
        // return PhoneNumberUtils.formatNumberToE164(mPhoneNumber.getText().toString(), mCountryIso);
    }
}
