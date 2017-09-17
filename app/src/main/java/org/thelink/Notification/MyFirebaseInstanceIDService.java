package org.thelink.Notification;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Mayank on 30-07-2016.
 */


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String REGISTER_URL="http://www.thelinkweb.com/update.php";     //here comes the URL for the server;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token= FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("token", token).commit();
        Log.e("token"  , token) ;
    }

    private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                      Log.i("notification " , response);
                      //  Toast.makeText(MyFirebaseInstanceIDService.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyFirebaseInstanceIDService.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Name" , "defaultStringIfNothingFound"));
                params.put("number",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", "defaultStringIfNothingFound"));
                params.put("token",token);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}