package org.thelink.Profile;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
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

public class PlayListSongs extends AppCompatActivity {

    private static final String PLAYLIST_SONG_URL = "http://thelinkweb.com/fetch_user_playlist_songs.php";
    ArrayList<String> song_name , artist_nam, download_url , localsongname , localsongspath , localsongsartist;
    ListView listView;
    PlaylistSongsAdapter playlistSongsAdapter;
    ArrayList<String> combinesongnamelist = new ArrayList<>() ,combineartistnamelist  = new ArrayList<>(), combinesongpathlist  = new ArrayList<>();
    String playlistname;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_songs);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_playlist_songs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));

        playlistname= getIntent().getExtras().getString("playlist_name");

       number  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", "");

        listView = (ListView)findViewById(R.id.listView4);
              GettingLocalSongs(playlistname);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(PlayListSongs.this);
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

public void GettingLocalSongs(String name ){
    localsongsartist = new ArrayList<>();
    localsongname = new ArrayList<>();
    localsongspath =  new ArrayList<>();
    String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    ContentResolver musicResolver = getApplicationContext().getContentResolver();
    Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);
    try {
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //setting thee columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int displayname = musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int path = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int displaynmame = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DISPLAY_NAME);


            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thispath = musicCursor.getString(path);

                String dispalay = musicCursor.getString(displaynmame);
                String upToNCharacters = thisTitle.substring(0, Math.min(thisTitle.length(), 3));
                String lastthree = thispath.substring(thispath.length() - 3);
                String aud = "AUD";
                String m4a = "m4a";

                if(!upToNCharacters.equalsIgnoreCase(aud)) {
                    if(!lastthree.equalsIgnoreCase(m4a)) {

                            localsongname.add(thisTitle);
                            localsongspath.add(thispath);
                            localsongsartist.add(thisArtist);
                    }
                }
            } while (musicCursor.moveToNext());

        }
        Log.e("ASDasdas" , localsongname.toString());
        Log.e("ASDasdas" , localsongsartist.toString());

        assert musicCursor != null;
        musicCursor.close();

        GettingPlaylistSongs(name,number);
    }catch (Exception e){
    }

}

    public void GettingPlaylistSongs(final String name , final String user_number){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, PLAYLIST_SONG_URL,
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
                        Toast.makeText(PlayListSongs.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user",user_number);
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
    private void parseJSON(String json){


        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray("result");

            combineartistnamelist.clear();
            combinesongpathlist.clear();
           combinesongpathlist.clear();
            song_name = new ArrayList<>();
            artist_nam= new ArrayList<>();
            download_url = new ArrayList<>();

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                song_name.add(jo.getString("song"));
                artist_nam.add(jo.getString("artist"));
                download_url.add(jo.getString("download_url"));
            }

            combinesongnamelist = new ArrayList<>(song_name);
            combinesongnamelist.addAll(localsongname);

            combineartistnamelist = new ArrayList<>(artist_nam);
            combineartistnamelist.addAll(localsongsartist);


             combinesongpathlist = new ArrayList<>(download_url);
            combinesongpathlist.addAll(localsongspath);

            playlistSongsAdapter = new PlaylistSongsAdapter(PlayListSongs.this , combinesongnamelist,
                    combineartistnamelist , combinesongpathlist , song_name.size() , playlistname  );
            listView.setAdapter(playlistSongsAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            startActivity(new Intent(PlayListSongs.this, Profile_Activity.class));
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
