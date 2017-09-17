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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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


public class PlayListClass extends AppCompatActivity {

        String playlistname , usernumber , status;
        TextView playlist_textview;
        ListView listView;
    private ArrayList<String> songname = new ArrayList<>();
    private ArrayList<String> artistname = new ArrayList<>();
    private ArrayList<String> downloadlink = new ArrayList<>();
    PlayListClassAdapter playListClassAdapter;
    Button likeButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_playlist_friend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));


        listView = (ListView)findViewById(R.id.listView);

        playlistname = getIntent().getExtras().getString("PlayListname");
        usernumber = getIntent().getExtras().getString("number");
        status = getIntent().getExtras().getString("status");
        Log.e("Asdad", playlistname + usernumber);


        playlist_textview = (TextView)findViewById(R.id.textView21);
        playlist_textview.setText(playlistname);

        likeButton = (Button)findViewById(R.id.button8);
        if(status.equals("0")){
            likeButton.setText("Like");
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallingLikePlayList(playlistname);
                    CallingLiked_PlayList(playlistname);
                }
            });

        } else if(status.equals("1")){
            likeButton.setText("UnLike");
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CallingunLikePlayList();
                    CallingunLiked_PlayList();
                }
            });

        }


        GettingPlaylist(playlistname , usernumber);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(PlayListClass.this);
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
    private  void CallingunLikePlayList( ){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/unlike.php" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  CreatingProgressDialog("loading");
                        // parseJSON(response);
                        Log.e("res", response);
                        likeButton.setText("Like");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PlayListClass.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""));
                params.put("playlist" , playlistname);
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

    private void CallingunLiked_PlayList(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/unlike_playlist.php" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  CreatingProgressDialog("loading");
                        // parseJSON(response);
                        Log.e("res", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PlayListClass.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""));
                params.put("playlist" , playlistname);
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
    private void CallingLiked_PlayList(final String playlistname){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/liked_playlist.php" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  CreatingProgressDialog("loading");
                        // parseJSON(response);
                        Log.e("res", response);
                        likeButton.setText("Unlike");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PlayListClass.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""));
                params.put("number" , usernumber);
                params.put("playlist" , playlistname);
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



    private void parseJSON(String json){

       songname.clear();
        artistname.clear();
        downloadlink.clear();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray("result");


            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                songname.add(jo.getString("song"));
                artistname.add(jo.getString("artist"));
                 downloadlink.add(jo.getString("download_url"));
            }

               playListClassAdapter = new PlayListClassAdapter(PlayListClass.this , songname ,artistname, downloadlink);
            listView.setAdapter(playListClassAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void CallingLikePlayList(final String playlistname){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/like.php" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  CreatingProgressDialog("loading");
                        // parseJSON(response);
                        Log.e("res", response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PlayListClass.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""));
                params.put("playlist" , playlistname);
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

    private void GettingPlaylist(final String name , final String number){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/fetch_user_playlist_songs.php" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  CreatingProgressDialog("loading");
                        parseJSON(response);
                        Log.e("res", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user",number);
                params.put("playlist" , name);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            startActivity(new Intent(PlayListClass.this, Home_Download_Activity.class));
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
