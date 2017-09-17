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
 * Created by Mayank on 12-02-2017.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private Context mContext;
    private List<String> name  , number , Download_url , image;


    public FriendsAdapter(Context context, List<String> friend_name , List<String> friend_number  , List<String> image_url){
        mContext = context;
        name = friend_name;
        number = friend_number;
        image = image_url;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView mCardView;
        public TextView mTextView ,mNumberView;
        public ImageView imageView;


        public ViewHolder(View v){
            super(v);
            // Get the widget reference from the custom layout
            mCardView = (CardView) v.findViewById(R.id.friend_card_view);
            mTextView = (TextView) v.findViewById(R.id.friend_name);
            imageView = (ImageView)v.findViewById(R.id.friends_image);
            mNumberView = (TextView)v.findViewById(R.id.friend_number);
        }
    }


    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.friends_row,parent,false);
        ViewHolder vh = new ViewHolder(v);

        // Return the ViewHolder
        return vh;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        // Get the current color from the data set
        String current = name.get(position);
        holder.mTextView.setText(current);
        String current_pics = image.get(position);
        Picasso.with(mContext).load(current_pics)
                /*.error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)*/
                .into(holder.imageView);
        String current_number = number.get(position);
        holder.mNumberView.setText(current_number);

        // Set a click listener for CardView
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Toast.makeText(mContext, " User Number:  " + number.get(position), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext , FriendsDetail.class);
                intent.putExtra("number" , number.get(position));
                intent.putExtra("name" , name.get(position) );
                intent.putExtra("profilepicture", image.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return name.size();
    }
}
