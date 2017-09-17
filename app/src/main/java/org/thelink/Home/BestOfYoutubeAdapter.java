package org.thelink.Home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thelink.R;

import java.util.List;

/**
 * Created by Mayank on 10-02-2017.
 */
public class BestOfYoutubeAdapter extends RecyclerView.Adapter<BestOfYoutubeAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mDataSet , mAlbumPics;


    public BestOfYoutubeAdapter(Context context, List<String> list , List<String> albumpics){
        mContext = context;
        mDataSet = list;
        mAlbumPics= albumpics;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView mCardView;
        public TextView mTextView;
        public ImageView imageView;

        public ViewHolder(View v){
            super(v);
            // Get the widget reference from the custom layout
            mCardView = (CardView) v.findViewById(R.id.card_view);
            mTextView = (TextView) v.findViewById(R.id.tv);
            imageView = (ImageView)v.findViewById(R.id.album_pic);
        }
    }

    @Override
    public BestOfYoutubeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.download_row,parent,false);
        ViewHolder vh = new ViewHolder(v);

        // Return the ViewHolder
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        // Get the current color from the data set
        String current = mDataSet.get(position);
        holder.mTextView.setText(current);
        String current_pics = mAlbumPics.get(position);
        Picasso.with(mContext).load(current_pics)
                /*.error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)*/
                .into(holder.imageView);

        // Set a click listener for CardView
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, YoutubeClass.class);
                intent.putExtra("channel_name" , mDataSet.get(position));
                intent.putExtra("image" , mAlbumPics.get(position));
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount(){
        // Count the items
        return mDataSet.size();
    }
}