package org.thelink.InviteActivity;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.thelink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mayank on 02-03-2017.
 */
public class FriendRequestAdapter extends ArrayAdapter<String> {


    private Context context;
    private ArrayList<String> number = new ArrayList<String>();
    public ArrayList<String> profilepics = new ArrayList<String>();
    ArrayList<String> name = new ArrayList<String>();

    public FriendRequestAdapter(Context context, ArrayList<String> mnumber  , ArrayList<String> pics , ArrayList<String> mname) {
        super(context, R.layout.list_content, mnumber  );
        this.number = mnumber;
        this.context = context;
        this.profilepics = pics ;
        this.name = mname;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final FriendRequestHolder friendRequestHolder;
      /*  if (convertView == null) {*/
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_content, null);
        friendRequestHolder = new FriendRequestHolder();
        friendRequestHolder.name = (TextView)convertView.findViewById(R.id.name);
        friendRequestHolder.number = (TextView)convertView.findViewById(R.id.number);
        friendRequestHolder.buttonaccept = (Button) convertView.findViewById(R.id.button3);
        friendRequestHolder.buttondecline = (Button) convertView.findViewById(R.id.button4);
        friendRequestHolder.hiddentextview = (TextView)convertView.findViewById(R.id.textView9);
        friendRequestHolder.photoview = (ImageView)convertView.findViewById(R.id.imageView_friend);

        convertView.setTag(friendRequestHolder);
        /*} else {
            followHolder = (FollowHolder) convertView.getTag();
        }*/

        friendRequestHolder.name.setText(name.get(position));
        friendRequestHolder.number.setText(number.get(position));

        Picasso.with(this.context).load(profilepics.get(position)).into(friendRequestHolder.photoview);


        friendRequestHolder.buttonaccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

             accept(number.get(position), PreferenceManager.getDefaultSharedPreferences(context).getString("Number", ""));

                 friendRequestHolder.buttondecline.setVisibility(View.GONE);
                friendRequestHolder.buttonaccept.setVisibility(View.GONE);
                friendRequestHolder.hiddentextview.setVisibility(View.VISIBLE);
                friendRequestHolder.hiddentextview.setText("Request Accepted");

            }
        });


        friendRequestHolder.buttondecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delete(number.get(position) ,  PreferenceManager.getDefaultSharedPreferences(context).getString("Number", ""));
                friendRequestHolder.buttondecline.setVisibility(View.GONE);
                friendRequestHolder.buttonaccept.setVisibility(View.GONE);
                friendRequestHolder.hiddentextview.setVisibility(View.VISIBLE);
                friendRequestHolder.hiddentextview.setText("Request Decline");
            }
        });
        return convertView;
    }


    private void accept(final String number, final String usernumber ){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/Accept.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String check = "record updated successfully";
                        if (response.contains(check)){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                //user khud ka number  dusre ka number
                params.put("number",number);
                params.put("user",usernumber);

                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void delete(final String number, final String usernumber){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://thelinkweb.com/un_friend.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Declined", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                //user khud ka number  dusre ka number
                params.put("number",number);
                params.put("user",usernumber);

                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public class FriendRequestHolder {


        TextView number , name , hiddentextview;
        Button buttonaccept ,buttondecline;
        ImageView photoview;


    }

}
