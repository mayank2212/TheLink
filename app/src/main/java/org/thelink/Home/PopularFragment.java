package org.thelink.Home;

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
public class PopularFragment extends Fragment {
    public static final String YOUTUBE_SONGS = "http://thelinkweb.com/fetch_indie_music.php";
    private String channel_name;
    private List<String> songname = new ArrayList<>();
    private  List<String> artist_name = new ArrayList<>();
    private  List<String> download_link_popular = new ArrayList<>();

    //recycleview
    private RecyclerView.LayoutManager mLayoutManager ;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter ;


    public PopularFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.popular_fragment, container, false);
        // Inflate the layout for this fragment
        channel_name = YoutubeClass.value;
     //   Toast.makeText(getActivity(), channel_name,Toast.LENGTH_LONG).show();

//        channel_name = this.getArguments().getString("channel_name");

        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.recycle_view_popularfragment);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        GettingYoutubePopularSongs(channel_name);
        return rootview;
    }


    private void GettingYoutubePopularSongs(final String ch_name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, YOUTUBE_SONGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response POPULARRR", response);
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
                params.put("type" , "Popular");
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
        artist_name.clear();
        download_link_popular.clear();
        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray("result");


            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                songname.add(jo.getString("song_name"));
                artist_name.add(jo.getString("artist"));
                download_link_popular.add(jo.getString("download_url"));


            }
            Log.e("name_popular" , songname.toString());


            mAdapter = new YoutubeFragmentAdapter(getContext(),songname , artist_name , download_link_popular);
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