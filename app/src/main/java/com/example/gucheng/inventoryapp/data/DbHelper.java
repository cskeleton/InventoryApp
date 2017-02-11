package com.example.gucheng.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gucheng.inventoryapp.data.GcContract.GcEntry;

/**
 * Created by gucheng on 2017/1/24.
 */

class DbHelper extends SQLiteOpenHelper {

    private final static int TABLE_VERSION = 0;

    DbHelper(Context context) {
        super(context, GcEntry.TABLE_NAME, null, TABLE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + GcEntry.TABLE_NAME + "(" +
                GcEntry._ID + " INTEGER PRIMARY KEY," +
                GcEntry.COLUMN_GC_NAME + " TEXT NOT NULL," +
                GcEntry.COLUMN_GC_SALE + " INTEGER DEFAULT 0," +
                GcEntry.COLUMN_GC_STOCK + " INTEGER NOT NULL DEFAULT 0," +
                GcEntry.COLUMN_GC_PRICE + " INTEGER NOT NULL DEFAULT 0," +
                GcEntry.COLUMN_GC_IMAGE + " BLOB" + " );";
        db.execSQL(SQL_CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
