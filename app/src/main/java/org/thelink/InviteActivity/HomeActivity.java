package org.thelink.InviteActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView;
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
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thelink.Following.FollowHelper;
import org.thelink.Home.Home_Download_Activity;
import org.thelink.MusicPlayer.MusicPlayerActivity;
import org.thelink.Profile.Profile_Activity;
import org.thelink.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity implements   SwipeRefreshLayout.OnRefreshListener ,BottomNavigationBar.OnTabSelectedListener , ListAdapter.customButtonListener {

    private String REGISTER_URL="http://www.thelinkweb.com/insert.php";
    public final String UNFOLLOW_URL = "http://www.thelinkweb.com/un_friend.php";
    ListAdapter adapter;
    ListView listView;

    ArrayList<String> allContacts = new ArrayList<String>();
    ArrayList<String> follow_invite_name = new ArrayList<>();

    JSONArray jsonArray ;
    JSONObject jsonObject=null ;
    JSONObject finalobject ;

     //Follow list
    ArrayList<String> followinglist = new ArrayList<String>();
    ArrayList<String> follow_name = new ArrayList<>();
    ArrayList<String> followingpicture = new ArrayList<>();
    ArrayList<String> follow_status =  new ArrayList<>();

    AlertDialog alertDialog;

    ProgressDialog loading = null;
    private int hot_number = 0;
    private TextView ui_hot = null;
    FloatingActionButton floatingActionButton ;

    boolean doubleBackToExitPressedOnce = false;
    ProgressDialog pd ;
    View parentlayout ;
    BottomNavigationBar bottomNavigationBar;
    private String REGISTER_URL_2="http://www.thelinkweb.com/send_notification.php";
    FollowHelper helper ;
    Context context ;
    SQLiteDatabase db , db_songs , dbsync;
    RelativeLayout relativeLayout  , mainlayout;
     static String user;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
    //   String countryCodeValue = "us";
        if (!countryCodeValue.equals("in")) {
            setContentView(R.layout.outsideindialayout);
            Button gobackbutton = (Button)findViewById(R.id.gobackbutton);
            gobackbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Home_Download_Activity.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            });

        } else {

            //in case of india
            setContentView(R.layout.activity_home);
            initcomponents();


            helper = new FollowHelper(this);
            this.registerReceiver(this.mConnReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            db = helper.getWritableDatabase();


            AsyncTaskInvite asyncTaskInvite = new AsyncTaskInvite();
            asyncTaskInvite.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun) {



                //Registration values....
                String name = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Name", "defaultStringIfNothingFound");
                String number = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", "defaultStringIfNothingFound");
                // String number = "9871753634";
                String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", "");
                String profileurl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AvatarUrl", "http://www.thelinkweb.com/uploads/default.png");
                Log.e("Name And Numbertoken ", name + "  " + number + "  " + token);
                sendRegistrationToServer(name, number, token, profileurl);

                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.apply();

            }


            Log.e("token", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", ""));
            //populating list view from the server
            //  Populating();


            bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar_home);
            bottomNavigationBar.setTabSelectedListener(this);
            bottomNavigationBar
                    .setMode(BottomNavigationBar.MODE_FIXED);
            bottomNavigationBar
                    .addItem(new BottomNavigationItem(R.drawable.home, "Home").setActiveColorResource(R.color.holo_pink_))
                    .addItem(new BottomNavigationItem(R.drawable.contacts, "Follow").setActiveColorResource(R.color.holo_pink_))
                    .addItem(new BottomNavigationItem(R.drawable.profile, "Profile").setActiveColorResource(R.color.holo_pink_))
                    .addItem(new BottomNavigationItem(R.drawable.music_player, "Music").setActiveColorResource(R.color.holo_pink_))
                    .setFirstSelectedPosition(1)
                    .initialise();
            this.registerReceiver(this.mConnReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }


     public void initcomponents(){
         listView = (ListView) findViewById(R.id.contacts_list);
         parentlayout = findViewById(android.R.id.content);
         relativeLayout = (RelativeLayout) findViewById(R.id.nointernet);
         mainlayout = (RelativeLayout)findViewById(R.id.mainlayout);
         final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
         toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));

         floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_homeactivity);
         floatingActionButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                 shareIntent.setType("text/plain");
                 shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try TheLink for Android!");
                 shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "TheLink - Free Music Download for Android. Click here to download: https://play.google.com/store/apps/details?id=org.thelink");

                 Intent chooserIntent = Intent.createChooser(shareIntent, "Share with");
                 chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(chooserIntent);
             }
         });

         listView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
     }

    @Override
    public void onRefresh() {

    }

    private class AsyncTaskInvite extends AsyncTask<Void , String ,String> {

        @Override
        protected void onPreExecute() {



            Log.e("follow_invite_name", follow_invite_name.toString());
            loading = new ProgressDialog(HomeActivity.this);
            loading.setCancelable(false);
            loading.setMessage("Loading... Please Wait..");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            helper.GettingNumbers();
            helper.GettingContactsName();

            //sending number to serveer for checking
            jsonArray=new JSONArray();

            try {

                     for (int i = 0 ; i<helper.numbers.size() ; i++){
                         jsonObject = new JSONObject();
                         jsonObject.put("user" , PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", "Your Number"));
                        //  jsonObject.put("user" , "9871753634");
                         jsonObject.put("number", helper.numbers.get(i));
                         jsonArray.put(jsonObject);

                     }

                finalobject = new JSONObject();
                finalobject.put("contacts", jsonArray);
          Log.e("jsonArray" , jsonArray.toString());
            }catch (Exception e ){

            }


            URL url;
            HttpURLConnection connection;
            OutputStream printout;
            StringBuilder sb = null;
            ArrayList<String> number = null;

            try {

                url = new URL("http://www.thelinkweb.com/contact2.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                connection.connect();

                printout = new BufferedOutputStream(connection.getOutputStream());
                printout.write(finalobject.getJSONArray("contacts").toString().getBytes());
                printout.flush();
                printout.close();
                sb= new StringBuilder();
                number = new ArrayList<>();
                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(), "utf-8"));
                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    parseJsonInvite(sb.toString());
                    br.close();
                }
            } catch (JSONException | IOException e1) {
                e1.printStackTrace();
            }

            return "a";
        }

        @Override
        protected void onPostExecute(String s) {

            Set<String> removing_duplicate = new LinkedHashSet<>(allContacts);
            allContacts = new ArrayList<>(removing_duplicate);
            adapter = new ListAdapter(getApplicationContext(),follow_name  , followingpicture, follow_status ,followinglist );
            adapter.setCustomButtonListner(HomeActivity.this);
            listView.setAdapter(adapter);
            listView.setTextFilterEnabled(true);
            loading.dismiss();

        }

    }

  private void parseJsonInvite(String response){
      //already installed user - following user
      followinglist.clear();
      follow_status.clear();
      followingpicture.clear();
      follow_name.clear();

      JSONObject jsonObject = null;
      try {
          jsonObject = new JSONObject(String.valueOf(response));
          JSONArray result = jsonObject.getJSONArray("result");


          Log.e("result" , response);
          for (int i = 0; i < result.length(); i++) {
              Log.e("SAdsadasd", result.length() + " ");

              JSONObject jo = result.getJSONObject(i);
              String check = jo.getString("status");
               if(!check.equals("1")){
                   follow_status.add(jo.getString("status"));
                   followinglist.add(jo.getString("number"));
                   followingpicture.add(jo.getString("profile"));
                   follow_name.add(jo.getString("name"));
              }

         //     helper.insertUserInvite(jo.getString("number"), jo.getString("profile"));
          }
          Log.e("picssss", followingpicture.toString());
          Log.e("number", followinglist.toString());
          Log.e("name", follow_name.toString());
          Log.e("status", follow_status.toString());

      } catch (JSONException e) {
          e.printStackTrace();
      }


  }

    @Override
    public void onButtonClickListner(int position, String value, String name , String status) {


        if(status.equals("0")){

            Send_request(value , PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", "") , name);

        } else if (status.equals("2")){
      CreatingCancelDailog(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""), value , name);
        }

    }

    
    private void CreatingCancelDailog(String user_name , final String number , String name){
        alertDialog = new AlertDialog.Builder(HomeActivity.this).create(); //Read Update
        alertDialog.setTitle("Cancel Request ");
         alertDialog.setMessage("Do you want to cancel your follow request to  " + name);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {


                SendingRequestCancelRequest(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""), number);
                listView.setAdapter(null);
                adapter.notifyDataSetChanged();
                AsyncTaskInvite asyncTaskInvite = new AsyncTaskInvite();
                asyncTaskInvite.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Toast toast = Toast.makeText(HomeActivity.this, "Request Cancelled...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();


            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
        //  alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
        
    } 
  private void SendingRequestCancelRequest(final String usernumber , final String number){
      StringRequest stringRequest = new StringRequest(Request.Method.POST, UNFOLLOW_URL,
              new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {

                      Log.e("response", response);
                      //  Toast.makeText(getApplicationContext() , response , Toast.LENGTH_LONG).
                      //         show();
                     // SendingNumber(number);
                  }
              },
              new Response.ErrorListener() {
                  @Override
                  public void onErrorResponse(VolleyError error) {
                      Toast.makeText(HomeActivity.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                  }
              }){
          @Override
          protected Map<String,String> getParams(){
              Map<String,String> params = new HashMap<String, String>();
              params.put("number" , usernumber);
              params.put("sender",number);
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


    private void Send_request(final String number, final String sender , final String name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL_2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response" , response);

                        String firstresponse = response.split("\\{")[0];
                        String new_request  ="REQUEST SEND";

                             if(response.contains("failure" + ":1")){

                                 Snackbar snackbar;
                                 snackbar = Snackbar.make(parentlayout, "Your request is not sent..Please try again" , Snackbar.LENGTH_LONG);
                                 View snackBarView = snackbar.getView();
                                 snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                 TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                                 textView.setTextColor(getResources().getColor(R.color.black));
                                 snackbar.show();



                        }else if (response.contains(new_request)) {

                            Snackbar snackbar;
                            snackbar = Snackbar.make(parentlayout, "Your request has been sent to " + name , Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(getResources().getColor(R.color.black));
                            snackbar.show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this,"Your request is not sent..Please try again", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("phone",number);
                params.put("sender",sender);
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



    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(HomeActivity.this);
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

    public void service(){
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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





    private void sendRegistrationToServer(final String name, final String number,final String token , final String url) {
        // Add custom implementation, as needed.

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    //    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
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
                params.put("token",token);
                params.put("profileurl" , url);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
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
                startActivity(new Intent(getApplicationContext(), Home_Download_Activity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;

            case 1 :

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


    private void GettingRequestNumber(final String usernumber){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/friendrequestno.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            Log.e("responseeee", response);
                      String number =   response.replaceAll("\\s+","");
                        updateHotCount(Integer.parseInt(number));
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
                params.put("user",usernumber);

                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.followmenu, menu);
        final View menu_hotlist = menu.findItem(R.id.friendrequest).getActionView();
        ui_hot = (TextView) menu_hotlist.findViewById(R.id.hotlist_hot);

        GettingRequestNumber(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""));

        new MyMenuItemStuffListener(menu_hotlist, "Show hot message") {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this, FriendRequest.class));
                overridePendingTransition(R.anim.enter_original, R.anim.exit_original);
            }
        };
        return super.onCreateOptionsMenu(menu);
    }

    // call the updating code on the main thread,
// so we can call this asynchronously
    public void updateHotCount(final int new_hot_number) {

        if (ui_hot == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new_hot_number == 0)
                    ui_hot.setVisibility(View.INVISIBLE);
                else {
                    ui_hot.setVisibility(View.VISIBLE);
                    ui_hot.setText(Integer.toString(new_hot_number));
                }
            }
        });
    }

    static abstract class MyMenuItemStuffListener implements View.OnClickListener, View.OnLongClickListener {
        private String hint;
        private View view;

        MyMenuItemStuffListener(View view, String hint) {
            this.view = view;
            this.hint = hint;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override abstract public void onClick(View v);

        @Override public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            view.getLocationOnScreen(screenPos);
            view.getWindowVisibleDisplayFrame(displayFrame);
            final Context context = view.getContext();
            final int width = view.getWidth();
            final int height = view.getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, "Request", Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();
            return true;
        }
    }
}


