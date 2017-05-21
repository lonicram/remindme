package com.medi.marcin.medicalendar;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class ProfileEntry implements BaseColumns {
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_MOBILE_PHONE = "mobile_phone";
        public static final String COLUMN_NAME_DATE_OF_BIRTH = "date_of_birth";
    }
    public static class ReminderEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_PROFILE_ID = "profile_id";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +
                    ProfileEntry._ID + " INTEGER PRIMARY KEY," +
                    ProfileEntry.COLUMN_NAME_USERNAME + " TEXT UNIQUE," +
                    ProfileEntry.COLUMN_NAME_FIRST_NAME + " TEXT," +
                    ProfileEntry.COLUMN_NAME_LAST_NAME + " TEXT," +
                    ProfileEntry.COLUMN_NAME_MOBILE_PHONE + " TEXT," +
                    ProfileEntry.COLUMN_NAME_DATE_OF_BIRTH + " DATE);" +
            "CREATE TABLE " + ReminderEntry.TABLE_NAME + " (" +
                    ReminderEntry._ID + " INTEGER PRIMARY KEY," +
                    ReminderEntry.COLUMN_COMMENT + " TEXT," +
                    ReminderEntry.COLUMN_DATE + " TEXT," +
                    ReminderEntry.COLUMN_TIME + " TEXT," +
                    ReminderEntry.COLUMN_TITLE + " TEXT," +
                    ReminderEntry.COLUMN_PROFILE_ID + " INTEGER);" +
                    " FOREIGN KEY ("+ ReminderEntry.COLUMN_PROFILE_ID +") " +
                    "REFERENCES "+ ProfileEntry.TABLE_NAME +"("+ ProfileEntry.COLUMN_NAME_USERNAME +")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME;

    public static class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "EntriesReader.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // No migrations mechanism implemented, delete and create database
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    /**
     * Get writable DB
     * @param context
     * @return
     */
    private static SQLiteDatabase getWritableDb(Context context){
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        return  mDbHelper.getWritableDatabase();
    }

    /**
     * Get readable DB
     * @param context
     * @return
     */
    private static SQLiteDatabase getReadableDb(Context context){
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        return  mDbHelper.getReadableDatabase();
    }

    /**
     * Add Profile to DB
     * @param context
     * @param firstName
     * @param lastName
     * @param dateOfBirth
     * @param mobilePhone
     */
    public static void addProfile(
            Context context,
            String firstName,
            String lastName,
            String dateOfBirth,
            String mobilePhone
    ){
        SQLiteDatabase db = getWritableDb(context);
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ProfileEntry.COLUMN_NAME_USERNAME, firstName.charAt(0) + lastName);
        values.put(ProfileEntry.COLUMN_NAME_FIRST_NAME, firstName);
        values.put(ProfileEntry.COLUMN_NAME_LAST_NAME, lastName);
        values.put(ProfileEntry.COLUMN_NAME_DATE_OF_BIRTH, dateOfBirth);
        values.put(ProfileEntry.COLUMN_NAME_MOBILE_PHONE, mobilePhone);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(ProfileEntry.TABLE_NAME, null, values);
    }

    public static List listUsernames(Context context) {
        SQLiteDatabase db = getReadableDb(context);
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProfileEntry._ID,
                ProfileEntry.COLUMN_NAME_FIRST_NAME,
                ProfileEntry.COLUMN_NAME_LAST_NAME,
                ProfileEntry.COLUMN_NAME_DATE_OF_BIRTH,
                ProfileEntry.COLUMN_NAME_USERNAME,
                ProfileEntry.COLUMN_NAME_MOBILE_PHONE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ProfileEntry.COLUMN_NAME_USERNAME + " DESC";

        Cursor cursor = db.query(
                ProfileEntry.TABLE_NAME,                  // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List itemUsernames = new ArrayList<>();
        while (cursor.moveToNext()) {
            String username = cursor.getString(
                    cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_USERNAME));
            itemUsernames.add(username);
        }
        cursor.close();
        return itemUsernames;
    }

    public static Hashtable<String, String> getUserInfo(Context context, String username) {
        SQLiteDatabase db = getReadableDb(context);
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProfileEntry._ID,
                ProfileEntry.COLUMN_NAME_FIRST_NAME,
                ProfileEntry.COLUMN_NAME_LAST_NAME,
                ProfileEntry.COLUMN_NAME_DATE_OF_BIRTH,
                ProfileEntry.COLUMN_NAME_USERNAME,
                ProfileEntry.COLUMN_NAME_MOBILE_PHONE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ProfileEntry.COLUMN_NAME_USERNAME + " DESC";
        String[] where_criteria = {username};

        Cursor cursor = db.query(
                ProfileEntry.TABLE_NAME,                      // The table to query
                projection,                                   // The columns to return
                ProfileEntry.COLUMN_NAME_USERNAME + " = ? ",  // The columns for the WHERE clause
                where_criteria,                               // The values for the WHERE clause
                null,                                         // don't group the rows
                null,                                         // don't filter by row groups
                sortOrder                                     // The sort order
        );

        Hashtable<String, String> userData = new Hashtable<>();
        while (cursor.moveToNext()) {
            userData.put(
                    "username", cursor.getString(
                        cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_USERNAME))
            );
            userData.put(
                    "firstName", cursor.getString(
                            cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_FIRST_NAME))
            );
            userData.put(
                    "lastName", cursor.getString(
                    cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_LAST_NAME))
            );
            userData.put(
                    "mobilePhone", cursor.getString(
                    cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_MOBILE_PHONE))
            );
            userData.put(
                    "dateOfBirth", cursor.getString(
                            cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_DATE_OF_BIRTH))
            );

        }
        cursor.close();
        return userData;
    }

    public static int updateProfile(
            Context context,
            String username,
            String firstName,
            String lastName,
            String dateOfBirth,
            String mobilePhone
    ){
        SQLiteDatabase db = getWritableDb(context);
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ProfileEntry.COLUMN_NAME_FIRST_NAME, firstName);
        values.put(ProfileEntry.COLUMN_NAME_LAST_NAME, lastName);
        values.put(ProfileEntry.COLUMN_NAME_DATE_OF_BIRTH, dateOfBirth);
        values.put(ProfileEntry.COLUMN_NAME_MOBILE_PHONE, mobilePhone);
        String[] whereArgs = {username};
        // Insert the new row, returning the primary key value of the new row
        int newRowId = db.update(ProfileEntry.TABLE_NAME, values, "username=?", whereArgs);
        return newRowId;
    }

    public static boolean deleteProfileInDatabase(Context context, String username){
        SQLiteDatabase db = getWritableDb(context);
        String[] whereArgs = {username};
        return db.delete(ProfileEntry.TABLE_NAME, "username = ?", whereArgs) > 0;
    }


    // reminders part
    // TODO: consider moving it to separate file

    /**
     * Add Entry to DB
     */
    public static void addReminder(
            Context context,
            String comment,
            String date,
            String time,
            String title,
            String username
    ){
        SQLiteDatabase db = getWritableDb(context);
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ReminderEntry.COLUMN_COMMENT, comment);
        values.put(ReminderEntry.COLUMN_DATE, date);
        values.put(ReminderEntry.COLUMN_TIME, time);
        values.put(ReminderEntry.COLUMN_TITLE, title);
        values.put(ReminderEntry.COLUMN_PROFILE_ID, username);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(ReminderEntry.TABLE_NAME, null, values);
    }

    public static List listReminders(Context context) {
        SQLiteDatabase db = getReadableDb(context);
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ReminderEntry._ID,
                ReminderEntry.COLUMN_TITLE,
                ReminderEntry.COLUMN_COMMENT,
                ReminderEntry.COLUMN_DATE,
                ReminderEntry.COLUMN_TIME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ReminderEntry.COLUMN_TITLE + " DESC";

        Cursor cursor = db.query(
                ReminderEntry.TABLE_NAME,                  // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List items = new ArrayList<>();
        while (cursor.moveToNext()) {
            String username = cursor.getString(
                    cursor.getColumnIndexOrThrow(ReminderEntry.COLUMN_TITLE));
            items.add(username);
        }
        cursor.close();
        return items;
    }

    public static Hashtable<String, String> getReminderInfo(Context context, String reminder_id) {
        SQLiteDatabase db = getReadableDb(context);
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ReminderEntry._ID,
                ReminderEntry.COLUMN_COMMENT,
                ReminderEntry.COLUMN_DATE,
                ReminderEntry.COLUMN_TIME,
                ReminderEntry.COLUMN_TITLE,
                ReminderEntry.COLUMN_PROFILE_ID
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ReminderEntry.COLUMN_TITLE + " DESC";
        String[] where_criteria = {reminder_id};

        Cursor cursor = db.query(
                ReminderEntry.TABLE_NAME,                      // The table to query
                projection,                                   // The columns to return
                ReminderEntry._ID + " = ? ",  // The columns for the WHERE clause
                where_criteria,                               // The values for the WHERE clause
                null,                                         // don't group the rows
                null,                                         // don't filter by row groups
                sortOrder                                     // The sort order
        );

        Hashtable<String, String> reminderData = new Hashtable<>();
        while (cursor.moveToNext()) {
            reminderData.put(
                    "title", cursor.getString(
                            cursor.getColumnIndexOrThrow(ReminderEntry.COLUMN_TITLE))
            );
            reminderData.put(
                    "comment", cursor.getString(
                            cursor.getColumnIndexOrThrow(ReminderEntry.COLUMN_COMMENT))
            );
            reminderData.put(
                    "date", cursor.getString(
                            cursor.getColumnIndexOrThrow(ReminderEntry.COLUMN_DATE))
            );
            reminderData.put(
                    "time", cursor.getString(
                            cursor.getColumnIndexOrThrow(ReminderEntry.COLUMN_TIME))
            );
            reminderData.put(
                    "id", cursor.getString(
                            cursor.getColumnIndexOrThrow(ReminderEntry._ID))
            );

        }
        cursor.close();
        return reminderData;
    }

    public static int updateReminder(
            Context context,
            String comment,
            String date,
            String time,
            String title,
            Integer id
    ){
        SQLiteDatabase db = getWritableDb(context);
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ReminderEntry.COLUMN_COMMENT, comment);
        values.put(ReminderEntry.COLUMN_DATE, date);
        values.put(ReminderEntry.COLUMN_TIME, time);
        values.put(ReminderEntry.COLUMN_TITLE, title);

        String[] whereArgs = {Integer.toString(id)};
        // Insert the new row, returning the primary key value of the new row
        int newRowId = db.update(ProfileEntry.TABLE_NAME, values, "_id=?", whereArgs);
        return newRowId;
    }

    public static boolean deleteReminder(Context context, String id){
        SQLiteDatabase db = getWritableDb(context);
        String[] whereArgs = {id};
        return db.delete(ProfileEntry.TABLE_NAME, "_id = ?", whereArgs) > 0;
    }
}


