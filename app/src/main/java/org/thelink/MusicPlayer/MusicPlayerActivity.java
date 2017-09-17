package org.thelink.MusicPlayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.thelink.Home.Home_Download_Activity;
import org.thelink.InviteActivity.HomeActivity;
import org.thelink.Profile.Profile_Activity;
import org.thelink.R;

public class MusicPlayerActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener  {

    boolean doubleBackToExitPressedOnce = false;
    BottomNavigationBar bottomNavigationBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_files);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));


        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar_files);
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.home, "Home").setActiveColorResource(R.color.holo_pink_))
                .addItem(new BottomNavigationItem(R.drawable.contacts, "Follow").setActiveColorResource(R.color.holo_pink_))
                .addItem(new BottomNavigationItem(R.drawable.profile, "Profile").setActiveColorResource(R.color.holo_pink_))
                .addItem(new BottomNavigationItem(R.drawable.music_player, "Music").setActiveColorResource(R.color.holo_pink_))
                .setFirstSelectedPosition(3)
                .initialise();
    }

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
        switch (position) {
            case 0:
                startActivity(new Intent(getApplicationContext(), Home_Download_Activity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;

            case 1:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;

            case 2:
                startActivity(new Intent(getApplicationContext(), Profile_Activity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
            case 3:

                break;
        }
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


    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
               /* relativeLayout.setVisibility(View.GONE);
                mainlayout.setVisibility(View.VISIBLE);*/
              /*  intent = new Intent(context, SongService.class);
                context.startService(intent);*/
                //     Log.e("home", "service is started");

            } else {
                /*mainlayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);*/
               /* intent = new Intent(context, SongService.class);
                context.stopService(intent);*/
                Log.e("newwatcher", "service is stopped");
            }
        }
    };



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