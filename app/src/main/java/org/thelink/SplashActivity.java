package org.thelink;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.thelink.Following.FollowHelper;
import org.thelink.Home.Home_Download_Activity;
import org.thelink.Service.SongService;
import org.thelink.SyncAdapter.SyncDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class SplashActivity extends AppCompatActivity {
    ArrayList<String> allContacts = new ArrayList<String>();
    ArrayList<String> allnumbers = new ArrayList<>();
    private LoginManager_App loginManagerApp;
    private SwipeManager swipeManager;
    Uri Content_URI;
    FollowHelper helper;
    SyncDatabase syncDatabase;
    SQLiteDatabase db , db1 ;
    public ArrayList<String> database_songpath = new ArrayList<String>();
    public ArrayList<String> database_songname = new ArrayList<String>();
    SyncDatabase syncDatabasehelper;
    RelativeLayout relativeLayout ;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    ProgressBar progressBar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        //   String countryCodeValue = "us";
       if (!countryCodeValue.equals("in")) {

            Intent intent = new Intent(SplashActivity.this, Home_Download_Activity.class);
            startActivity(intent);
            finish();

        } else {
            //for indian user
            progressBar = (ProgressBar) findViewById(R.id.marker_progress);
            assert progressBar != null;
            progressBar.setVisibility(View.VISIBLE);
            try {

                getSupportActionBar().hide();
            } catch (Exception e) {


            }
            MessageDigest md = null;
            try {
                PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
                        getApplicationContext().getPackageName(),
                        PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                }
            } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {

            }
            assert md != null;
            Log.e("SecretKey = ", Base64.encodeToString(md.digest(), Base64.DEFAULT));


            String locale = SplashActivity.this.getResources().getConfiguration().locale.getDisplayCountry();
            TelephonyManager tm1 = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String countryCodeValues = tm1.getNetworkCountryIso();

            Log.e("ASdasdas", countryCodeValues);
            relativeLayout = (RelativeLayout) findViewById(R.id.splashlayout);

            oncreateCopysplash();
        }
   }

    public void oncreateCopysplash(){
        if(checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
            helper = new FollowHelper(this);
            db = helper.getWritableDatabase();
            syncDatabase = new SyncDatabase(this ) ;
            db1 = syncDatabase.getWritableDatabase();
            SplashAsync splashAsync = new SplashAsync();
            splashAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
   }

    private  boolean checkAndRequestPermissions() {
        int permissionContacts = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        int StoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readphonestate = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int accessnetworkstate = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int accesswifistate = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        int sendsms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int cahngewifistate = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
        int getaccounts = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        int readextextermalstorage = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            readextextermalstorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (StoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readphonestate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        } if (accessnetworkstate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        } if (accesswifistate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (sendsms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }  if (cahngewifistate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CHANGE_WIFI_STATE);
        } if (getaccounts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
        } if (readextextermalstorage != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }



        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE , PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET , PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE , PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_WIFI_STATE , PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.SEND_SMS , PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CHANGE_WIFI_STATE , PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.GET_ACCOUNTS , PackageManager.PERMISSION_GRANTED);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                }


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

                            ) {
                        Log.d(TAG, "sms & location services permission granted");
                        oncreateCopysplash();
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_WIFI_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)


                                ) {
                            showDialogOK("These Permission are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    System.exit(0);
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

                            new AlertDialog.Builder(SplashActivity.this)
                                    .setTitle("Permissions")
                                    .setCancelable(false)
                                    .setMessage("This Application needs some permission to function.")
                                    .setPositiveButton("Go to Setting", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                    Uri.fromParts("package", getApplicationContext().getPackageName(), null));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            System.exit(0);
                                        }
                                    })

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    private class SplashAsync extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            db1.execSQL("delete from " + SyncDatabase.TABLE_NAME);
          /*  db.execSQL("delete from" + FollowHelper.TABLE_NAME);*/
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            helper.deleteTable();
            database_songname.clear();
            database_songpath.clear();
            allContacts.clear();
            allnumbers.clear();


            long startnow;
            long endnow;

            startnow = android.os.SystemClock.uptimeMillis();
            ArrayList arrContacts = new ArrayList();

            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER;
            Cursor cursor = SplashActivity.this.getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,   ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.Contacts._ID}, selection, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                allContacts.add(contactName);
                contactNumber = contactNumber.replaceAll("\\s+","").replaceAll("-", "");
                if(contactNumber.length() == 10){
                    allnumbers.add(contactNumber);

                } else {
                    contactNumber = contactNumber.substring(Math.max(0, contactNumber.length() - 10));
                    allnumbers.add(contactNumber);
                }

                cursor.moveToNext();
            }
            cursor.close();
            cursor = null;

            endnow = android.os.SystemClock.uptimeMillis();
            Log.e("END", "TimeForContacts " + (endnow - startnow) + " ms");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            insertContacts insertContacts = new insertContacts();
            insertContacts.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            Intent intent1  = new Intent(SplashActivity.this , SongService.class);
            startService(intent1);
            SwipeManager prefManager = new SwipeManager(SplashActivity.this);
            prefManager.SetSwipeStatus(true);
            syncDatabase.GettingAllSongs();
            progressBar.setVisibility(View.GONE);

            loginManagerApp = new LoginManager_App(SplashActivity.this);
            if (!loginManagerApp.isLoggedIn()) {
                launchMainActivity();
                finish();
            }
            else {
                //change here to mainactivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
           }
        }
        private void launchMainActivity() {
        /*loginManagerApp.ISLoggedIn(false);*/
            Intent intent = new Intent(getApplicationContext() , Home_Download_Activity.class);
            startActivity(intent);
            finish();
        }
    }

    private class insertContacts extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... voids) {
            db.beginTransaction();
            for (int i = 0 ; i<allContacts.size() ; i++){
                helper.insertUSER(allContacts.get(i), allnumbers.get(i));
            }
            db.setTransactionSuccessful();
            db.endTransaction();

            return true;
        }
    }

}
