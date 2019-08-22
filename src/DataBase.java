package com.example.user.wifiservice;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper {

    public DataBase(Context context) {
        super(context, "DataBase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS settings (task INTEGER)");

        // init
        db.execSQL("INSERT INTO settings (task) VALUES (0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS settings");
        onCreate(db);
    }

    boolean setTask(boolean value) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.execSQL("UPDATE settings SET task = '" + (value ? 1 : 0) + "'");
            return true;
        } catch (Exception e) {
            Log.d("DataBase", e.getMessage());
        }

        return false;
    }

    boolean getTask() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT task FROM settings", null);

        int status = 0;

        if (cursor.moveToFirst()) {

            try {
                status = Integer.parseInt(cursor.getString(0));
            } catch (Exception e){
                Log.d("DataBase", e.getMessage());
            }
        }

        return (status == 0 ? false : true);
    }
}
