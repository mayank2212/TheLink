package org.thelink.Following;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Mayank on 20-07-2016.
 */
public class FollowHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=3;
    private static final String DATABASE_NAME = "followinglist";
    static String clicked_number ;

    Context mcontext ;
    public static String TABLE_NAME= "follow_table" ,
            UID = "uid " ,
            USER_NAME = "username " ,
            NUMBER= "number";
    public static String TABLE_NAME_FOLLOW = "databasetable_follow" ,
    NUMBER_FOLLOW = "number_follow" ,
    PROFILE_PICTURE="profile" ;

    static public String TABLE_NAME_INSTALLED= "installed_user" ,
    NUMBER_INVITE ="number_invite" ,
    PROFILE_INVITE="profile_invite";

    static String CREATE_TABLE = " CREATE TABLE  " + TABLE_NAME + " ( " +  UID + "  INTEGER PRIMARY KEY  " + "  AUTOINCREMENT  , "
            +  USER_NAME + " VARCHAR(20)  "  + "  ,  " + NUMBER + " VARCHAR(20)  "  +  " ) ; ";
    static  String CREATE_TABLE_FOLLOW = " CREATE TABLE  " + TABLE_NAME_FOLLOW + " ( "
            +  NUMBER_FOLLOW + " VARCHAR(20)  "  + "  ,  " + PROFILE_PICTURE + " VARCHAR(200)  "  +  " ) ; ";

    static  String CREATE_TABLE_INSTALLED = " CREATE TABLE  " + TABLE_NAME_INSTALLED + " ( "
            +  NUMBER_INVITE + " VARCHAR(20)  "  + "  ,  " + PROFILE_INVITE + " VARCHAR(200)  "  +  " ) ; ";


    SQLiteDatabase db ;
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<String> follow_names = new ArrayList<>();
    public ArrayList<String> follower_name = new ArrayList<>();
    public ArrayList<String> numbers;
    public ArrayList<String> contacts_name =  new ArrayList<>();
    public ArrayList<String> databasepics =  new ArrayList<>();
    public ArrayList<String> invitenumber = new ArrayList<>();
    public ArrayList<String> invitepics = new ArrayList<>();
    public static ArrayList<String> followpics = new ArrayList<>();

    public ArrayList<String> follow_number = new ArrayList<>();
    public FollowHelper(Context mcontext ){
        super(mcontext, DATABASE_NAME, null, DATABASE_VERSION);
        this.mcontext = mcontext;
        db = getWritableDatabase();

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_FOLLOW);
        db.execSQL(CREATE_TABLE_INSTALLED);
        Log.e("tale created" , "");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME_FOLLOW);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME_INSTALLED);

        Toast.makeText(mcontext, "ON UPGRADE " + oldVersion + " " + newVersion , Toast.LENGTH_LONG).show();
        onCreate(db);
    }

    public long insertUSER ( String n , String p ){
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME, n);
        cv.put(NUMBER, p);
        long i = db.insert (TABLE_NAME , null, cv);
        return  i ;
    }

    public long insertUserFollow(String n , String p ){
        ContentValues cv = new ContentValues();
        cv.put(NUMBER_FOLLOW ,n );
        cv.put(PROFILE_PICTURE , p);
        long i = db.insert(TABLE_NAME_FOLLOW , null , cv);
        return i;

    }

    public long insertUserInvite(String n , String p ){
        ContentValues cv = new ContentValues();
        cv.put(NUMBER_INVITE ,n );
        cv.put(PROFILE_INVITE, p);
        long i = db.insert(TABLE_NAME_INSTALLED , null , cv);
        return i;

    }

    public ArrayList<String> InviteNumber(){
        invitenumber.clear();

        String getting_number = "SELECT number_invite FROM installed_user";
        Cursor c = db.rawQuery(getting_number , new String[] {});
        while (c.moveToNext()){
            String tempnumber = c.getString(c.getColumnIndex("number_invite"));
            invitenumber.add(tempnumber);

        }
        c.close();

        return invitenumber;

    }

    public ArrayList<String> FollowNumber(){
        follow_number.clear();
        String getting_number = "SELECT number_follow FROM databasetable_follow";
        Cursor c = db.rawQuery(getting_number , new String[] {});
        while (c.moveToNext()){
            String tempnumber = c.getString(c.getColumnIndex("number_follow"));
            follow_number.add(tempnumber);

        }
        c.close();
        return follow_number;
    }
    public void deleteTable(){
        db.execSQL("DELETE FROM " + TABLE_NAME);

    }
    public void selectUser(String n){

        String selectQuery = "SELECT number FROM follow_table WHERE username=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { n });
        if (c.moveToFirst()) {
        clicked_number = c.getString(c.getColumnIndex("number"));
            Log.e("number " , clicked_number);
        }
        c.close();
    }
    public String ReturnNumber(){
        return clicked_number;
    }


    public void Getting_Name(ArrayList<String> arrayList){
        names.clear();
        for (int i = 0 ; i < arrayList.size() ; i++) {
            String number = arrayList.get(i);
            String selecting_name = "SELECT username FROM follow_table WHERE number=? " ;

            Cursor c = db.rawQuery(selecting_name, new String[] { number });
            if (c.moveToFirst()) {
                String tempname = c.getString(c.getColumnIndex("username"));
              names.add(tempname);
            }
            c.close();
        }

        Log.e("names" ,  names.toString());
    }


    public void Getting_Name_Invite ( ArrayList<String> arrayList){

        follow_names.clear();
        for (int i = 0 ; i < arrayList.size() ; i++) {
            String number = arrayList.get(i);
            String selecting_name = "SELECT username FROM follow_table WHERE number=? " ;

            Cursor c = db.rawQuery(selecting_name, new String[] { number });
            if (c.moveToFirst()) {
                String tempname = c.getString(c.getColumnIndex("username"));
                follow_names.add(tempname);
            }
            c.close();
        }

        Log.e("follow_names", follow_names.toString());
    }

    public String follow_number_return(String number ){
       String name = "" ;
        String selectQuery = "SELECT number FROM follow_table WHERE username=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { number });
        if (c.moveToFirst()) {
            name = c.getString(c.getColumnIndex("number"));
            Log.e("number " , name);
        }
        c.close();

        return name ;
    }

    public ArrayList<String > InvitePics(){
        invitepics.clear();
        String getting_number = "SELECT profile_invite FROM installed_user";
        Cursor c = db.rawQuery(getting_number , new String[] {});
        while (c.moveToNext()){
            String tempnumber = c.getString(c.getColumnIndex("profile_invite"));
            invitepics.add(tempnumber);

        }
        c.close();
        return invitepics;
    }

    public ArrayList<String> Commonprofile(){
         databasepics.clear();
         invitepics.clear();
         followpics.clear();

        String getting_number = "SELECT profile_invite FROM installed_user";
        Cursor c = db.rawQuery(getting_number , new String[] {});
        while (c.moveToNext()){
            String tempnumber = c.getString(c.getColumnIndex("profile_invite"));
            invitepics.add(tempnumber);

        }
        c.close();

        String gettinpics = "SELECT profile FROM databasetable_follow";
        Cursor c1 = db.rawQuery(gettinpics , new String[] {});
        while (c1.moveToNext()){
            String tempnumber = c1.getString(c1.getColumnIndex("profile"));
            followpics.add(tempnumber);

        }
        c1.close();

      invitepics.removeAll(followpics);
        Log.e("invite", invitepics.toString());
        Log.e("followpiics", followpics.toString());
      return invitepics;


    }
    public  ArrayList<String> GettingnameFollowers(ArrayList<String> arrayList){

        follower_name.clear();
        for (int i = 0 ; i < arrayList.size() ; i++) {
            String number = arrayList.get(i);
            String selecting_name = "SELECT username FROM follow_table WHERE number=? " ;

            Cursor c = db.rawQuery(selecting_name, new String[] { number });
            if (c.moveToFirst()) {
                String tempname = c.getString(c.getColumnIndex("username"));
                follower_name.add(tempname);
            }
            c.close();
        }

        return follower_name;
    }

    public ArrayList<String> GettingNumbers(){
        numbers = new ArrayList<>();
        numbers.clear();
           String getting_number = "SELECT number FROM follow_table";
        Cursor c = db.rawQuery(getting_number , new String[] {});
        while (c.moveToNext()){
            String tempnumber = c.getString(c.getColumnIndex("number"));
            numbers.add(tempnumber);

        }
        Log.e("number " , numbers.toString());
        c.close();
        return numbers;
    }
    public ArrayList<String> GettingContactsName(){
        contacts_name.clear();
        String getting_number = "SELECT username FROM follow_table";
        Cursor c = db.rawQuery(getting_number , new String[] {});
        while (c.moveToNext()){
            String tempname = c.getString(c.getColumnIndex("username"));
            contacts_name.add(tempname);

        }
        c.close();
        Log.e("contact name" , contacts_name.toString());
        return contacts_name ;
    }

}
