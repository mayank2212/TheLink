package org.thelink.Profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.thelink.R;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {
    public static final String FEEDBACK_URL = "http://www.thelinkweb.com/feedback.php";
    Button submit ;
    EditText feedback_edittext ;
    RelativeLayout mainlayout , nointernet ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        try{
            getSupportActionBar().hide();
           }catch (Exception e){

        }

        mainlayout = (RelativeLayout) findViewById(R.id.mainlayoutfeedback) ;
        nointernet = (RelativeLayout) findViewById(R.id.nointernetfeedback);


       /* this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));*/
         submit = ( Button) findViewById(R.id.submit);
        feedback_edittext = (EditText) findViewById(R.id.feedback);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(feedback_edittext.getText().toString().length() > 0)) {
                    Toast.makeText(getApplicationContext(), "Please enter your feedback first", Toast.LENGTH_LONG).show();
                } else {

                    SendFeedBack(PreferenceManager.getDefaultSharedPreferences
                            (getApplicationContext()).getString("Name", "Your Name"), PreferenceManager.getDefaultSharedPreferences
                            (getApplicationContext()).getString("Number", "Your Number"), feedback_edittext.getText().toString());
                }
            }
        });
    }


    private void SendFeedBack(final String name, final String number,final String feedback ) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, FEEDBACK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",name);
                params.put("number",number);
                params.put("feedback",feedback);

                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }

}
