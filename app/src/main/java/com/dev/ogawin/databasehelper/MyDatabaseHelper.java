package com.dev.ogawin.databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dev.ogawin.model.Rappel;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Rappel_Manager";

    // Table name: Rappel.
    private static final String TABLE_RAPPEL = "Rappel";

    private static final String COLUMN_RAPPEL_ID ="Rappel_Id";
    private static final String COLUMN_RAPPEL_TITLE ="Rappel_Title";
    private static final String COLUMN_RAPPEL_NOTES = "Rappel_Notes";
    private static final String COLUMN_RAPPEL_DATE = "Rappel_Date";
    private static final String COLUMN_RAPPEL_TIME = "Rappel_Time";
    private static final String COLUMN_RAPPEL_RECURRENCE = "Rappel_Recurrence";
    private static final String COLUMN_RAPPEL_DATE_FIN_RECURRENCE = "Rappel_Date_Fin_Recurrence";
    private static final String COLUMN_RAPPEL_PRIORITE = "Rappel_Priorite";
    private static final String COLUMN_RAPPEL_IDS_SOUS_TACHES = "Rappel_Id_SousTaches";
    private static final String COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS = "Rappel_Planned_Alarm_Time_In_Ms";

    private static MyDatabaseHelper instanceDb;

    public MyDatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static MyDatabaseHelper getInstanceDb(Context context)
    {
        if(instanceDb == null)
        {
            instanceDb = new MyDatabaseHelper(context);

            return  instanceDb;
        }
        return instanceDb;
    }
    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        // Script.
        String script = "CREATE TABLE " + TABLE_RAPPEL + "("
                + COLUMN_RAPPEL_ID + " INTEGER PRIMARY KEY," + COLUMN_RAPPEL_TITLE + " TEXT,"
                + COLUMN_RAPPEL_NOTES + " TEXT," + COLUMN_RAPPEL_DATE + " TEXT," + COLUMN_RAPPEL_TIME + " TEXT,"+ COLUMN_RAPPEL_RECURRENCE + " TEXT,"
                + COLUMN_RAPPEL_DATE_FIN_RECURRENCE + " TEXT," + COLUMN_RAPPEL_PRIORITE + " TEXT," + COLUMN_RAPPEL_IDS_SOUS_TACHES + " TEXT,"+
                COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS + " INTEGER" +")";
        // Execute Script.
        db.execSQL(script);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAPPEL);

        // Create tables again
        onCreate(db);
    }

    public void addRappel(Rappel rappel) {
        Log.i(TAG, "MyDatabaseHelper.addRappel ... " + rappel.getRappelTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RAPPEL_TITLE, rappel.getRappelTitle());
        values.put(COLUMN_RAPPEL_NOTES, rappel.getRappelNotes());

        if(rappel.getRappelDate() != null)
        {
            values.put(COLUMN_RAPPEL_DATE, rappel.getRappelDate().toString());
        }
        if(rappel.getRappelTime() != null)
        {
            values.put(COLUMN_RAPPEL_TIME, rappel.getRappelTime().toString());
        }

        values.put(COLUMN_RAPPEL_RECURRENCE, rappel.getRappelRecurrence());

        if(rappel.getRappelDateFinRecurrence() != null)
        {
            values.put(COLUMN_RAPPEL_DATE_FIN_RECURRENCE, rappel.getRappelDateFinRecurrence().toString());
        }

        values.put(COLUMN_RAPPEL_PRIORITE, rappel.getRappelPriorite());

        if(rappel.getRappelIdsSousTaches() != null)
        {
            values.put(COLUMN_RAPPEL_IDS_SOUS_TACHES, rappel.getRappelIdsSousTaches().toString());
        }

        if(rappel.getRappelPlannedAlarmTimeInMillis() != null)
        {
            values.put(COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS, rappel.getRappelPlannedAlarmTimeInMillis());
        }

        // Inserting Row
        db.insert(TABLE_RAPPEL, null, values);

        // Closing database connection
        db.close();
    }


    public Rappel getRappel(int id) {
        Log.i(TAG, "MyDatabaseHelper.getRappel ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RAPPEL, new String[] { COLUMN_RAPPEL_ID,
                        COLUMN_RAPPEL_TITLE, COLUMN_RAPPEL_NOTES, COLUMN_RAPPEL_DATE, COLUMN_RAPPEL_TIME,
                        COLUMN_RAPPEL_RECURRENCE, COLUMN_RAPPEL_DATE_FIN_RECURRENCE, COLUMN_RAPPEL_PRIORITE,
                        COLUMN_RAPPEL_IDS_SOUS_TACHES}, COLUMN_RAPPEL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Rappel rappel = new Rappel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), LocalDate.parse(cursor.getString(3)), LocalTime.parse(cursor.getString(4)), cursor.getString(5)
                , LocalDate.parse(cursor.getString(6)), cursor.getString(7), Arrays.asList(cursor.getString(8).split(",")));
        // return rappel
        return rappel;
    }


    public List<Rappel> getAllRappels() {
        Log.i(TAG, "MyDatabaseHelper.getAllRappels ... " );

        List<Rappel> rappelList = new ArrayList<Rappel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RAPPEL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Rappel rappel = new Rappel(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_TITLE)));
                rappel.setRappelId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_ID))));
                rappel.setRappelNotes(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_NOTES)));
                rappel.setRappelRecurrence(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_RECURRENCE)));
                rappel.setRappelPriorite(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_PRIORITE)));

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE)) != null)
                {
                    rappel.setRappelDate(LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE))));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_TIME)) != null)
                {
                    rappel.setRappelTime(LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_TIME))));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE_FIN_RECURRENCE)) != null)
                {
                    rappel.setRappelDateFinRecurrence(LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE_FIN_RECURRENCE))));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_IDS_SOUS_TACHES)) != null)
                {
                    rappel.setRappelIdsSousTaches(Arrays.asList(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_IDS_SOUS_TACHES)).split(",")));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS)) != null)
                {
                    rappel.setRappelPlannedAlarmTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS))));
                }

                // Adding rappel to list
                rappelList.add(rappel);
            } while (cursor.moveToNext());
        }

        // return rappel list
        return rappelList;
    }

    public List<Rappel> getTodayRappels() {
        Log.i(TAG, "MyDatabaseHelper.getTodayRappels ... " );

        List<Rappel> rappelList = new ArrayList<Rappel>();

        // Select Today rappel Query
        LocalDate now = LocalDate.now();
        String nowS = now.toString("yyyy-MM-dd");
        String selectQuery = "SELECT  * FROM " + TABLE_RAPPEL + " WHERE " + COLUMN_RAPPEL_DATE + " ='" + nowS +"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Rappel rappel = new Rappel(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_TITLE)));
                rappel.setRappelId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_ID))));
                rappel.setRappelNotes(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_NOTES)));
                rappel.setRappelRecurrence(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_RECURRENCE)));
                rappel.setRappelPriorite(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_PRIORITE)));

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE)) != null)
                {
                    rappel.setRappelDate(LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE))));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_TIME)) != null)
                {
                    rappel.setRappelTime(LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_TIME))));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE_FIN_RECURRENCE)) != null)
                {
                    rappel.setRappelDateFinRecurrence(LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE_FIN_RECURRENCE))));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_IDS_SOUS_TACHES)) != null)
                {
                    rappel.setRappelIdsSousTaches(Arrays.asList(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_IDS_SOUS_TACHES)).split(",")));
                }
                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS)) != null)
                {
                    rappel.setRappelPlannedAlarmTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS))));
                }

                // Adding rappel to list
                rappelList.add(rappel);
            } while (cursor.moveToNext());
        }

        // return rappel list
        return rappelList;
    }

    public int getRappelsCount() {
        Log.i(TAG, "MyDatabaseHelper.getRappelsCount ... " );

        String countQuery = "SELECT  * FROM " + TABLE_RAPPEL ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public int getRappelsCountToday() {
        Log.i(TAG, "MyDatabaseHelper.getRappelsCountToday ... " );

        // Select Today rappel Query
        LocalDate now = LocalDate.now();
        String nowS = now.toString("yyyy-MM-dd");

        String countQuery = "SELECT  * FROM " + TABLE_RAPPEL + " WHERE " + COLUMN_RAPPEL_DATE + " ='" + nowS +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public int updateRappel(Rappel rappel) {
        Log.i(TAG, "MyDatabaseHelper.updateRappel ... "  + rappel.getRappelTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RAPPEL_TITLE, rappel.getRappelTitle());
        values.put(COLUMN_RAPPEL_NOTES, rappel.getRappelNotes());

        if(rappel.getRappelDate() != null)
        {
            values.put(COLUMN_RAPPEL_DATE, rappel.getRappelDate().toString());
        }
        if(rappel.getRappelTime() != null)
        {
            values.put(COLUMN_RAPPEL_TIME, rappel.getRappelTime().toString());
        }

        values.put(COLUMN_RAPPEL_RECURRENCE, rappel.getRappelRecurrence());

        if(rappel.getRappelDateFinRecurrence() != null)
        {
            values.put(COLUMN_RAPPEL_DATE_FIN_RECURRENCE, rappel.getRappelDateFinRecurrence().toString());
        }

        values.put(COLUMN_RAPPEL_PRIORITE, rappel.getRappelPriorite());

        if(rappel.getRappelIdsSousTaches() != null)
        {
            values.put(COLUMN_RAPPEL_IDS_SOUS_TACHES, rappel.getRappelIdsSousTaches().toString());
        }


        if(rappel.getRappelPlannedAlarmTimeInMillis() != null)
        {
            values.put(COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS, rappel.getRappelPlannedAlarmTimeInMillis());
        }

        // updating row
        return db.update(TABLE_RAPPEL, values, COLUMN_RAPPEL_ID + " = ?",
                new String[]{String.valueOf(rappel.getRappelId())});
    }

    public List<Rappel> SearchAllRappelsByKeyword(String keyword) {
        Log.i(TAG, "MyDatabaseHelper.SearchAllRappelsByKeyword ... " );

        List<Rappel> rappelList = new ArrayList<Rappel>();

        // Select Query to find all tasks having the keyword in title or notes

        String selectQuery = "SELECT  * FROM " + TABLE_RAPPEL + " WHERE " + COLUMN_RAPPEL_TITLE + " LIKE '%" + keyword +"%'"
                + " OR " + COLUMN_RAPPEL_NOTES + " LIKE '%" + keyword +"%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Rappel rappel = new Rappel(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_TITLE)));
                rappel.setRappelId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_ID))));
                rappel.setRappelNotes(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_NOTES)));
                rappel.setRappelRecurrence(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_RECURRENCE)));
                rappel.setRappelPriorite(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_PRIORITE)));

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE)) != null)
                {
                    rappel.setRappelDate(LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE))));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_TIME)) != null)
                {
                    rappel.setRappelTime(LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_TIME))));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE_FIN_RECURRENCE)) != null)
                {
                    rappel.setRappelDateFinRecurrence(LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_DATE_FIN_RECURRENCE))));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_IDS_SOUS_TACHES)) != null)
                {
                    rappel.setRappelIdsSousTaches(Arrays.asList(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_IDS_SOUS_TACHES)).split(",")));
                }

                if(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS)) != null)
                {
                    rappel.setRappelPlannedAlarmTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_RAPPEL_PLANNED_ALARM_TIME_IN_MS))));
                }

                // Adding rappel to list
                rappelList.add(rappel);
            } while (cursor.moveToNext());
        }

        // return rappel list
        return rappelList;
    }

    public void deleteRappel(Rappel rappel) {
        Log.i(TAG, "MyDatabaseHelper.deleteRappel ... " + rappel.getRappelTitle() );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RAPPEL, COLUMN_RAPPEL_ID + " = ?",
                new String[] { String.valueOf(rappel.getRappelId()) });
        db.close();
    }

    public long getMaxRappelId()
    {
        long ret = 0;
        Log.i(TAG, "MyDatabaseHelper.getMaxRappelId ... " );

        String query = "SELECT  MAX(" + COLUMN_RAPPEL_ID+  ") FROM " + TABLE_RAPPEL ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {

            ret = cursor.getInt(0);
        }


        cursor.close();

        return  ret;
    }

}
