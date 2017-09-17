package org.thelink.Profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

import org.thelink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mayank on 24-02-2017.
 */
public class PlayListAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> playlistname , thumbsup;


    public PlayListAdapter(Context mcontext, ArrayList<String> mplaylist, ArrayList<String> mthumbsup){
        super(mcontext, R.layout.customplaylist_row, mplaylist );
        this.context = mcontext;
        this.playlistname = mplaylist;
        this.thumbsup = mthumbsup;

    }

    @Override
    public int getCount() {
        return playlistname.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Playlistholder playlistholder;


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.customplaylist_row, null);
            playlistholder = new Playlistholder();
            playlistholder.mainlayout = (RelativeLayout)convertView.findViewById(R.id.mainlayout);
            playlistholder.name = (TextView)convertView.findViewById(R.id.playlistname);
            playlistholder.imageButton = (ImageButton)convertView.findViewById(R.id.editbutton);
            playlistholder.deleteButton = (ImageButton)convertView.findViewById(R.id.imageButton5);
            playlistholder.number = (TextView)convertView.findViewById(R.id.likes_number);

            convertView.setTag(playlistholder);
        } else {
            playlistholder = (Playlistholder) convertView.getTag();
        }

        final String tempname = getItem(position);
      //  final String currentno = number.get(position);
        playlistholder.number.setText("Likes : " +thumbsup.get(position));
        playlistholder.name.setText(playlistname.get(position));
        playlistholder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PlayListSongs.class);
                intent.putExtra("playlist_name", playlistname.get(position));
                context.startActivity(intent);
            }
        });
        playlistholder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayListSongs.class);
                intent.putExtra("playlist_name", playlistname.get(position));
                context.startActivity(intent);
            }
        });

        playlistholder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create(); //Read Update
                alertDialog.setTitle("Delete Playlist ");
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        DeletePlayList(playlistname.get(position));
                        DeletingPlaylistSongs(playlistname.get(position));
                        Toast.makeText(context , "Playlist Deleted.." , Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

            }
        });


        return convertView;
    }


    private void DeletePlayList(final String name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/delete_all_playlist_songs.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                String number = PreferenceManager.getDefaultSharedPreferences(context).getString("Number", "");
                params.put("number",number);
                params.put("playlist" , name);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    private void DeletingPlaylistSongs(final String name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/delete_playlist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);
                        //Toast.makeText(getApplicationContext() , response , Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"Internal Error: Please try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                String number = PreferenceManager.getDefaultSharedPreferences(context).getString("Number", "");
                params.put("number",number);
                params.put("playlist" , name);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public class Playlistholder {

        private String name1;
        TextView number , name;
        ImageButton imageButton , deleteButton ;
          RelativeLayout mainlayout;
        public String getName(){
            return name1;
        }

    }
}
