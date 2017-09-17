package org.thelink.Home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.thelink.R;

import java.util.List;

/**
 * Created by Mayank on 12-02-2017.
 */
public class FriendsDetail_Adapter extends RecyclerView.Adapter<FriendsDetail_Adapter.ViewHolder> {
    private Context mContext;
    private List<String> playlistname , likes , status;
   String usernumber ;

    public FriendsDetail_Adapter(Context context, List<String> list , List<String> albumpics, String number , List<String> mstatus){
        mContext = context;
        playlistname = list;
        likes= albumpics;
        usernumber = number;
        status = mstatus;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
    public CardView mCardView;
    public TextView mTextView , mLikeTextView;
    public ImageButton imageButton;

    public ViewHolder(View v){
        super(v);
        // Get the widget reference from the custom layout
        mCardView = (CardView) v.findViewById(R.id.card_view_detailfriend);
        mTextView = (TextView) v.findViewById(R.id.friend_detail_songname);
        mLikeTextView = (TextView) v.findViewById(R.id.friend_detail_like);
        imageButton = (ImageButton)v.findViewById(R.id.imageButton3);
        //  hiddentext = (TextView)v.findViewById(R.id.)
    }
}

    /*@Override
    public int getItemViewType(int position) {
       *//* if(playlistname.contains(likedplaylist.get(position))){
            return LIKED_ROW;
        } else{
            return UNLIKE_ROW;
        }*//*
    }*/

    @Override
    public FriendsDetail_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){


        View v = LayoutInflater.from(mContext).inflate(R.layout.detail_friends_row,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        // Get the current color from the data set
        final String current = playlistname.get(position);
        holder.mTextView.setText(current);
        String currentlikes = likes.get(position);
        holder.mLikeTextView.setText("Likes : " +currentlikes);
        if(status.get(position).equals("1")){
            holder.imageButton.setBackgroundResource(R.drawable.liked);
            //Toast.makeText(mContext, "unliked", Toast.LENGTH_LONG).show();

        }else {
            holder.imageButton.setVisibility(View.GONE);
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, playlistname.get(position), Toast.LENGTH_LONG).show();
                    //calling two php like.php and liked_playlist
                  /*  CallingLikePlayList(current);
                    CallingLiked_PlayList(current);
*/
                }
            });
        }
        // Set a click listener for CardView
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //to get that playlist songs
                Intent intent = new Intent(mContext, PlayListClass.class);
                intent.putExtra("PlayListname" , playlistname.get(position));
                intent.putExtra("number" , usernumber);
                intent.putExtra("status" , status.get(position));
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount(){
        // Count the items
        //textViewNoData.setVisibility(songname.size() > 0 ? View.GONE : View.VISIBLE);
        return playlistname.size();
    }

}