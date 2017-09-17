package org.thelink.Home;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import java.util.List;
import java.util.Map;

public class BollywoodSongs extends AppCompatActivity {

    private static final String TRENDING_SONGS = "http://thelinkweb.com/fetch_trending.php";
    private RecyclerView recyclerView;
    private List<String> name = new ArrayList<>();
    private  List<String> pictures = new ArrayList<>();
    private  List<String> artist_name = new ArrayList<>();
    private  List<String> download_link = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager ;
    private RecyclerView.Adapter mAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bollywood_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bollywood);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setTitle("Bollywood Songs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));


        recyclerView = (RecyclerView)findViewById(R.id.recycle_view_bollywood);
        Getting_Bollywood_songs();
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(BollywoodSongs.this);
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

    public void Getting_Bollywood_songs(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, TRENDING_SONGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        parseJSON(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BollywoodSongs.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("type" , "Hindi");
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
    private void parseJSON(String response){
        JSONObject jsonObject = null;
        name.clear();
        pictures.clear();
        artist_name.clear();
        download_link.clear();
        try {
            jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");


            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                name.add(jo.getString("song_name"));
                pictures.add(jo.getString("image_url"));
                artist_name.add(jo.getString("artist"));
                download_link.add(jo.getString("download_url"));

            }
            Log.e("name" , name.toString());

            mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new FourImageViewAdapter(BollywoodSongs.this,name , artist_name ,  pictures , download_link);
            recyclerView.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            startActivity(new Intent(BollywoodSongs.this, Home_Download_Activity.class));
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();
            return true;


        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }
}
