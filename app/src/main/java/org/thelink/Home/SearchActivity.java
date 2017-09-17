package org.thelink.Home;

        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.BroadcastReceiver;
        import android.content.Context;
import android.content.Intent;
        import android.content.IntentFilter;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
        import org.thelink.DownloadClass;
        import org.thelink.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements SearchListAdapter.customButtonListener_search   {

   /* EditText search_edittext ;
    private String Result;
    Button search_button;
    ListView listView;
    Intent intent;
    ArrayList<String> song_name;
    ArrayList<String> song_link;
    private static String JSON_URL;
    String Download_song_name;
    SearchListAdapter adapter;
    private String iframe_Src;
    String folderName = "thelink";
    private AlertDialog.Builder alert;
    AlertDialog ad ;*/



    //new <code></code>
    private String Result;
    private ListView listView;
    private EditText search_edittext;
    private Button search_button;
    private String URL;
    ArrayList<String> song_name;
    ArrayList<String> song_link;
    SearchListAdapter adapter;
    Intent intent;
    String subtract = "http://mp3liosong.xyz/watch?v=";
    String folderName = "thelink";
    String Download_song_name;
    private AlertDialog.Builder alert;
    AlertDialog ad ;
    private String downnload_song_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setTitle("Search...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.holo_pink_));
        song_name = new ArrayList<>();
        song_link = new ArrayList<>();

        listView = (ListView)findViewById(R.id.search_list_view);


        search_edittext = (EditText)findViewById(R.id.search_edittext);
        search_edittext.requestFocus();
        //getting keyboard
        // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(search_edittext, InputMethodManager.SHOW_IMPLICIT);

        search_edittext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(!search_edittext.getText().toString().equals("")) {
                     //   Getting_Url_For_Search();
                        Get_URL();
                        HideKeyboard();
                    }
                }

                return true;
            }
        });

        search_button = (Button)findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!search_edittext.getText().toString().equals("")) {
                   // Getting_Url_For_Search();
                    Get_URL();
                    HideKeyboard();
                }
            }
        });
        search_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (search_edittext.getRight() - search_edittext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (!search_edittext.getText().toString().equals("")) {
                            // Getting_Url_For_Search();
                            search_edittext.setText("");
                            adapter.notifyDataSetChanged();
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                return false;
            }
        });
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(SearchActivity.this);
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
    private void HideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void Get_URL(){
        URL="http://mp3lq.com/music/" + search_edittext.getText().toString();
        Search_request(URL);
    }

    private void Search_request(String JSON_URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Result = response;
                        //  Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        Parse_Response volley_request = new Parse_Response();
                        volley_request.execute(Result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    @Override
    public void onButtonClickListner(int position, String value, String name) {

        DownloadClass downloadClass = new DownloadClass(SearchActivity.this);
        downloadClass.DownloadMusic(name , value);

    }


    class Parse_Response extends AsyncTask<String,String,String>{
        String Response;

        @Override
        protected String doInBackground(String... params) {

            Response = params[0];
            song_name.clear();
            song_link.clear();
            Document doc= Jsoup.parse(Response);
            Elements links = doc.select("a");
            Elements divs = doc.select("div.meta strong");
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();

            for (Element div : divs) {
                song_name.add(div.text());
                sb.append(div.text());

            }
            Log.e("song_name",song_name.toString());

            for (Element link : links){


                // Or if you want to have absolute URL instead, so that you can leech them.
                String absUrl = link.absUrl("href");
                if(absUrl.equals("")){

                }
                else {
                    sb2.append(absUrl);
                    String youtube_link = absUrl.toString();
                    String sub = youtube_link.replace("http://mp3liosong.xyz/watch?v=","");
                    String finallink = "http://www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=" + sub;
                    song_link.add(finallink);
                }
            }
            Log.e("song_link",song_link.toString());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter=new SearchListAdapter(SearchActivity.this, song_name , song_link );
            adapter.setCustomButtonListner(SearchActivity.this);
            listView.setAdapter(adapter);

        }
    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            startActivity(new Intent(SearchActivity.this, Home_Download_Activity.class));
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
