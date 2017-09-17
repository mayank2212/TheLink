package org.thelink.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.thelink.R;

import java.util.ArrayList;

/**
 * Created by Mayank on 08-03-2017.
 */
public class CreatePlayListActivityAdapter extends ArrayAdapter<String> {


    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    public ArrayList<String> artistname;
    private ArrayList<String> datapaths = new ArrayList<>();
    private int size ;

    public CreatePlayListActivityAdapter(Context context, ArrayList<String> dataItem , ArrayList<String> dataPaths , ArrayList<String> martistname ) {
        super(context, R.layout.create_playlist_row, dataItem );
        this.context = context;
        this.data = dataItem;
        this.datapaths = dataPaths;
        this.artistname = martistname;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        CreatePlayList createPlayList;
        //    if (convertView == null) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.create_playlist_row, null);
        createPlayList = new CreatePlayList();
        createPlayList.name = (TextView)convertView.findViewById(R.id.text_view);
        createPlayList.artistname = (TextView)convertView.findViewById(R.id.textView22);


        convertView.setTag(createPlayList);

        createPlayList.name.setText(artistname.get(position));
        createPlayList.artistname.setText(data.get(position));

        return convertView;
    }


    class CreatePlayList {


        TextView artistname , name;

    }
}