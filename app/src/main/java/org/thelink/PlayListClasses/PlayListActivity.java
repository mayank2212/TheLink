package org.thelink.PlayListClasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import org.thelink.Following.FollowHelper;
import org.thelink.Following.Follow_Activity;
import org.thelink.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayListActivity extends AppCompatActivity implements MediaScannerConnection.MediaScannerConnectionClient,SearchView.OnQueryTextListener , CustomListAdapter_Playlist.customButtonListener_playlist {
    SQLiteDatabase db;
     String clicked_number ;
    RelativeLayout mainlayout , nointernet ;
     FollowHelper helper ;
    public  ArrayList<String> user_playlist = new ArrayList<String>();
    public ArrayList<String> songpaths =  new ArrayList<>();
    public ArrayList<String> songtitle = new ArrayList<>();
    public ArrayList<String> phonepaths = new ArrayList<>();
    public static final  String JSON_URL = "http://www.thelinkweb.com/fetchuploadedsongs.php";
     public static final String REGISTER_URL_PLAYLIST = "http://www.thelinkweb.com/retrieve_songs.php";
    ListView listView ;
    private SearchView searchView;
    ProgressDialog pd , downloadingprogress;
    AlertDialog alertDialog ;
    DownloadManager downloadManager;
        Long reference;
    private MenuItem searchMenuItem;
    File direct ;
    String folderName = "thelink";
    BroadcastReceiver receiver;
    static String finalsong_name ;
    //private android.widget.SearchView searchView;
    private SearchView mSearchView;
    CustomListAdapter_Playlist adapter ;
    //InterstitialAd mInterstitialAd;
    String number ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        /*this.registerReceiver(this.mConnReceiver1,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));*/
        mainlayout = (RelativeLayout) findViewById(R.id.mainlayout_playlist);
        nointernet = (RelativeLayout) findViewById(R.id.nointernetplaylist);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_playlist);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
      //  mSearchView = (SearchView) findViewById(R.id.search_view);
      /*  helper = new FollowHelper(this);
        db = helper.getWritableDatabase();
        clicked_number  = helper.ReturnNumber();*/
         number = Follow_Activity.clicked_numeber ;
        Log.e("number " , number  + "") ;
        Send_request();
        //Toast.makeText(getApplicationContext() , clicked_number , Toast.LENGTH_LONG).show();
       //now clicked number will go to server matches laylist from sql and will show in list
         listView = (ListView) findViewById(R.id.listView2);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);


        //ad tryingg


       // mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-4325649262156513~7340832784");
        /*mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                //  beginPlayingGame();
            }
        });*/

      //  requestNewInterstitial();

        receiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {

                            scanFile(Environment.DIRECTORY_DOWNLOADS +
                                    File.separator + folderName + File.separator + songtitle , "");
                        }
                    }
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }
  /*  private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }*/
    private BroadcastReceiver mConnReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if(wifiManager.isWifiEnabled()){
                nointernet.setVisibility(View.GONE);
                mainlayout.setVisibility(View.VISIBLE);
                //oncreatecopy();
            } else {
                mainlayout.setVisibility(View.GONE);
                nointernet.setVisibility(View.VISIBLE);

            }
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mConnReceiver1);
        unregisterReceiver(receiver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mConnReceiver1, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void Send_request(){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
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
                            Toast.makeText(PlayListActivity.this,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("number",number);
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
        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray("result");


            songtitle = new ArrayList<>();
            songpaths = new ArrayList<>();
            phonepaths= new ArrayList<>();

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                songtitle.add(jo.getString("song_title"));
                songpaths.add(jo.getString("uploadedpath"));
                phonepaths.add(jo.getString("phonepath"));
            }
            Log.e("song title",songtitle.toString());
            Log.e("song paths", songpaths.toString());

            PopulatingList populatingList =  new PopulatingList();
            populatingList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Send_Playlist(final String number){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL_PLAYLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                       // Log.e("response", response);
                         //CreatingProgressDialog("Please Wait... Loading");
                        StringBuilder sb = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new StringReader(response));
                        String line ;
                        try{
                            while((line= reader.readLine()) != null){
                                line = line.replaceAll("\\s+","");
                                user_playlist.add(line);
                            }

                        }catch (Exception e){

                        }

                        PopulatingList populatingList =  new PopulatingList();
                        populatingList.execute((Void) null) ;

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PlayListActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("number",number);
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
    public void onMediaScannerConnected() {

    }

    @Override
    public void onScanCompleted(String path, Uri uri) {

    }


    private class PopulatingList extends AsyncTask<Void, Void, Boolean> {

               @Override
               protected Boolean doInBackground(Void... params) {


                   return true;
               }


               @Override
               protected void onPostExecute(Boolean aBoolean) {
                   super.onPostExecute(aBoolean);
                  adapter=new CustomListAdapter_Playlist(PlayListActivity.this, songtitle , songpaths);
                   adapter.setCustomButtonListner(PlayListActivity.this);

//                  HidingDialog();
                   listView.setAdapter(adapter);
                   listView.setTextFilterEnabled(true);
                //   setupSearchView();
                  // HidingDialog();
               }
           }



    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
    }

 /*   public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText.toString());
           *//* mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Send_request();
                    return true;
                }
            });*//*
        }
        return true;
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlistmenu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView)menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        PlayListActivity.this.adapter.getFilter().filter(newText);

        return true;
    }
/*    public boolean onQueryTextSubmit(String query) {
        return false;
    }*/


    public void CreatingProgressDialog(String title) {
        pd = new ProgressDialog(PlayListActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(title);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

    }

    public void DownloadingProgres(String title){
        downloadingprogress = new ProgressDialog(PlayListActivity.this);
        downloadingprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        downloadingprogress.setMessage(title);
        downloadingprogress.setCancelable(false);
        downloadingprogress.setCanceledOnTouchOutside(false);
        downloadingprogress.show();

    }
    public void Hidingdownloadingprogress(){
        downloadingprogress.hide();
    }

    public void HidingDialog(){
        pd.hide();
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       *//* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlistmenu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);*//*

        return true;
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(PlayListActivity.this, Follow_Activity.class));
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

    @Override
    public void onButtonClickListner(final int position, final String value, final String name) {
            // Toast.makeText(getApplicationContext() , position + "FUCKING URL " + songpaths.get(position) , Toast.LENGTH_LONG).show();

      /*final String real_position  =   adapter.getItem(position);
               final int abc = adapter.getPosition(real_position);
                   Log.e("real postion " , real_position + abc + "             ");*/
        alertDialog = new AlertDialog.Builder(PlayListActivity.this).create(); //Read Update
        alertDialog.setTitle("Download");
        alertDialog.setMessage("Do you want to Download this song " + value);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "DOWNLOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              //  Toast.makeText(getApplicationContext(), "Download " , Toast.LENGTH_LONG).show();
             //    if(clicked_number.equals(whatevernumebr)){

               /* Intent i = new Intent(PlayListActivity.this, DownloadActivity.class);
                i.putExtra("songpath", songpaths.get(position));
                i.putExtra("songtitle", songtitle.get(position));
                startActivity(i);*/


                DownloadingSongs(name, value);
                finalsong_name = songtitle.get(position);



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
    }

    public void DownloadingSongs(String downloadedpaths , String song_name){


        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadedpaths = downloadedpaths.replaceAll(" " , "%20");
        downloadedpaths = downloadedpaths.replace("[", "%5B");
        downloadedpaths = downloadedpaths.replace("]", "%5D");
            Uri uri = Uri.parse(downloadedpaths);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setAllowedOverRoaming(false);
            request.setTitle(song_name);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.allowScanningByMediaScanner();
        //request.setDestinationInExternalFilesDir(PlayListActivity.this, Environment.DIRECTORY_DOWNLOADS,
          //      File.separator + folderName + File.separator + song_name + ".mp3");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                File.separator + folderName + File.separator + song_name + ".mp3");
            reference = downloadManager.enqueue(request);
        Log.e("refernece", String.valueOf(reference));



    }
    private void scanFile(String path, String song_name) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(Environment.DIRECTORY_DOWNLOADS+
                    File.separator + folderName + File.separator + songtitle);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
        else
        {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(Environment.DIRECTORY_DOWNLOADS+
                    File.separator + folderName + File.separator + songtitle)));
        }
    }

}
