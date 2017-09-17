package org.thelink.Service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.thelink.Profile.newDbHelper;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mayank on 17-08-2016.
 */


public class SongService extends Service {

    private String REGISTER_URL_FOLLOWER = "http://thelinkweb.com/insertsongs_userplaylist.php";
    private String SERVER_URL = "http://thelinkweb.com/songuploadtry.php";
    // private String SERVER_URL =" http://thelinkweb.com/upload123.php";

    static String UploadedPath;
    static int i;

    /////////////////////////////
    public ArrayList<String> selectedSongName , selectedSongPath , selectedPlaylist  , selectedStatus ,selectedArtistName ;
    newDbHelper dbHelper;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("songservice", "service is started.......... ");

        //selected
        selectedSongName = new ArrayList<>();
        selectedSongPath = new ArrayList<>();
        selectedPlaylist = new ArrayList<>();
        selectedStatus = new ArrayList<>();
        selectedArtistName =  new ArrayList<>();

        dbHelper = new newDbHelper(getApplicationContext());
        Getting_Selectedsongs();

    }


    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e("service", "service is already running");

    }

    private void Getting_Selectedsongs() {

        Cursor res= dbHelper.getall();
        if(res.getCount()==0){
            return;
        }
        while(res.moveToNext()) {

            selectedSongName.add(res.getString(1));
            selectedSongPath.add(res.getString(2));
            selectedPlaylist.add(res.getString(3));
            selectedStatus.add(res.getString(4));
            selectedArtistName.add(res.getString(5));
        }

        Log.e("Asdasdas" , selectedSongName.size() + "   ") ;
        if (selectedSongName.size() == 0) {

            onDestroy();
        } else {
            Upload upload = new Upload();
            upload.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        }

    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.e("Service tag", "ondestroy");
    }


    private class Upload extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        public int uploadFile(final String selectedFilePath) {

            int serverResponseCode = 0;

            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";


            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File selectedFile = new File(selectedFilePath);


            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length - 1];

            try {

                Log.e("selected file path ", selectedFilePath);
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                System.setProperty("http.keepAlive", "false");
                connection.setRequestProperty("Connection", "close");
                //connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);


                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);


                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.e("TAG", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK

                if (serverResponseCode == 200) {

                    UploadedPath = "http://thelinkweb.com/uploads/" + fileName;

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL_FOLLOWER,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //Toast.makeText(Follow_Activity.this, response, Toast.LENGTH_LONG).show();
                                    Log.e("response", response);
                                    dbHelper.Delete_Row(selectedSongName.get(i) , selectedPlaylist.get(i));
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("volley error", error.toString());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("number", PreferenceManager.getDefaultSharedPreferences
                                    (getApplicationContext()).getString("Number", ""));
                            params.put("song", selectedSongName.get(i));
                            params.put("download_url", UploadedPath);
                            params.put("artist", selectedArtistName.get(i));
                            params.put("playlist" ,selectedPlaylist.get(i));
                            return params;
                        }

                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            5000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }
                Log.e("above closing ", " fileinputstreamclosing");
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();

                {
                    Log.e("File Not Found", "");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();

            }
            return serverResponseCode;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            for (i = 0; i < selectedSongName.size(); i++) {
                uploadFile(selectedSongName.get(i));
            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

        }
    }
}
