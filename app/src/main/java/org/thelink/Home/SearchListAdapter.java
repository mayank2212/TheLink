package org.thelink.Home;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.thelink.R;

import java.util.ArrayList;

/**
 * Created by Mayank on 19-02-2017.
 */
public class SearchListAdapter extends ArrayAdapter<String> {


    customButtonListener_search customListner;

    public interface customButtonListener_search {
        public void onButtonClickListner(int position,String value , String name);
    }

    public void setCustomButtonListner(customButtonListener_search listener) {
        this.customListner = listener;
    }


    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    public ArrayList<String> songpath;

    public SearchListAdapter(Context context, ArrayList<String> dataItem  , ArrayList<String> song_path) {
        super(context, R.layout.search_list_row, dataItem  );
        this.data = dataItem;
        this.context = context;
        this.songpath = song_path ;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        FollowHolder followHolder;
      /*  if (convertView == null) {*/
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.search_list_row, null);
        followHolder = new FollowHolder();
        followHolder.name = (TextView)convertView.findViewById(R.id.search_name);
        followHolder.name.setSelected(true);
        Typeface typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        followHolder.name.setTypeface(typeFace);
        followHolder.button = (ImageButton) convertView.findViewById(R.id.search_button);

        //      viewHolder.button.setBackgroundResource(R.drawable.invitebuttonbackground);
        convertView.setTag(followHolder);
        /*} else {
            followHolder = (FollowHolder) convertView.getTag();
        }*/

        followHolder.name.setText(data.get(position));
        /*followHolder.photoview*/
        followHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, data.get(position), songpath.get(position));

                }

            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return 10;
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

