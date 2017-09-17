package org.thelink.Profile;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
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
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thelink.Home.Home_Download_Activity;
import org.thelink.InviteActivity.HomeActivity;
import org.thelink.MusicPlayer.MusicPlayerActivity;
import org.thelink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class Profile_Activity extends AppCompatActivity implements   BottomNavigationBar.OnTabSelectedListener {

    BottomNavigationBar bottomNavigationBar;
    private static final String Followerno = "http://thelinkweb.com/followerno.php";
    private static final String Followingno = "http://thelinkweb.com/followno.php";
    private static final String PlaylistUrl = "http://thelinkweb.com/fetch_user_playlist.php";
    //follower click
    TextView Follower , Follower_number;
    //following click
    TextView Following, Following_number;

    boolean doubleBackToExitPressedOnce = false;
    TextView Name_textview , Number_textview , following , followers , howtouse , feedback;
    ImageView profile_image , blurImage;

    ListView playlistlistview;
    ArrayList<String> thumbs_up =  new ArrayList<>();
    ArrayList<String> playlistname =  new ArrayList<>();

    private PlayListAdapter playListAdapter;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        //   String countryCodeValue = "us";
        if (!countryCodeValue.equals("in")) {
            setContentView(R.layout.outsideindialayout);
            Button gobackbutton = (Button) findViewById(R.id.gobackbutton);
            gobackbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Home_Download_Activity.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            });

        } else {
            setContentView(R.layout.activity_profile_);
            blurImage = (ImageView) findViewById(R.id.blur_imageview);
            profile_image = (ImageView) findViewById(R.id.user_profile_photo);
            Name_textview = (TextView) findViewById(R.id.user_profile_name);
            Number_textview = (TextView) findViewById(R.id.user_profile_short_bio);
            playlistlistview = (ListView) findViewById(R.id.listprofile);
            floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_profileactivity);

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Profile_Activity.this, CreatePlayListActivity.class));
                    overridePendingTransition(R.anim.enter_original, R.anim.exit_original);

                }
            });

            //listener to hide fab on scroll
            playlistlistview.setOnScrollListener(new AbsListView.OnScrollListener() {
                private int mLastFirstVisibleItem;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    if (mLastFirstVisibleItem < firstVisibleItem) {
                        floatingActionButton.hide();
                    }
                    if (mLastFirstVisibleItem > firstVisibleItem) {
                        floatingActionButton.show();
                    }
                    mLastFirstVisibleItem = firstVisibleItem;

                }
            });


            String name = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Name", "");
            Name_textview.setText(name);

            String number = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", "");
            Number_textview.setText(number);

            GettingPlaylist(number);

            //followers
            GettingFollowerNumber(number);
            Follower = (TextView) findViewById(R.id.followername);
            Follower_number = (TextView) findViewById(R.id.followno);
            Follower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(Profile_Activity.this, Followers.class));
                    overridePendingTransition(R.anim.enter_original, R.anim.exit_original);
                }
            });
            Follower_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Profile_Activity.this, Followers.class));
                    overridePendingTransition(R.anim.enter_original, R.anim.exit_original);

                }
            });


            //following
            GettingFollowingNumber(number);
            Following = (TextView) findViewById(R.id.followingname);
            Following_number = (TextView) findViewById(R.id.followingno);
            Following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "followinggg" , Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), FollowingClass.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            });
            Following_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Toast.makeText(getApplicationContext(), "followinggg" , Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), FollowingClass.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            });


            String defaulturl = "http://www.thelinkweb.com/uploads/default.png";
            String url = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AvatarUrl", defaulturl);
            Picasso.with(getApplicationContext())
                    .load(url)
                    .into(profile_image);
            Picasso.with(Profile_Activity.this).load(url)
                    .transform(new BlurTransformation(this, 8, 10)).centerCrop().fit().into(blurImage);


            bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar_profile);
            bottomNavigationBar.setTabSelectedListener(this);
            bottomNavigationBar
                    .setMode(BottomNavigationBar.MODE_FIXED);
            bottomNavigationBar
                    .addItem(new BottomNavigationItem(R.drawable.home, "Home").setActiveColorResource(R.color.holo_pink_))
                    .addItem(new BottomNavigationItem(R.drawable.contacts, "Follow").setActiveColorResource(R.color.holo_pink_))
                    .addItem(new BottomNavigationItem(R.drawable.profile, "Profile").setActiveColorResource(R.color.holo_pink_))
                    .addItem(new BottomNavigationItem(R.drawable.music_player, "Music").setActiveColorResource(R.color.holo_pink_))
                    .setFirstSelectedPosition(2)
                    .initialise();
        }
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(Profile_Activity.this);
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

        public  void GettingPlaylist(final String user_number ){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, PlaylistUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("response", response);
                           parsePlaylistJson(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Profile_Activity.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("user",user_number);
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
     private void GettingFollowerNumber(final String no ){
         StringRequest stringRequest = new StringRequest(Request.Method.POST, Followerno,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {

                         Log.e("response", response);
                         //Toast.makeText(getApplicationContext() , response , Toast.LENGTH_LONG).show();
                         Following_number.setText(response);
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(Profile_Activity.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                     }
                 }){
             @Override
             protected Map<String,String> getParams(){
                 Map<String,String> params = new HashMap<String, String>();
                 params.put("user",no);
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
       private void parsePlaylistJson(String json){

          JSONObject jsonObject = null;
           try {
               jsonObject = new JSONObject(String.valueOf(json));
               JSONArray result = jsonObject.getJSONArray("result");


               for (int i = 0; i < result.length(); i++) {

                   JSONObject jo = result.getJSONObject(i);

                   playlistname.add(jo.getString("playlist"));
                   thumbs_up.add(jo.getString("thumbs_up"));



                   //     helper.insertUserInvite(jo.getString("number"), jo.getString("profile"));
               }

           } catch (JSONException e) {
               e.printStackTrace();
           }

           playListAdapter=new PlayListAdapter(Profile_Activity.this, playlistname, thumbs_up );
           playlistlistview.setAdapter(playListAdapter);

       }

          private void GettingFollowingNumber(final String no){
              StringRequest stringRequest = new StringRequest(Request.Method.POST, Followingno,
                  new Response.Listener<String>() {
                      @Override
                      public void onResponse(String response) {

                          Log.e("response", response);
                          //Toast.makeText(getApplicationContext() , response , Toast.LENGTH_LONG).show();
                          Follower_number.setText(response);
                      }
                  },
                  new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          Toast.makeText(Profile_Activity.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                      }
                  }){
              @Override
              protected Map<String,String> getParams(){
                  Map<String,String> params = new HashMap<String, String>();
                  params.put("user",no);
                  return params;
              }

          };
              stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                      5000,
                      DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                      DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
              RequestQueue requestQueue = Volley.newRequestQueue(this);
              requestQueue.add(stringRequest);}












   /* private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if(wifiManager.isWifiEnabled()){
                relativeLayout.setVisibility(View.GONE);
                profile_main.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
              ///  oncreateCopy();

            } else {
                profile_main.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            }
        }
    };*/



    @Override
    public void onTabSelected(int position) {
        // lastSelectedPosition = position;
        // Toast.makeText(getApplicationContext(), " dfsdf" + position, Toast.LENGTH_LONG).show();
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
                startActivity(new Intent(getApplicationContext(), Home_Download_Activity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;

            case 1 :
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
            case 2 :

                break;
            case 3:
                startActivity(new Intent(getApplicationContext(), MusicPlayerActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
        }

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
