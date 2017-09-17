package org.thelink.Home;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thelink.InviteActivity.HomeActivity;
import org.thelink.MusicPlayer.MusicPlayerActivity;
import org.thelink.Profile.Profile_Activity;
import org.thelink.R;
import org.thelink.Service.SongService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home_Download_Activity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    private BottomNavigationBar bottomNavigationBar;

    private static final String YOUTUBE_URL = "http://thelinkweb.com/fetch_youtube_channels.php";
    private static final String FRIENDS_URL = "http://www.thelinkweb.com/following.php";
        private RecyclerView mRecyclerView , friends_recycleview;
    private RecyclerView.LayoutManager mLayoutManager , friends_layoutmanager;
    private RecyclerView.Adapter mAdapter , friends_adapter;
        //Youtube list
    private List<String> name = new ArrayList<>();
    private  List<String> pictures = new ArrayList<>();
        //Friends List
    private List<String> friendsname = new ArrayList<>();
   /* private List<String> song_name = new ArrayList<>();*/
    private List<String> number = new ArrayList<>();
    private List<String> image_url = new ArrayList<>();


    Button morechannel_button;
    boolean doubleBackToExitPressedOnce = false;

    private ImageView trending , tgif , bollywood, edm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__download_);
            //picasso
        initialization();


        Getting_BestofYoutube();
        GettingUserFriends("");

        mLayoutManager = new LinearLayoutManager(
                Home_Download_Activity.this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        friends_layoutmanager = new LinearLayoutManager(Home_Download_Activity.this ,
                LinearLayoutManager.HORIZONTAL ,
                false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        friends_recycleview.setLayoutManager(friends_layoutmanager);



        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar_home_download);
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar
                .setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.home, "Home").setActiveColorResource(R.color.holo_pink_))
                .addItem(new BottomNavigationItem(R.drawable.contacts, "Follow").setActiveColorResource(R.color.holo_pink_))
                .addItem(new BottomNavigationItem(R.drawable.profile, "Profile").setActiveColorResource(R.color.holo_pink_))
                .addItem(new BottomNavigationItem(R.drawable.music_player, "Music").setActiveColorResource(R.color.holo_pink_))
                .setFirstSelectedPosition(0)
                .initialise();


        //onclick listeners

        tgif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Getting_Trending_Songs();
                startActivity(new Intent(Home_Download_Activity.this, TgifSongs.class));
                overridePendingTransition(R.anim.enter_original, R.anim.exit_original);

            }
        });

        trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Download_Activity.this, TrendingSongs.class));
                overridePendingTransition(R.anim.enter_original, R.anim.exit_original);

            }
        });
        bollywood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Download_Activity.this, BollywoodSongs.class));
                overridePendingTransition(R.anim.enter_original, R.anim.exit_original);

            }
        });

        edm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Download_Activity.this, EdmSongs.class));
                overridePendingTransition(R.anim.enter_original, R.anim.exit_original);

            }
        });
        morechannel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  MoreChannelDailog moreChannelDailog = new MoreChannelDailog(Home_Download_Activity.this);

                moreChannelDailog.show_dailog(Home_Download_Activity.this);
            }
        });
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(Home_Download_Activity.this);
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    if(loading.isShowing()){
                        loading.dismiss();
                    }
                    Intent intent1  = new Intent(Home_Download_Activity.this , SongService.class);
                    startService(intent1);
                }
                // you can start service over here
                if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if(loading.isShowing()){
                        loading.dismiss();
                    }
                    Intent intent1  = new Intent(Home_Download_Activity.this , SongService.class);
                    stopService(intent1);
                }
                // stop service over here so it wont consume user data
            }else {
                Intent intent1  = new Intent(Home_Download_Activity.this , SongService.class);
                stopService(intent1);
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
    private void initialization(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_download);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));


        tgif = (ImageView)findViewById(R.id.imageView3);
        edm = (ImageView)findViewById(R.id.imageView4);
        bollywood = (ImageView)findViewById(R.id.imageView);
        trending = (ImageView)findViewById(R.id.imageView5);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        friends_recycleview = (RecyclerView)findViewById(R.id.recycler_view_friends);
        morechannel_button = ( Button)findViewById(R.id.morechannel_button);

    }
    @Override
    public void onTabSelected(int position) {
        SettingTabs(position);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
    public void SettingTabs(int position) {
        switch (position){
            case 0:

                break;
            case 1 :
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
            case 2 :
                startActivity(new Intent(getApplicationContext(), Profile_Activity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
            case 3:
                startActivity(new Intent(getApplicationContext(), MusicPlayerActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
        }
    }

    public void Getting_BestofYoutube(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, YOUTUBE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);

                        parseYoutubeJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Home_Download_Activity.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
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
    private void parseYoutubeJSON(String response){
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");


            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                name.add(jo.getString("channel_name"));
                pictures.add(jo.getString("image_url"));


            }
            TextView bestofyoutube = (TextView)findViewById(R.id.bestofyoutube);
            bestofyoutube.setText("Best of Youtube");
            Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");
            bestofyoutube.setTypeface(type);
            mAdapter = new BestOfYoutubeAdapter(Home_Download_Activity.this,name , pictures);
            mRecyclerView.setAdapter(mAdapter);
           /* AsyncTaskFollow asyncTaskFollow  = new AsyncTaskFollow();
            asyncTaskFollow.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
//
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void GettingUserFriends(final String user){
        // follow_list.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FRIENDS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);

                        parseJSON_Friends(response);


                        Log.e("response ", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Home_Download_Activity.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""));
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

    protected void parseJSON_Friends(String json) {
        JSONObject jsonObject = null;
        friendsname.clear();
        number.clear();
        image_url.clear();
        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray("result");


            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                number.add(jo.getString("number"));
                friendsname.add(jo.getString("name"));
                image_url.add(jo.getString("profile"));

            }

            TextView friends = (TextView)findViewById(R.id.friends);
            friends.setText("Friends");
            Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");
            friends.setTypeface(type);
            friends_adapter = new FriendsAdapter(Home_Download_Activity.this, friendsname , number  , image_url);
            friends_recycleview.setAdapter(friends_adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.download_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
