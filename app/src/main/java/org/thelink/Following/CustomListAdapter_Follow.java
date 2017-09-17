package org.thelink.Following;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thelink.R;

import java.util.ArrayList;

/**
 * Created by Mayank on 21-07-2016.
 */
public class CustomListAdapter_Follow extends ArrayAdapter<String> {


    customButtonListener_follow customListner;

    public interface customButtonListener_follow {
        public void onButtonClickListner(int position,String value , String name);
    }

    public void setCustomButtonListner(customButtonListener_follow listener) {
        this.customListner = listener;
    }


    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    public ArrayList<String> profilepics;
    public CustomListAdapter_Follow(Context context, ArrayList<String> dataItem  , ArrayList<String> pics) {
        super(context, R.layout.follow_row, dataItem  );
        this.data = dataItem;
        this.context = context;
        this.profilepics = pics ;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        FollowHolder followHolder;
      /*  if (convertView == null) {*/
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.follow_row, null);
            followHolder = new FollowHolder();
            followHolder.name = (TextView)convertView.findViewById(R.id.follow_name);
            Typeface typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
            followHolder.name.setTypeface(typeFace);
            followHolder.button = (ImageButton) convertView.findViewById(R.id.show_playlist);
            followHolder.photoview = (ImageView)convertView.findViewById(R.id.follow_photo);
            //      viewHolder.button.setBackgroundResource(R.drawable.invitebuttonbackground);
            convertView.setTag(followHolder);
        /*} else {
            followHolder = (FollowHolder) convertView.getTag();
        }*/
        final String temp = getItem(position);
        final String tempname = getItem(position);
        followHolder.name.setText(tempname);
        /*followHolder.photoview*/
        Picasso.with(this.context).load(profilepics.get(position)).into(followHolder.photoview);
        followHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, temp, tempname);

                }

            }
        });
        return convertView;
    }


    public class FollowHolder {

        private String name1;
        TextView text , name;
        ImageButton button;
        ImageView photoview;
        public String getName(){
            return name1;
        }

    }
}
