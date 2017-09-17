package org.thelink.InviteActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thelink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendRequest extends AppCompatActivity {
    private ListView listView;
    private Button button;
   ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> picturelist = new ArrayList<>();
    ArrayList<String> numberlist = new ArrayList<>();

 FriendRequestAdapter friendRequestAdapter;
    TextView emptytextview ;
    RelativeLayout mainlayout , nointernet ;


    public FriendRequest(){}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_friendrequest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));

        GettingFriendRequest(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""));

        emptytextview = (TextView) findViewById(R.id.textView2);

         listView=(ListView)findViewById(R.id.listView_friend);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(FriendRequest.this);
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    if(loading.isShowing()){
                        loading.dismiss();
                    }
                }
                // you can start service over here
                if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if(loading.isShowing()){
                        loading.dismiss();
                    }
                }
                // stop service over here so it wont consume user data
            }else {

                loading.setCancelable(false);
                loading.setMessage("No Internet... Please check your internet connection..");
                loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                loading.show();
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

    public void GettingFriendRequest(final String usernumber){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/friendrequest.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);
                        parseJson(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FriendRequest.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user" , usernumber);
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


    private void parseJson(String json){

        namelist.clear();
        numberlist.clear();
        picturelist.clear();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(String.valueOf(json));
            JSONArray result = jsonObject.getJSONArray("result");


            Log.e("result" , json);
            for (int i = 0; i < result.length(); i++) {
                Log.e("SAdsadasd", result.length() + " ");

                JSONObject jo = result.getJSONObject(i);

                numberlist.add(jo.getString("number"));
                picturelist.add(jo.getString("profile"));
                namelist.add(jo.getString("name"));
            }
                  friendRequestAdapter = new FriendRequestAdapter(FriendRequest.this ,numberlist , picturelist , namelist );
                  listView.setAdapter(friendRequestAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               /* Intent intent = new Intent(FriendRequest.this , Follow_Activity.class);
                startActivity(intent);
                finish();*/
                startActivity(new Intent(FriendRequest.this, HomeActivity.class));
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }
}

