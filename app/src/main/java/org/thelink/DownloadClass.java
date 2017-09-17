package org.thelink;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Mayank on 14-02-2017.
 */
public class DownloadClass {
     private DownloadManager downloadManager;
    private Context context;
    String folderName = "thelink";
    Long reference;
    public static String downloadString = "http://www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=";

    public DownloadClass(Context context ){
        this.context = context;
    }

    public void DownloadMusic(String downloadurl , String song_name ){
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //downloadurl = downloadString+downloadurl;
        downloadurl = downloadurl.replaceAll(" " , "%20");
        downloadurl = downloadurl.replace("[", "%5B");
        downloadurl = downloadurl.replace("]", "%5D");
        Uri uri = Uri.parse(downloadurl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setTitle(song_name);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.allowScanningByMediaScanner();
        //request.setDestinationInExternalFilesDir(PlayListActivity.this, Environment.DIRECTORY_DOWNLOADS,
        //      File.separator + folderName + File.separator + song_name + ".mp3");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                File.separator + folderName + File.separator + song_name + ".mp3");
        reference = downloadManager.enqueue(request);

    }


}
