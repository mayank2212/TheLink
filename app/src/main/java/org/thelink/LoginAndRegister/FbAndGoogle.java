package org.thelink.LoginAndRegister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.thelink.IntroSlider.WelcomeActivity;
import org.thelink.LoginManager_App;
import org.thelink.R;

import java.util.Arrays;

public class FbAndGoogle extends AppCompatActivity  implements  GoogleApiClient.OnConnectionFailedListener  {
    //Signin button
    private SignInButton signInButton;
    LoginManager_App loginManager;
    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;
    RelativeLayout mainlayout , nointernetlayout;
   LoginButton loginButton ;
    CallbackManager   callbackManager ;
      Button googlePlusbutton , facebookbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_fb_and_google);
        try{

            getSupportActionBar().hide();
        }catch (Exception e){}
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        googlePlusbutton = (Button) findViewById(R.id.googleplus);

        facebookbutton = (Button) findViewById(R.id.facebook);
        facebookbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().logInWithReadPermissions(FbAndGoogle.this, Arrays.asList("email", "public_profile"));

//                LoginManager.getInstance().registerCallback(callbackManager,
//                        new FacebookCallback<LoginResult>() {
//                            @Override
//                            public void onSuccess(LoginResult loginResult) {
//
//                                System.out.println("Success");
//                                String userID = loginResult.getAccessToken().getUserId();
//                                String profileurl = "https://graph.facebook.com/" + userID + "/picture?type=large";
//                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
//                                        .putString("AvatarUrl", profileurl).commit();
//                                Log.e("profiel", profileurl) ;
//                                GraphRequest request = GraphRequest.newMeRequest(
//                                        accessToken,
//                                        new GraphRequest.GraphJSONObjectCallback() {
//                                                                 @Override
//                                                                 public void onCompleted(JSONObject json, GraphResponse response) {
//                                                                     if (response.getError() != null) {
//                                                                         // handle error
//                                                                         System.out.println("ERROR");
//                                                                     } else {
//                                                                         System.out.println("Success");
//                                                                         try {
//                                                                             String jsonresult = String.valueOf(json);
//                                                                             System.out.println("JSON Result" + jsonresult);
//                                                                             String name = json.getString("name");
//                                                                             PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
//                                                                                     .putString("Name", name).commit();
//                                                                             Log.e("name", name);
//
///*
//
//                                                        String str_id = json.getString("id");
//                                                        String str_firstname = json.getString("first_name");
//                                                        String str_lastname = json.getString("last_name");
//*/
//
//
//                                                                             //     Log.e("sad", str_firstname + " " + str_id);
//                                                                         } catch (JSONException e) {
//                                                                             e.printStackTrace();
//                                                                         }
//                                                                     }
//                                                                 }
//
//                                                             }).executeAsync();
//
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                Log.d("cancel", "On cancel");
//                            }
//
//                            @Override
//                            public void onError(FacebookException error) {
//                                Log.d("error", error.toString());
//                            }
//                        });
               // GraphRequest request ;
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        //String userID =
                        Profile profile = Profile.getCurrentProfile();
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                        .putString("AvatarUrl", String.valueOf(profile)).commit();
                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.e("LoginActivity", response.toString());

                                        // Application code
                                        if (object != null) {
                                            try {

                                                String name = object.getString("name");
                                                // textView.setText(name.toString());
                                                Log.e("name", name);
                                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                                                                    .putString("Name", name).commit();
//                                    String birthday = object.getString("birthday"); // 01/31/1980 format
                                               String userid = object.getString("id");
                                                String profileurl = "https://graph.facebook.com/" + userid + "/picture?type=large";
                                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                                        .putString("AvatarUrl", profileurl).commit();
                                                Log.e("SDFdsf" , profileurl);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Log.e("noresullt :", "error");
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

            }
        });


        mainlayout = (RelativeLayout)findViewById(R.id.mainfblayout);
        nointernetlayout=(RelativeLayout)findViewById(R.id.nointernetfb);



        //Setting onclick listener to signing button
        googlePlusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                    nointernetlayout.setVisibility(View.GONE);
                mainlayout.setVisibility(View.VISIBLE);}

                // you can start service over here
                if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                    nointernetlayout.setVisibility(View.GONE);
                mainlayout.setVisibility(View.VISIBLE);}

                // stop service over here so it wont consume user data
            }else {
                mainlayout.setVisibility(View.GONE);
                nointernetlayout.setVisibility(View.VISIBLE);
            }

        }
    };
    //This function will option signing intent
    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onactivityResult", "CALLED");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            loginManager = new LoginManager_App(this);
            loginManager.SetLoginStatus(false);
            startActivity(new Intent(FbAndGoogle.this, WelcomeActivity.class));
            overridePendingTransition(R.anim.enter_original, R.anim.exit_original);
            finish();
        }
    }


    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            //Displaying name and email
            Log.e("name", acct.getDisplayName());
            Log.e("email", acct.getEmail());
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                    .putString("Name", acct.getDisplayName()).commit();
            String photoUrl = String.valueOf(acct.getPhotoUrl());
           // Toast.makeText(getApplicationContext(), acct.getPhotoUrl().toString(), Toast.LENGTH_LONG).show();

            if((photoUrl.equals("null")) || photoUrl.equals("")) {
                photoUrl ="http://www.thelinkweb.com/uploads/default.png";
            }
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                    .putString("AvatarUrl", photoUrl).commit();
          //  Toast.makeText(getApplicationContext(), photoUrl, Toast.LENGTH_LONG).show();
            loginManager = new LoginManager_App(this);
            loginManager.SetLoginStatus(false);
           /* Intent intent = new Intent(FbAndGoogle.this,WelcomeActivity.class);
            this.startActivity(intent);
            finish();*/
            startActivity(new Intent(FbAndGoogle.this, WelcomeActivity.class));
            overridePendingTransition(R.anim.enter_original, R.anim.exit_original);
            finish();
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
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
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}