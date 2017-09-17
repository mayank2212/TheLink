package org.thelink.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.thelink.DownloadClass;
import org.thelink.R;

import java.util.ArrayList;

/**
 * Created by Mayank on 02-03-2017.
 */
public class PlayListClassAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    public ArrayList<String> artistname , donwloadlink;

    public PlayListClassAdapter(Context context, ArrayList<String> dataItem  , ArrayList<String> martistname, ArrayList<String> mdownlaodlink) {
        super(context, R.layout.playlistclassrow, dataItem  );
        this.data = dataItem;
        this.context = context;
        this.artistname = martistname ;
    this.donwloadlink = mdownlaodlink;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        PlaylistHolder playlistHolder;
      /*  if (convertView == null) {*/
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.playlistclassrow, null);
        playlistHolder = new PlaylistHolder();
        playlistHolder.songname = (TextView)convertView.findViewById(R.id.songnameplaylist);
       playlistHolder.artistname = (TextView)convertView.findViewById(R.id.artistnameplaylist);
        playlistHolder.downloadbutton = (ImageButton) convertView.findViewById(R.id.downloadbuttonplaylist);

        //      viewHolder.button.setBackgroundResource(R.drawable.invitebuttonbackground);
        convertView.setTag(playlistHolder);
        /*} else {
            followHolder = (FollowHolder) convertView.getTag();
        }*/

        playlistHolder.songname.setText(data.get(position));
        playlistHolder.artistname.setText(artistname.get(position));



        playlistHolder.downloadbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DownloadClass downloadClass =  new DownloadClass(context);
                downloadClass.DownloadMusic(donwloadlink.get(position) , data.get(position));
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return data.size();
    }



    class PlaylistHolder {
      TextView songname , artistname ;
        ImageButton downloadbutton ;

    }
}
