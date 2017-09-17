package org.thelink.Profile;

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
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import org.thelink.Following.FollowHelper;
import org.thelink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FollowingClass extends AppCompatActivity implements  CustonAdapter_Followers.customButtonListener_foollwers{

    ListView listView ;
    public final String FOLLOWING_URL = "http://thelinkweb.com/following.php";
    public final String UNFOLLOW_URL = "http://www.thelinkweb.com/un_friend.php";
    public ArrayList<String> followers_list  = new ArrayList<>();
    public ArrayList<String> followers_namelist =  new ArrayList<>();
    public ArrayList<String> profilepicture  = new ArrayList<>();
    FollowHelper helper ;
    SQLiteDatabase db ;
    CustonAdapter_Followers adapter_follower ;
    String number ;
    AlertDialog alertDialog ;
    View parentLayout ;
    RelativeLayout nointernet , mainlayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_class);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_following);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        listView = (ListView)findViewById(R.id.listViewfollowing);

          number =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", "");
      //  number = "9871753634";

        //getting follower list
        FollowingAsync asyncTaskfollower =  new FollowingAsync();
        asyncTaskfollower.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(FollowingClass.this);
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

    private  void SendingNumber(final String usernumber){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FOLLOWING_URL,
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
                        Toast.makeText(FollowingClass.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    protected void parseJSON(String json) {
        JSONObject jsonObject = null;
        followers_list.clear();
        followers_namelist.clear();
        profilepicture.clear();
        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray("result");


            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                followers_list.add(jo.getString("number"));
                followers_namelist.add(jo.getString("name"));
                profilepicture.add(jo.getString("profile"));

            }

//
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter_follower=new CustonAdapter_Followers(FollowingClass.this, followers_namelist, profilepicture , followers_list);
        adapter_follower.setCustomButtonListner(FollowingClass.this);
        listView.setAdapter(adapter_follower);
    }

    private class FollowingAsync extends AsyncTask<Void , Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            SendingNumber(number);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }


    }


    @Override
    public void onButtonClickListner(final int position,final String m_number, String name) {


        alertDialog = new AlertDialog.Builder(FollowingClass.this).create(); //Read Update
        alertDialog.setTitle("Delete " + followers_namelist.get(position));
        //  alertDialog.setMessage("Do you want to Unfollow " + followers_namelist.get(position));
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                SendingFollowerNumber( followers_list.get(position) , number);
                Toast toast = Toast.makeText(FollowingClass.this, "Deleted...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
        //  alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
    }

    private void SendingFollowerNumber( final String user_number   , final String number_unfollow){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/unfollow.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);
                        //  Toast.makeText(getApplicationContext() , response , Toast.LENGTH_LONG).
                        //         show();
                        SendingNumber(number);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FollowingClass.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                Log.e("number key" , number_unfollow);
                Log.e("sender key" , user_number);
                params.put("number" , user_number);
                params.put("sender",number_unfollow);
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
        switch (item.getItemId()) {
            case android.R.id.home:
               /* Intent intent = new Intent(FriendRequest.this , Follow_Activity.class);
                startActivity(intent);
                finish();*/
                startActivity(new Intent(FollowingClass.this, Profile_Activity.class));
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
