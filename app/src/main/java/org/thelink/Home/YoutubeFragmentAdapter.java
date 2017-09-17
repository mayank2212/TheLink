package org.thelink.Home;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.thelink.DownloadClass;
import org.thelink.PlayMusicClass;
import org.thelink.R;

import java.util.List;

/**
 * Created by Mayank on 16-02-2017.
 */
public class YoutubeFragmentAdapter extends RecyclerView.Adapter<YoutubeFragmentAdapter.ViewHolder> {
    private Context mContext;
    private List<String> songname , artistname , downloadlink;
    public static String downloadString = "http://www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=";


    public YoutubeFragmentAdapter(Context context, List<String> list , List<String> artist_name , List<String> download_link){
        mContext = context;
        songname = list;
        artistname= artist_name;
        downloadlink = download_link;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView mCardView;
        public TextView mTextView , mArtistTextview;
        public ImageButton imageButtonDownload , imageButtonPlay;

        public ViewHolder(View v){
            super(v);
            // Get the widget reference from the custom layout
            mCardView = (CardView) v.findViewById(R.id.card_view_youtubefragment);
            mTextView = (TextView) v.findViewById(R.id.youtubefragmentsongname);
            mArtistTextview = (TextView)  v.findViewById(R.id.youtubefragmentartist);
            imageButtonPlay = (ImageButton)v.findViewById(R.id.youtubefragmentplay);
            imageButtonDownload = (ImageButton)v.findViewById(R.id.youtubefragmentdownload);
            //  hiddentext = (TextView)v.findViewById(R.id.)
        }
    }
    @Override
    public YoutubeFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.youtubefragmenrow,parent,false);
        ViewHolder vh = new ViewHolder(v);

        // Return the ViewHolder
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        // Get the current color from the data set
        String current = songname.get(position);
        holder.mTextView.setText(current);
        String current_artist = artistname.get(position);
        holder.mArtistTextview.setText(current_artist);
        holder.imageButtonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadClass downloadClass = new DownloadClass(mContext);
                downloadClass.DownloadMusic(downloadString+downloadlink.get(position), songname.get(position));
            }
        });

        holder.imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    Toast.makeText(mContext, downloadString+downloadlink.get(position), Toast.LENGTH_LONG).show();
                PlayMusicClass playMusicClass = new PlayMusicClass(mContext);
                playMusicClass.playMusic(downloadString + downloadlink.get(position));
            }
        });
        // Set a click listener for CardView
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

    @Override
    public int getItemCount(){
        // Count the items
        //textViewNoData.setVisibility(songname.size() > 0 ? View.GONE : View.VISIBLE);
        return songname.size();
    }
}