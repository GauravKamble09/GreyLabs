package com.greylabs.greylabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.greylabs.greylabs.model.LocationObject;

import java.util.ArrayList;

/**
 * Created by gaura on 09-06-2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LocationManager";

    // Contacts table name
    private static final String TABLE_LOCATIONS = "locations";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ADDRESS + " TEXT,"
                + KEY_LAT + " DOUBLE,"+ KEY_LON + " DOUBLE" + ")";
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addLocation(LocationObject locationObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, locationObject.getAddress());
        values.put(KEY_LAT, locationObject.getLatitude());
        values.put(KEY_LON, locationObject.getLongitude());

        // Inserting Row
        db.insert(TABLE_LOCATIONS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public LocationObject getLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOCATIONS, new String[] { KEY_ID,
                        KEY_ADDRESS, KEY_LAT, KEY_LON }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        LocationObject locationObject = new LocationObject(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3));
        // return contact
        return locationObject;
    }

    // Getting All Contacts
    public ArrayList<LocationObject> getAllLocations() {
        ArrayList<LocationObject> locationObjectArrayList = new ArrayList<LocationObject>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocationObject locationObject = new LocationObject();
                locationObject.setId(Integer.parseInt(cursor.getString(0)));
                locationObject.setAddress(cursor.getString(1));
                locationObject.setLatitude(cursor.getDouble(2));
                locationObject.setLongitude(cursor.getDouble(3));

                locationObjectArrayList.add(locationObject);
            } while (cursor.moveToNext());
        }

        // return contact list
        return locationObjectArrayList;
    }

    // Updating single contact
    public int updateLocation(LocationObject locationObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, locationObject.getAddress());
        values.put(KEY_LAT, locationObject.getLatitude());
        values.put(KEY_LON, locationObject.getLongitude());

        // updating row
        return db.update(TABLE_LOCATIONS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(locationObject.getId()) });
    }

    // Deleting single contact
    public void deleteLocation(LocationObject locationObject) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, KEY_ID + " = ?",
                new String[] { String.valueOf(locationObject.getId()) });
        db.close();
    }


    // Getting contacts Count
    public int getLocationsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
