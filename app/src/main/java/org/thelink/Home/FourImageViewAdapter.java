package org.thelink.Home;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thelink.DownloadClass;
import org.thelink.PlayMusicClass;
import org.thelink.R;

import java.util.List;

/**
 * Created by Mayank on 10-02-2017.
 */
public class FourImageViewAdapter extends RecyclerView.Adapter<FourImageViewAdapter.ViewHolder_Tgif> {
    private Context mcontext;
    private List<String> name , martist_name , pictures , mdownload_url;
    private int lastPosition = -1;
    public static String downloadString = "http://www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=";

    public FourImageViewAdapter(Context context, List<String> name, List<String> artist_name, List<String> pictures, List<String> download_url) {
        this.mcontext = context;
        this.name = name;
        this.martist_name = artist_name;
        this.pictures = pictures;
        this.mdownload_url = download_url;
    }


    public static class ViewHolder_Tgif extends RecyclerView.ViewHolder{
        public CardView mCardView;
        public TextView mTextView ,mArtistName;
        public ImageView imageView;
        public ImageButton imageButton , play_imagebutton;


        public ViewHolder_Tgif(View v){
            super(v);
            // Get the widget reference from the custom layout
            mCardView = (CardView) v.findViewById(R.id.card_view);
            mTextView = (TextView) v.findViewById(R.id.textView13);
            imageView = (ImageView)v.findViewById(R.id.imageView6);
            mArtistName = (TextView)v.findViewById(R.id.textView14);
            imageButton = (ImageButton)v.findViewById(R.id.imageButton2);
            play_imagebutton = (ImageButton)v.findViewById(R.id.imageButton4);
        }
    }

    @Override
    public FourImageViewAdapter.ViewHolder_Tgif onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mcontext).inflate(R.layout.trendingsong_row,parent,false);
        ViewHolder_Tgif vh = new ViewHolder_Tgif(v);

        // Return the ViewHolder
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder_Tgif holder, final int position){
        // Get the current color from the data set
        String current = name.get(position);
        holder.mTextView.setText(current);
        String current_pics = pictures.get(position);
        Picasso.with(mcontext).load(current_pics)
                .into(holder.imageView);
        String artist_name = martist_name.get(position);
        holder.mArtistName.setText(artist_name);

        // Set a click listener for CardView
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownloadClass downloadClass = new DownloadClass(mcontext);
                downloadClass.DownloadMusic(downloadString+mdownload_url.get(position) , name.get(position));
            }
        });
        final MediaPlayer mediaPlayer = new MediaPlayer();
       /// setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        holder.play_imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               // Toast.makeText(mcontext, "Clicked..........." , Toast.LENGTH_LONG).show();
               PlayMusicClass playMusicClass = new PlayMusicClass(mcontext);
                playMusicClass.playMusic(downloadString+mdownload_url.get(position));



            }
        });
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {

        return name.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mcontext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;

        }
    }
}
