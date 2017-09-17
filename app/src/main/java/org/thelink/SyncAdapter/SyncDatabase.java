package org.thelink.SyncAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Mayank on 27-07-2016.
 */
public class SyncDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=3;
    private static final String DATABASE_NAME = "playlist_database";
    public ArrayList<String> SyncAdapterSongs = new ArrayList<>();
    public ArrayList<String> SyncAdapterPaths =  new ArrayList<>();
   // static String clicked_number ;
    SQLiteDatabase db ;
    Context mcontext ;

    static public String TABLE_NAME= "playlist_table" ,
            UID = "uid " ,
            SONG_NAME = "song_name " ,
            SONG_PATH= "song_path" ,
            PLAYLIST_NAME;

    static String CREATE_TABLE = " CREATE TABLE  " + TABLE_NAME + " ( " +  UID + "  INTEGER PRIMARY KEY  " + "  AUTOINCREMENT  , "
            +  SONG_NAME + " TEXT unique  "  + "  ,  " + SONG_PATH + " TEXT unique  "  +  "  , " + PLAYLIST_NAME + "TEXT unique " +  " ); ";


    public SyncDatabase(Context mcontext ){
        super(mcontext, DATABASE_NAME, null, DATABASE_VERSION);
         this.mcontext = mcontext;
        db = getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        Toast.makeText(mcontext, "ON UPGRADE " + oldVersion + " " + newVersion, Toast.LENGTH_LONG).show();
        onCreate(db);
    }
    public void insertSongs ( String n , String p , String playlist){
        ContentValues cv = new ContentValues();
        cv.put(SONG_NAME, n);
        cv.put(SONG_PATH, p);
        cv.put(PLAYLIST_NAME , playlist);
        db.insert(TABLE_NAME, null, cv);

    }



    public void AllTable(){
        String arg[] = {SONG_NAME , SONG_PATH};
        Cursor cr = db.query(TABLE_NAME, arg, null, null, null, null, null);
        while (cr.moveToNext()){
            String n = cr.getString(0);
            String p = cr.getString(1);
            Log.e("whole table", n + "  " + p);
        }

    }


    public void GettingAllSongs() {
        String arg[] = {SONG_NAME, SONG_PATH};
        Cursor cr = db.query(TABLE_NAME, arg, null, null, null, null, null);
        while (cr.moveToNext()) {
            String n = cr.getString(0);
            String p = cr.getString(1);
            SyncAdapterSongs.add(n);
            SyncAdapterPaths.add(p);

        }
        Log.e("s", SyncAdapterPaths.toString() + "   " + SyncAdapterSongs.toString());

    }
    public void DeletingSong(String n ){
        db.delete(TABLE_NAME, SONG_NAME + "= ' " + n + " ' ", null) ;


    }

}
