package com.mentormate.academy.fbpartyapp.Events;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mentormate.academy.fbpartyapp.Utils.Constants;

/**
 * Created by ivaylok on 2/15/2015.
 */
    public class DBHelper extends SQLiteOpenHelper {



    private final static String CREATE_TABLE_SQL = "" +
            "CREATE TABLE " + Constants.TABLE_NAME +
            " ( " +
                Constants.DB_ID  + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                Constants.DB_EVENT_ID + " BIGINT NOT NULL, " +
                Constants.DB_URL + " TEXT NOT NULL, " +
                Constants.DB_START_TIME + " TEXT, " +
                Constants.DB_NAME + " TEXT, " +
                Constants.DB_DESCRIPTION + " TEXT, " +
                Constants.DB_COVER + " TEXT, " +
                Constants.DB_LAT + " REAL, " +
                Constants.DB_LON + " REAL" +
            " );"
            ;

    public DBHelper(Context context) {
        super(context, Constants.DB_DBNAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Constants.LOG_DEBUG, "DB upraged from " + oldVersion + " to version " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(db);
    }


}
