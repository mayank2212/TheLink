package org.thelink.Profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayank on 16-03-2017.
 */
public class newDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "newMyDB.db";
    public static final String TABLE_NAME = "Playlist";
    public static final String COL1 = "ID";
    public static final String COL2 = "SONG_NAME";
    public static final String COL3 = "PATH";
    public static final String COL4 = "PLAYLIST_NAME";
    public static final String COL5 = "STATUS";
    public static final String COL6 = "ARTIST_NAME";

    public newDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "create table " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT,SONG_NAME TEXT,PATH TEXT,PLAYLIST_NAME TEXT,STATUS INTEGER , ARTIST_NAME TEXT)";

        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_ENTRIES ="DROP TABLE IF EXISTS" + TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);

    }

    public void entry(String name,String path,String playlist,int status , String artist){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(COL2,name);
        cv.put(COL3, path);
        cv.put(COL4, playlist);
        cv.put(COL5, status);
        db.insert(TABLE_NAME, null, cv);
    }


    public Cursor getall() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public void Delete_Row(String name,String playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL2 + " = ? AND " + COL4 + " = ?",
                new String[]{name, playlist});
        db.close();
    }

}
