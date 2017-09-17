package org.thelink.Home;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thelink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsDetail extends AppCompatActivity {

    public static final  String PLAYLIST_SINGLE_USER = "http://thelinkweb.com/fetch_user_playlist.php";
    private List<String> playlistname , likes , likedPlaylistname , status;
    private RecyclerView.LayoutManager mLayoutManager ;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter ;
    String number ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_friend_detail);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));

        number =  getIntent().getExtras().getString("number");
        String name =  getIntent().getExtras().getString("name");
        String profile_picture =  getIntent().getExtras().getString("profilepicture");


        GettingPlayListFromTheServer(number);


        //top relative layout
        ImageView imageView = (ImageView)findViewById(R.id.friend_detail_image);
        Picasso.with(FriendsDetail.this).load(profile_picture).into(imageView);
        TextView name_textview = (TextView)findViewById(R.id.textView15);
        name_textview.setText(name);
        TextView number_textview = (TextView)findViewById(R.id.textView16);
        number_textview.setText(number);



        //recycle view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view_friend_songs);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(FriendsDetail.this);
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
     private void GettingPlayListFromTheServer(final String user){
         StringRequest stringRequest = new StringRequest(Request.Method.POST, PLAYLIST_SINGLE_USER ,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         //  CreatingProgressDialog("loading");
                         parseJSON(response);
                         Log.e("ressssssssss", response);
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(FriendsDetail.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                     }
                 }){
             @Override
             protected Map<String,String> getParams(){
                 Map<String,String> params = new HashMap<String, String>();
                 params.put("user",user);
                 params.put("number",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""));

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
        try {
            jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");


            playlistname = new ArrayList<>();
            likes = new ArrayList<>();
            status= new ArrayList<>();

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                playlistname.add(jo.getString("playlist"));
                likes.add(jo.getString("thumbs_up"));
                status.add(jo.getString("status"));

              //  phonepaths.add(jo.getString("phonepath"));
            }

            mAdapter = new FriendsDetail_Adapter(FriendsDetail.this,playlistname , likes ,
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", "") , status );
            mRecyclerView.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            startActivity(new Intent(FriendsDetail.this, Home_Download_Activity.class));
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
