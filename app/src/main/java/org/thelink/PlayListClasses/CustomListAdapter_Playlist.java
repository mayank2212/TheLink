package org.thelink.PlayListClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import org.thelink.R;

import java.util.ArrayList;

/**
 * Created by Mayank on 21-07-2016.
 */
public class CustomListAdapter_Playlist extends ArrayAdapter<String> implements Filterable {

    customButtonListener_playlist customListner;



    public interface customButtonListener_playlist {
        public void onButtonClickListner(int position,String value , String name);
    }

    public void setCustomButtonListner(customButtonListener_playlist listener) {
        this.customListner = listener;
    }


    private ArrayList<String> firstName;
    private ArrayList<String> lastName;

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    public ArrayList<String> path;
    private ArrayList<String> filteredList;
    private ArrayList<String> filteredListforpath;


 public CustomListAdapter_Playlist(Context context ,  ArrayList<String> dataItem  ,ArrayList<String> path  ){
     super(context, R.layout.playlist_row, dataItem );
     this.data = dataItem;
     this.filteredList = dataItem ;
     this.path = path;
     this.filteredListforpath = path;
     this.context = context;

     getFilter();
 }

    @Override
    public int getCount() {
        return filteredList.size();
    }
    @Override
    public String getItem(int i) {
        return filteredList.get(i);
    }
    @Override
    public int getPosition(String item) {
        return super.getPosition(item);
    }
    @Override
    public Filter getFilter() {
        return myFilter;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        PlayListHolder playListHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.playlist_row, null);
            playListHolder = new PlayListHolder();
            playListHolder.name = (TextView)convertView.findViewById(R.id.songname);
            Typeface typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
            playListHolder.name.setTypeface(typeFace);
            playListHolder.imagebutton = (ImageButton) convertView.findViewById(R.id.imageButton_download);
          //  playListHolder.removebutton = (ImageButton) convertView.findViewById(R.idd.)


            //      viewHolder.button.setBackgroundResource(R.drawable.invitebuttonbackground);
            convertView.setTag(playListHolder);
        } else {
            playListHolder = (PlayListHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        final String tempname = getItem(position);
        playListHolder.name.setText(tempname);
        playListHolder.imagebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, filteredList.get(position), filteredListforpath.get(position));

                }
              ///  Toast.makeText(context, filteredList.get(position) + " and " + filteredListforpath.get(position), Toast.LENGTH_LONG).show();
            }
        });
        return convertView;
    }


    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<String> tempList=new ArrayList<>();
            ArrayList<String> tempListnumber=new ArrayList<>();
            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if(constraint != null && constraint.length()>0) {
                int length=data.size();
                int i=0;
                while(i<length){

                    String item=data.get(i);
                    if(item.toLowerCase().contains(constraint.toString().toLowerCase())){
                        tempList.add(item);
                        tempListnumber.add(path.get(i));
                    }
                    //do whatever you wanna do here
                    //adding result set output array

                    i++;
                }
                //following two lines is very important
                //as publish result can only take FilterResults objects

                filterResults.values = tempList;
                filteredListforpath=tempListnumber;
                filterResults.count = tempList.size();
            }else {
                filterResults.count = data.size();
                filteredListforpath=path;
                filterResults.values = data;

            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence contraint, Filter.FilterResults results) {
            filteredList = (ArrayList<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public class PlayListHolder {

        private String name1;
        TextView text , name;

        ImageButton imagebutton , removebutton;
        public String getName(){
            return name1;
        }

    }

}
