package org.thelink.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.thelink.Home.Home_Download_Activity;
import org.thelink.InviteActivity.FriendRequest;
import org.thelink.R;

import java.util.Map;
/**
 * Created by Mayank on 30-07-2016.
 */
public class MyFireBaseMessagingService extends FirebaseMessagingService {
    private Map<String, String> data;
    private static final String TAG="MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
    data=remoteMessage.getData();
    String message=data.get("message");
    String titledata=data.get("title");
    String name=data.get("name");
    String number=data.get("number");
    String profile=data.get("profile");
    String Accept = null;
    if(data.containsKey("accept")) {
        Accept = data.get("accept");
        Accept_Notification(titledata,message);
    }

    //if(Accept==null) {
      if(data.containsKey("friendrequest")){
        Database db = new Database(this);
        db.entry(name, number,profile, 0);
        sendNotification(titledata,message);
    }

        if(data.containsKey("manualnoti")){
            ManualNotification(titledata , message);
        }



    Log.d(TAG, "From: " + remoteMessage.getFrom());
    Log.d(TAG, "From: " + remoteMessage.getData());
    Log.d(TAG, "name: " + name);
    Log.d(TAG, "number: " + number);
    Log.d(TAG, "profile:" + profile);
}

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, FriendRequest.class);
        Bundle bundle = new Bundle();
        bundle.putString("message", messageBody);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //creating custom action button
//        Intent actionIntent = new Intent(this, ActionActivity.class);
//        PendingIntent actionPendingIntent =
//                PendingIntent.getActivity(this, 0, actionIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);

//        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,"custom action",actionPendingIntent).build();

        //creating notification here
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notificationlogo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
//                .addAction(action)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void Accept_Notification(String title, String messageBody) {
        Intent intent = new Intent(this, Home_Download_Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("message", messageBody);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //creating custom action button
//        Intent actionIntent = new Intent(this, ActionActivity.class);
//        PendingIntent actionPendingIntent =
//                PendingIntent.getActivity(this, 0, actionIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);

//        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,"custom action",actionPendingIntent).build();

        //creating notification here
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notificationlogo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
//                .addAction(action)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void ManualNotification(String title , String messageBody){

        Intent intent = new Intent(this, Home_Download_Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("message", messageBody);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //creating custom action button
//        Intent actionIntent = new Intent(this, ActionActivity.class);
//        PendingIntent actionPendingIntent =
//                PendingIntent.getActivity(this, 0, actionIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);

//        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,"custom action",actionPendingIntent).build();

        //creating notification here
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notificationlogo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
//                .addAction(action)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}