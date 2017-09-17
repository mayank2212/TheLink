package org.thelink.Profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
 * Created by Mayank on 28-02-2017.
 */
public class PlaylistSongsAdapter  extends ArrayAdapter<String> {


    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    public ArrayList<String> download_url;
    private ArrayList<String> datapaths = new ArrayList<>();
    private int size ;
    newDbHelper dbHelper;
    String Playlistname  ;

    public PlaylistSongsAdapter(Context context, ArrayList<String> dataItem , ArrayList<String> dataPaths , ArrayList<String> mdownload_url , int msize , String mplaylistname  ) {
        super(context, R.layout.playlistsongsrow, dataItem );
        this.context = context;
        this.data = dataItem;
        this.datapaths = dataPaths;
        this.download_url = mdownload_url;
        this.size =  msize;
        this.Playlistname = mplaylistname;
        dbHelper = new newDbHelper(context);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        SongsHolder songsHolder;
    //    if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.playlistsongsrow, null);
            songsHolder = new SongsHolder();
            songsHolder.name = (TextView)convertView.findViewById(R.id.playlist_songname);
            songsHolder.artistname = (TextView)convertView.findViewById(R.id.artistname);
            songsHolder.button = (Button) convertView.findViewById(R.id.deletebutton);

            if(position<=size-1){
                songsHolder.button.setText("Delete");
                songsHolder.button.setTextColor(Color.RED);
                songsHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       
                      final AlertDialog alertDialog = new AlertDialog.Builder(context).create(); //Read Update
                        alertDialog.setTitle("Delete Song ");
                        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                            @Override

                            public void onClick(DialogInterface dialog, int which) {


                                Toast toast = Toast.makeText(context, "Song Removed from Playlist...", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                DeleteUserPlayListSong(Playlistname, data.get(position));

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

            } else if(position>= size-1){

                songsHolder.button.setText("ADD");
                songsHolder.button.setTextColor(Color.BLACK);
                songsHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Toast toast = Toast.makeText(context,  data.get(position) +" ADDED TO PLAYLIST", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        dbHelper.entry(data.get(position) , datapaths.get(position),Playlistname,0 , datapaths.get(position));
                        
                    }
                });

            }

            convertView.setTag(songsHolder);
      /*  } else {
            songsHolder = (SongsHolder) convertView.getTag();
        }*/

        songsHolder.name.setText(data.get(position));
        songsHolder.artistname.setText(datapaths.get(position));




        return convertView;
    }


    private void DeleteUserPlayListSong(final String name , final String songname ){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/delete_song_user_playlist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);
                    /*  PlayListSongs playListSongs =  new PlayListSongs();
                        playListSongs.GettingLocalSongs(Playlistname);*/
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

                params.put("number", PreferenceManager.getDefaultSharedPreferences(context).getString("Number", ""));
                params.put("playlist",name);
                params.put("song",songname);

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

     class SongsHolder {

        private String name1;
        TextView artistname , name;
        Button button , removebutton;
        public String getName(){
            return name1;
        }

    }
}