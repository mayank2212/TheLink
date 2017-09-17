package org.thelink.Following;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thelink.MusicPlayer.MusicPlayerActivity;
import org.thelink.InviteActivity.FriendRequest;
import org.thelink.InviteActivity.HomeActivity;
import org.thelink.PlayListClasses.PlayListActivity;
import org.thelink.Profile.Profile_Activity;
import org.thelink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Follow_Activity extends AppCompatActivity implements  BottomNavigationBar.OnTabSelectedListener , CustomListAdapter_Follow.customButtonListener_follow {
    FollowHelper helper;
    SQLiteDatabase db;
    BottomNavigationBar bottomNavigationBar;
    boolean doubleBackToExitPressedOnce = false;
    ListView listView;
   CustomListAdapter_Follow adapter_follow ;
    public static ArrayList<String> follow_list = new ArrayList<String>();
    ArrayList<String> follow_names =  new ArrayList<>();
    public  ArrayList<String> profilepics_list = new ArrayList<String>();
    View parentlayout ;
    public static final String REGISTER_URL_FOLLOWER = "http://www.thelinkweb.com/following.php";
    public static final String UNFOLLOW_URL= "http://thelinkweb.com/unfollow.php";
    ProgressDialog pd ;
    TextView emptyText ;
        AlertDialog alertDialog ;
    RelativeLayout mainlayout , nointernetfollow;
    public static String clicked_numeber ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_);
        parentlayout = findViewById(android.R.id.content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_follow);
        setSupportActionBar(toolbar);
        mainlayout = (RelativeLayout) findViewById(R.id.mainlayoutfollow);
        nointernetfollow = (RelativeLayout)findViewById(R.id.nointernetfollow);
        listView= (ListView) findViewById(R.id.listView_follow);
        emptyText = (TextView)findViewById(android.R.id.empty);
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        oncreatecopy();
       // listView.setEmptyView(emptyText);

        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar_follow);
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar
                .setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.contacts, "Invite").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.follow, "Following").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.profile, "Profile").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.music, "Files").setActiveColorResource(R.color.colorPrimary))
                .setFirstSelectedPosition(1)
                .initialise();

       listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
     //  Toast.makeText(getApplicationContext(), helper.names.get(position) + "  " + follow_list.get(position), Toast.LENGTH_LONG).show();
        alertDialog = new AlertDialog.Builder(Follow_Activity.this).create(); //Read Update
        alertDialog.setTitle("Unfollow");
        alertDialog.setMessage("Do you want to Unfollow " + follow_names.get(position));
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "UNFOLLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               /* Toast.makeText(getApplicationContext(), "unfollow ", Toast.LENGTH_LONG).show();*/
                SendingNumbertoUnfollow(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString
                        ("Number", "defaultStringIfNothingFound"), follow_list.get(position));
                Snackbar snackbar;
                snackbar = Snackbar.make(parentlayout, "You unfollowed " + follow_names.get(position), Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.black));
                snackbar.show();
 oncreatecopy();
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
                alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
                return  true;
    }
});
    }
   public void oncreatecopy(){

        helper = new FollowHelper(this);
        db = helper.getWritableDatabase();
        //helper.viewUser();
        //sending number to get followers list
       Send_Number(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", "defaultStringIfNothingFound"));
    //          Send_Number("9871753634");
        follow_list.clear();
        helper.names.clear();
        //getting names corresponding to numbers


    }
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {


            ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (null != activeNetwork) {
                    if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        //return TYPE_WIFI;
                        nointernetfollow.setVisibility(View.GONE);
                        mainlayout.setVisibility(View.VISIBLE);
                    }
                    // you can start service over here
                    if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // return TYPE_MOBILE;
                        nointernetfollow.setVisibility(View.GONE);
                        mainlayout.setVisibility(View.VISIBLE);
                    }
                    // stop service over here so it wont consume user data
                }else {
                    mainlayout.setVisibility(View.GONE);
                    nointernetfollow.setVisibility(View.VISIBLE);
                }
                // return TYPE_NOT_CONNECTED;
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
    private void SendingNumbertoUnfollow(final String number , final String number2){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UNFOLLOW_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);
                             //Toast.makeText(getApplicationContext() , response , Toast.LENGTH_LONG).show();
                       oncreatecopy();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Follow_Activity.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("sender",number);
                params.put("number" , number2);
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


    private void Send_Number(final String user){
       // follow_list.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL_FOLLOWER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(Follow_Activity.this, response, Toast.LENGTH_LONG).show();
                      Log.e("response", response);
                        //Iterable<String> pieces = Splitter.fixedLength(3).split(string);
                       /* followinglist.add(response);*/

                        parseJSON(response);


                        Log.e("response ", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Follow_Activity.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user",user);
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

    protected void parseJSON(String json) {
        JSONObject jsonObject = null;
        follow_list.clear();
        follow_names.clear();
        profilepics_list.clear();
        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray("result");


            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                follow_list.add(jo.getString("number"));
                follow_names.add(jo.getString("name"));
                profilepics_list.add(jo.getString("profile"));

            }
            AsyncTaskFollow asyncTaskFollow  = new AsyncTaskFollow();
            asyncTaskFollow.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> GettingFollow () {

        return helper.names;
    }


    @Override
    public void onButtonClickListner(int position, String value, String name) {
        int itemPosition = position;

        String itemValue = follow_list.get(position);
                Log.e("number " , follow_list.get(position));
          clicked_numeber = itemValue;
        // helper.selectUser(itemValue);
           //     helper.ReturnNumber();
//        Log.e("return number ", helper.ReturnNumber());

        /*Intent intent = new Intent(getApplicationContext() , PlayListActivity.class);
        startActivity(intent);*/
        startActivity(new Intent(Follow_Activity.this, PlayListActivity.class));
        overridePendingTransition(R.anim.enter_original, R.anim.exit_original);

    }


    private class AsyncTaskFollow extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
          //  helper.Getting_Name(follow_list);
            adapter_follow=new CustomListAdapter_Follow(Follow_Activity.this, follow_names, profilepics_list);
            adapter_follow.setCustomButtonListner(Follow_Activity.this);
            listView.setAdapter(adapter_follow);
            if(follow_names.size() ==0 ){

                emptyText.setVisibility(View.VISIBLE);
            }
           // HidingDialog();
        }
    }

























    private class AsyncTaskFriend extends AsyncTask<Void, Void , Void>{

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Intent intent = new Intent(Follow_Activity.this ,FriendRequest.class) ;
            startActivity(new Intent(Follow_Activity.this, FriendRequest.class));
            overridePendingTransition(R.anim.enter_original, R.anim.exit_original);

            // startActivity(intent);
        }
    }


    public void CreatingProgressDialog(String title) {
        pd = new ProgressDialog(Follow_Activity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(title);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

    }

    public void HidingDialog(){
        pd.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.followmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.friendrequest:
                // Not implemented here
                AsyncTaskFriend asyncTaskFriend =  new AsyncTaskFriend();
                asyncTaskFriend.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
            default:
                break;


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
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;

            case 1 :

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
}
