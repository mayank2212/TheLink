package org.thelink.Profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.thelink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreatePlayListActivity extends AppCompatActivity {


    ListView listView;
    ArrayList<String>  localsongname , localsongspath , localsongsartist;
    Button createplaylistbutton ;
    CreatePlayListActivityAdapter adapter ;
    newDbHelper dbHelper;
    String Playlistname ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_play_list);
        dbHelper = new newDbHelper(this);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_create_playlist);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listView = (ListView) findViewById(R.id.list_view_createplaylist);
        GettingLocalSongs();

        createplaylistbutton = (Button)findViewById(R.id.button9);
        createplaylistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlert();

            }
        });

        listView.setAdapter(adapter);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                view.setSelected(true);

            }
        });


        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = listView.getCheckedItemCount();
                listView.getSelectedView().setBackgroundColor(Color.GREEN);

                mode.setTitle(checkedCount + " Selected");

                adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(CreatePlayListActivity.this);
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
    private void showAlert(){

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(CreatePlayListActivity.this);
        alertdialog.setTitle("Save Playlist");

        final EditText input = new EditText(this);
        input.setMaxLines(1);
        input.setHint("Name this Playlist");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        alertdialog.setView(input);
        alertdialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Playlistname = input.getText().toString();
                if (Playlistname.equalsIgnoreCase("")) {
                           Toast.makeText(getApplicationContext() , "Enter Name First" , Toast.LENGTH_LONG).show();
                } else {
                    StoringDataintoDb();
                    SendingPlaylistnametoServer(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Name" , "")
                            , PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Number", ""), Playlistname, "0");
                    startActivity(new Intent(CreatePlayListActivity.this, Profile_Activity.class));
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    finish();

                }
            }
        });
        alertdialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertdialog.show();
    }


    private void SendingPlaylistnametoServer(final String name , final String number , final String playname , final String status){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/insert_user_playlist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreatePlayListActivity.this, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",name);
                params.put("number" , number);
                params.put("playlist",playname);
                params.put("thumbs_up" , status);
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

    private void StoringDataintoDb(){
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i))
                selectedItems.add(localsongname.get(position));

           dbHelper.entry(localsongname.get(position) , localsongspath.get(position),Playlistname,0 , localsongsartist.get(position));
        }

        Log.e("array", selectedItems.toString());

        GettingLocalSongs();
    }
    private void GettingLocalSongs(){
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
                int name = musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                int path = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.DATA);
                int displaynmame = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.DISPLAY_NAME);


                do {

                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    String thispath = musicCursor.getString(path);

                    String upToNCharacters = thisTitle.substring(0, Math.min(thisTitle.length(), 3));
                    String lastthree = thispath.substring(thispath.length() - 3);
                    String aud = "AUD";
                    String m4a = "m4a";

                    if (!upToNCharacters.equalsIgnoreCase(aud)) {
                        if (!lastthree.equalsIgnoreCase(m4a)) {

                            localsongname.add(thisTitle);
                            localsongspath.add(thispath);
                            localsongsartist.add(thisArtist);

                        }
                    }
                } while (musicCursor.moveToNext());

            }

        adapter = new CreatePlayListActivityAdapter(this,localsongname , localsongspath , localsongsartist);

            assert musicCursor != null;
            musicCursor.close();

        }catch (Exception e){
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               /* Intent intent = new Intent(FriendRequest.this , Follow_Activity.class);
                startActivity(intent);
                finish();*/
                startActivity(new Intent(CreatePlayListActivity.this, Profile_Activity.class));
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

