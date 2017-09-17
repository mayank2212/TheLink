package org.thelink.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mayank on 25-09-2016.
 */
public class NetWatcher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);


        switch (status){
            case "Wifi enabled" :
              intent = new Intent(context, SongService.class);
                context.startService(intent);

                break;
            case "Mobile data enabled":
                intent = new Intent(context, SongService.class);
                context.stopService(intent);
                break;

            case "Not connected to Internet" :
                intent = new Intent(context, SongService.class);
                context.stopService(intent);
                break;
        }
    }
}