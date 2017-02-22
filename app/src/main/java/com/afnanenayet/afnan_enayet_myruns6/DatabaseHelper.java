package com.afnanenayet.afnan_enayet_myruns6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Writes {@link Entry} objects to SQLite database
 */
class DatabaseHelper extends SQLiteOpenHelper {
    private final static String DEBUG_TAG = "DBHelper";
    private final static String TABLE_NAME = "MyRunsDB";
    private final static int TABLE_VERSION = 1;

    private final static String CREATE_TABLE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " ("
            + EntryDataSource.ActivityEntry.idColumn + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EntryDataSource.ActivityEntry.inputTypeColumn + " INTEGER NOT NULL, "
            + EntryDataSource.ActivityEntry.activityTypeColumn + " INTEGER NOT NULL, "
            + EntryDataSource.ActivityEntry.dateTimeColumn + " DATETIME NOT NULL, "
            + EntryDataSource.ActivityEntry.durationColumn + " INTEGER NOT NULL, "
            + EntryDataSource.ActivityEntry.distanceColumn + " FLOAT, "
            + EntryDataSource.ActivityEntry.avgPaceColumn + " FLOAT, "
            + EntryDataSource.ActivityEntry.avgSpeedColumn + " FLOAT, "
            + EntryDataSource.ActivityEntry.caloriesColumn + " INTEGER, "
            + EntryDataSource.ActivityEntry.climbColumn + " FLOAT, "
            + EntryDataSource.ActivityEntry.heartRateColumn + " INTEGER, "
            + EntryDataSource.ActivityEntry.commentColumn + " TEXT, "
            + EntryDataSource.ActivityEntry.privacyColumn + " INTEGER, "
            + EntryDataSource.ActivityEntry.gpsDataColumn + " BLOB );";

    DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, TABLE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Inserts an {@link Entry} object into the database
     *
     * @param entry The input {@link Entry} object
     * @return The ID of the entry
     */
    long insertEntry(Entry entry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = EntryDataSource.entryToValues(entry);
        return db.insert(TABLE_NAME, null, values);
    }

    // Remove an entry by giving its index
    public void removeEntry(long rowIndex) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,
                EntryDataSource.ActivityEntry.idColumn + "=" + rowIndex,
                null);
        Log.d(DEBUG_TAG, "Deleting entry " + rowIndex + " in db helper");
    }

    /**
     * Gets a particular entry by its index
     *
     * @param rowId The ID of the entry
     * @return Returns an {@link Entry} object
     */
    Entry fetchEntryByIndex(long rowId) {
        Log.d(DEBUG_TAG, "Reading entry");

        SQLiteDatabase db = getReadableDatabase();
        Entry returnEntry = null;

        Cursor cursor = db.query(TABLE_NAME,
                EntryDataSource.allColumns,
                EntryDataSource.ActivityEntry.idColumn + "=" + rowId,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            returnEntry = EntryDataSource.cursorToEntry(cursor);
        }

        cursor.close();
        return returnEntry;
    }

    /**
     * Retrievs all the {@link Entry} objects in the database
     *
     * @return A list of entries
     */
    ArrayList<Entry> fetchEntries() {
        Log.d(DEBUG_TAG, "Reading all entries");

        ArrayList<Entry> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                EntryDataSource.allColumns,
                null, null, null, null, null);
        cursor.moveToFirst();

        // Looping through and retrieving all entries in DB
        while (!cursor.isAfterLast()) {
            Entry entry = EntryDataSource.cursorToEntry(cursor);
            results.add(entry);
            cursor.moveToNext();
        }

        cursor.close();
        return results;
    }
}
