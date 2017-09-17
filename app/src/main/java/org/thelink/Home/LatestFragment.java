package org.thelink.Home;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Map;

/**
 * Created by Mayank on 11-02-2017.
 */
public class LatestFragment extends Fragment{

    public static final String YOUTUBE_SONGS = "http://thelinkweb.com/fetch_indie_music.php";
    private String channel_name;
    //json
    private List<String> songname = new ArrayList<>();
    private  List<String> artistname = new ArrayList<>();
    private  List<String> download_link = new ArrayList<>();
    //recycleview
    private RecyclerView.LayoutManager mLayoutManager ;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter ;


    public LatestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.latest_fragment, container, false);

        channel_name = YoutubeClass.value;
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.recycle_view_latestfragment);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
//      channel_name = this.getArguments().getString("channel_name");
        GettingYoutubeLatestSongs(channel_name);

        rootview.getContext().registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        return rootview;
    }



    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            ProgressDialog loading = new ProgressDialog(getContext());
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

        private void GettingYoutubeLatestSongs(final String ch_name){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, YOUTUBE_SONGS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response", response);
                            parseYoutubeJson(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("ch_name" , ch_name);
                    params.put("type" , "Latest");
                    return params;
                }

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }

     private   void parseYoutubeJson(String json){

         JSONObject jsonObject = null;
         songname.clear();
         artistname.clear();
         download_link.clear();
         try {
             jsonObject = new JSONObject(json);
             JSONArray result = jsonObject.getJSONArray("result");


             for (int i = 0; i < result.length(); i++) {
                 JSONObject jo = result.getJSONObject(i);
                 songname.add(jo.getString("song_name"));
                 artistname.add(jo.getString("artist"));
                 download_link.add(jo.getString("download_url"));


             }
             Log.e("name" , songname.toString());

             mAdapter = new YoutubeFragmentAdapter(getContext(),songname , artistname , download_link);
             mRecyclerView.setAdapter(mAdapter);

            // mLayoutManager = new LinearLayoutManager(this);
             //recyclerView.setLayoutManager(mLayoutManager);
            // mAdapter = new FourImageViewAdapter(BollywoodSongs.this,name , artist_name ,  pictures , download_link);
            // recyclerView.setAdapter(mAdapter);

         } catch (JSONException e) {
             e.printStackTrace();
         }

    }
}