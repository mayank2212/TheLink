package org.thelink.MusicPlayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.thelink.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mayank on 19-07-2016.
 */
public class CustomListAdapter extends ArrayAdapter<String> {

    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListner(int position,String value , String name);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    public ArrayList<String> orig;
    private ArrayList<String> datapaths = new ArrayList<>();


    public CustomListAdapter(Context context, ArrayList<String> dataItem , ArrayList<String> dataPaths ) {
        super(context, R.layout.files_row, dataItem );
        this.data = dataItem;
        this.context = context;
       this.datapaths = dataPaths;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        SongsHolder songsHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.files_row, null);
            songsHolder = new SongsHolder();
            songsHolder.name = (TextView)convertView.findViewById(R.id.textView3);
            Typeface typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
            songsHolder.name.setTypeface(typeFace);
            songsHolder.imagebutton = (ImageButton) convertView.findViewById(R.id.imageButton);
           songsHolder.removebutton = ( ImageButton) convertView.findViewById(R.id.removesong);

            //      viewHolder.button.setBackgroundResource(R.drawable.invitebuttonbackground);
            convertView.setTag(songsHolder);
        } else {
            songsHolder = (SongsHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        final String tempname = getItem(position);

        songsHolder.name.setText(tempname);
        songsHolder.removebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, temp, tempname);

                }

            }
        });
        songsHolder.imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getContext(), temp, Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(datapaths.get(position));
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                context.startActivity(intent);
            }
        });


        return convertView;
    }


    public class SongsHolder {

        private String name1;
        TextView text , name;
        ImageButton imagebutton , removebutton;
        public String getName(){
            return name1;
        }

    }
}