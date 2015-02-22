package com.mentormate.academy.fbpartyapp.Events;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.mentormate.academy.fbpartyapp.Utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ivaylok on 02/15/2015.
 */
public class EventsProvider extends ContentProvider {

    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    // projection map for a query
    private static HashMap<String, String> EventMap;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();

        return (db != null);
    }

    // maps content URI "patterns" to the integer values that were set above
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.DB_DBNAME, 1); //Sets the integer value for multiple rows in events to 1. Notice that no wildcard is used in the path
        /*
         * Sets the code for a single row to 2. In this case, the "#" wildcard is
         * used. "content://com.mentormate.academy.fbpartyapp.Events.EventsProvider/events/3" matches, but
         * "content://com.mentormate.academy.fbpartyapp.Events.EventsProvider/events doesn't.
         */
        uriMatcher.addURI(Constants.AUTHORITY, Constants.DB_DBNAME + "/#", 2);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.DB_DBNAME + "/event/#", 3);
        uriMatcher.addURI(Constants.AUTHORITY, Constants.DB_DBNAME + "/events_today/", 4);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Constants.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            // maps all database column names
            case 1:
                /*queryBuilder.setProjectionMap(EventMap);
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                Date date = formatter.parse(testDate);
                System.out.println(date);*/

                queryBuilder.appendWhere( "DATE(" + Constants.DB_START_TIME + ")!='" + formatter.format(new Date())+"'");
                break;
            case 2:
                queryBuilder.appendWhere( Constants.DB_ID + "=" + uri.getLastPathSegment());
                break;
            case 3:
                queryBuilder.appendWhere( Constants.DB_EVENT_ID + "=" + uri.getLastPathSegment());
                break;
            case 4:
                queryBuilder.appendWhere( "DATE(" + Constants.DB_START_TIME + ")='" + formatter.format(new Date())+"'");
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if(sortOrder == null || sortOrder == "")
        {
            sortOrder = Constants.DB_ID;
        }

        if ( sortOrder.equals("") )
        {
            sortOrder = Constants.DB_ID + " DESC";
        }

        Cursor cursor = queryBuilder.query(db, projection, selection , selectionArgs, null, null, sortOrder);
        //Log.d(Constants.LOG_DEBUG, "queryBuilder query: " + queryBuilder.buildQuery(projection, selection, "", null, sortOrder, null));
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long row = db.insert(Constants.TABLE_NAME, "", values);

        Uri responseUri = ContentUris.withAppendedId(Constants.URI, row);
        getContext().getContentResolver().notifyChange(responseUri, null);
        return responseUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)){
            case 1:
                // delete all the records of the table
                count = db.delete(Constants.TABLE_NAME, selection, selectionArgs);
                break;
            case 2:
                String id = uri.getLastPathSegment();	//gets the id
                count = db.delete( Constants.TABLE_NAME, Constants.DB_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case 3:
                String event_id = uri.getLastPathSegment();	//gets the id
                count = db.delete( Constants.TABLE_NAME, Constants.DB_EVENT_ID + " = " + event_id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsCount = 0;

        switch (uriMatcher.match(uri)){
            case 1:
                rowsCount = db.update(Constants.TABLE_NAME, values, selection, selectionArgs);
                break;
            case 2:
                rowsCount = db.update(Constants.TABLE_NAME, values,
                        Constants.DB_ID + " = " + uri.getLastPathSegment()
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case 3:
                rowsCount = db.update(Constants.TABLE_NAME, values,
                        Constants.DB_EVENT_ID + " = " + uri.getLastPathSegment()
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsCount;
    }
}
