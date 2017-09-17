package org.thelink.Home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.thelink.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mayank on 24-02-2017.
 */
public class MoreChannelDailog  {
    public static final String FEEDBACK_URL = "http://www.thelinkweb.com/feedback.php";
    public Context context;


    public MoreChannelDailog(Context mcontext){
        this.context = mcontext;

    }

    public void show_dailog(final Activity activity){


        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.morechanneldialog);

        final EditText morechannel = (EditText)dialog.findViewById(R.id.morechannel);

        Button submitbutton = (Button) dialog.findViewById(R.id.submitbutton);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(morechannel.getText().toString().length() > 0)) {
                    Toast.makeText(context, "Please enter your suggestion first..", Toast.LENGTH_LONG).show();
                } else {
                    sendFeedback(PreferenceManager.getDefaultSharedPreferences(activity).getString("Name" , "")
                            , PreferenceManager.getDefaultSharedPreferences(activity).getString("Number", ""), morechannel.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        Button dialogButton = (Button) dialog.findViewById(R.id.dismissbutton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendFeedback(final String username , final String usernumber ,final String feedback  ){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FEEDBACK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();
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
                params.put("name",username);
                params.put("number",usernumber);
                params.put("feedback",feedback);

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
}
