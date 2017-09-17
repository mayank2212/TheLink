package org.thelink.InviteActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thelink.Following.FollowHelper;
import org.thelink.R;

import java.util.ArrayList;

/**
 * Created by Mayank on 12-07-2016.
 */
public class ListAdapter extends ArrayAdapter<String> {
    customButtonListener customListner;
    FollowHelper  helper ;
    SQLiteDatabase db ;
    public static final int TYPE_NO_REQUEST = 0;




    private int lastPosition = -1;
    public interface customButtonListener {
        public void onButtonClickListner(int position,String value , String name , String status);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    public ArrayList<String> orig, status  , number;
    public ListAdapter(Context context, ArrayList<String> dataItem , ArrayList<String> pics , ArrayList<String> mstatus ,  ArrayList<String> mnumber ) {
        super(context, R.layout.pending_row, dataItem );
        this.data = dataItem;
        this.context = context;

       this.orig = pics ;
        this.status = mstatus;
        this.number = mnumber;

    }
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
      //  Log.e("size" , " " + helper.follow_invite_name.size());
        if(!status.isEmpty()) {
            if (status.get(position).equals("2")) {
                //pending
                return 1;
            } else
                //no request
                return 0;
            }

        return 0;
    }

    @Override
    public int getCount() {
       if(!data.isEmpty()) {
           return data.size();
       }else
           return data.size()-1;
    }
    /*@Override
    public int getItemViewType(int position) {
        return (contactList.get(position).getContactType() == ContactType.CONTACT_WITH_IMAGE) ? 0 : 1;
    }*/


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int theType = getItemViewType(position);
  /*      if (convertView == null) {*/

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
        //no request
            if(theType == 0 ){

                convertView = inflater.inflate(R.layout.invite_follow_row, null);
                assert convertView != null;
                viewHolder.name = (TextView) convertView.findViewById(R.id.textView6);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView2);
                viewHolder.number = (TextView)convertView.findViewById(R.id.textView18);
                viewHolder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.inviterow);
                viewHolder.button = (Button) convertView.findViewById(R.id.childButton);
                viewHolder.button.setBackground(context.getResources().getDrawable(R.drawable.follow_buttonbackground));
                    Picasso.with(this.context).cancelRequest(viewHolder.imageView);
                    Picasso.with(this.context).load(orig.get(position)).into(viewHolder.imageView);
                    convertView.setTag(viewHolder);

            }
            //pending
            else if( theType == 1)  {

                convertView = inflater.inflate(R.layout.pending_row, null);
                assert convertView != null;
                viewHolder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.pendingrow);
                viewHolder.name = (TextView) convertView.findViewById(R.id.textView6);
                viewHolder.number = (TextView)convertView.findViewById(R.id.textView18);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView2);
                Picasso.with(this.context).cancelRequest(viewHolder.imageView);
                Picasso.with(this.context).load(orig.get(position)).into(viewHolder.imageView);
                viewHolder.button = (Button) convertView.findViewById(R.id.childButton);

                convertView.setTag(viewHolder);
            }



        final String tempname = getItem(position);
        final String tempnumber = number.get(position);
        final String currentStatus = status.get(position);
        viewHolder.number.setText(tempnumber);
        viewHolder.name.setText(tempname);

        viewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, tempnumber, tempname,currentStatus);
                }

            }
        });

        return convertView;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public class ViewHolder {
        private String name1;
        TextView text , name , number;
        Button button;
        ImageView imageView ;
        RelativeLayout relativeLayout;
        public String getName(){
            return name1;
        }
    }
}