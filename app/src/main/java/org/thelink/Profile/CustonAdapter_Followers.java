package org.thelink.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thelink.R;

import java.util.ArrayList;

/**
 * Created by Mayank on 22-08-2016.
 */
public class CustonAdapter_Followers extends ArrayAdapter<String> {

    customButtonListener_foollwers customListner;

    public interface customButtonListener_foollwers {
        public void onButtonClickListner(int position,String value , String name);
    }

    public void setCustomButtonListner(customButtonListener_foollwers listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    public ArrayList<String> orig , number;


    public CustonAdapter_Followers(Context context, ArrayList<String> dataItem  , ArrayList<String> pics , ArrayList<String> m_number) {
        super(context, R.layout.follow_row, dataItem );
        this.data = dataItem;
        this.context = context;
        this.orig = pics;
        this.number = m_number;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        FollowersHolder followHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.followers_row, null);
            followHolder = new FollowersHolder();
            followHolder.name = (TextView)convertView.findViewById(R.id.followers_name);
            followHolder.imageView = (ImageView)convertView.findViewById(R.id.follow_photo);
            followHolder.number = (TextView)convertView.findViewById(R.id.textView20);
            followHolder.button = (Button) convertView.findViewById(R.id.show_followers);

            //      viewHolder.button.setBackgroundResource(R.drawable.invitebuttonbackground);
            convertView.setTag(followHolder);
        } else {
            followHolder = (FollowersHolder) convertView.getTag();
        }

        final String tempname = getItem(position);
        final String currentno = number.get(position);
        followHolder.number.setText(currentno);
        followHolder.name.setText(tempname);
        Picasso.with(this.context).load(orig.get(position)).into(followHolder.imageView);
        followHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, currentno, tempname);

                }

            }
        });
        return convertView;
    }

    public class FollowersHolder {

        private String name1;
        TextView number , name;
        ImageView imageView;
        Button button;
        public String getName(){
            return name1;
        }

    }
}
