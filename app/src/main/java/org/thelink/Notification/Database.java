package org.thelink.Notification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Mayank on 30-07-2016.
 */
public class Database extends SQLiteOpenHelper {
    public static final String TB="friends";
    public static final String DB="mydatabase.db";
    public static final String COL1="NAME";
    public static final String COL2="NUMBER";
    public static final String COL3="PROFILE";
    public static final String COL4="STATUS";
    private Context context;
    public Database(Context context) {
        super(context, DB, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table " + TB + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,NUMBER TEXT UNIQUE,PROFILE TEXT,STATUS INTEGER) ";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS" + TB);
        onCreate(db);
    }

    public void new_request(String name,String number,int status){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " +TB+ " WHERE NUMBER = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{number});
        if(cursor != null){
            return;

        }
        cursor.close();

    }

    public void entry(String name,String number,String profile,int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(COL1,name);
        cv.put(COL2,number);
        cv.put(COL3, profile);
        cv.put(COL4, status);
        db.insert(TB, null, cv);
    }

    public boolean update_status (String number,int status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL3,status);
        db.update(TB, contentValues, "NUMBER = ? ", new String[]{number});
        return true;
    }
    public void delete_request (String number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TB, "NUMBER = ? ", new String[]{number});
    }

    public Cursor getall() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TB, null);
        return res;
    }

}