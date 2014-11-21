package com.test.swivl.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BeansDBOpenHelper extends SQLiteOpenHelper {
    public static final String BEANS_TABLE_NAME = "Beans";

    private static final String DATABASE_NAME = "application_data.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_BEANS_TABLE_QUERY = "CREATE TABLE [" + BEANS_TABLE_NAME +"] (" +
            BeansDBAdapter.ID_COLUMN_NAME + " INTEGER NOT NULL PRIMARY KEY, " +
            BeansDBAdapter.LOGIN_COLUMN_NAME + " VARCHAR2(255) NOT NULL, " +
            BeansDBAdapter.HTML_URL_COLUMN_NAME + " VARCHAR2(255) NOT NULL, " +
            BeansDBAdapter.AVATAR_COLUMN_NAME + " BLOB)";


    public BeansDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_BEANS_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.i(BeansDBOpenHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion +
                        ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + BEANS_TABLE_NAME + ";");
        onCreate(database);
    }
}

